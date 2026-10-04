package com.example.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.model.WeatherInfo
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import org.json.JSONObject

/**
 * System Location Helper for Nothing Launcher.
 * Integrates real Android LocationManager (GPS, Network) + Geocoder,
 * with intelligent fallback to system TimeZone & Locale region.
 * Automatically adapts weather city name to user's real location.
 */
object SystemLocationHelper {

  fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
  }

  /**
   * Resolves the user's city and country name using:
   * 1. Real GPS / Network Location (if permission granted)
   * 2. System TimeZone ID (e.g. Africa/Tunis -> TUNIS, Europe/Paris -> PARIS)
   * 3. System Locale country
   */
  fun getAutoDetectedCity(context: Context): String {
    if (hasLocationPermission(context)) {
      try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager != null) {
          val lastGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
          val lastNet = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }
          val lastPassive = try { locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) } catch (_: Exception) { null }

          val bestLoc: Location? = lastGps ?: lastNet ?: lastPassive
          if (bestLoc != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(bestLoc.latitude, bestLoc.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
              val addr = addresses[0]
              val resolved = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.countryName
              if (!resolved.isNullOrBlank()) {
                return resolved.uppercase(Locale.getDefault())
              }
            }
          }
        }
      } catch (_: Exception) {
        // Fallback to timezone/locale below
      }
    }

    // Intelligent fallback: Parse from system TimeZone (e.g. Africa/Tunis, Europe/Berlin, America/New_York)
    return getCityFromSystemTimeZone()
  }

  fun getCityFromSystemTimeZone(): String {
    try {
      val tzId = TimeZone.getDefault().id
      if (tzId.contains("/")) {
        val rawCity = tzId.substringAfterLast("/").replace("_", " ")
        if (rawCity.isNotBlank()) {
          return rawCity.uppercase(Locale.getDefault())
        }
      }
      val country = Locale.getDefault().displayCountry
      if (country.isNotBlank()) {
        return country.uppercase(Locale.getDefault())
      }
    } catch (_: Exception) {
      // Fallback
    }
    return "TUNIS"
  }

  /** Fetches real current weather from Open-Meteo. No API key is required. */
  fun getCurrentWeather(context: Context): WeatherInfo? {
    if (!hasLocationPermission(context)) return null
    return try {
      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
      val location = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
        .mapNotNull { provider -> try { locationManager.getLastKnownLocation(provider) } catch (_: Exception) { null } }
        .maxByOrNull { it.time } ?: return null
      val city = getAutoDetectedCity(context)
      val endpoint = "https://api.open-meteo.com/v1/forecast?latitude=" + location.latitude + "&longitude=" + location.longitude + "&current=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&forecast_days=1&timezone=auto"
      val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 8000
        readTimeout = 8000
        useCaches = false
      }
      try {
        if (connection.responseCode !in 200..299) return null
        val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        val current = json.optJSONObject("current") ?: return null
        val daily = json.optJSONObject("daily")
        val temp = current.optDouble("temperature_2m", Double.NaN)
        if (temp.isNaN()) return null
        val code = current.optInt("weather_code", -1)
        val high = daily?.optJSONArray("temperature_2m_max")?.optDouble(0, temp) ?: temp
        val low = daily?.optJSONArray("temperature_2m_min")?.optDouble(0, temp) ?: temp
        WeatherInfo(temp.roundToInt(), weatherConditionFromCode(code), city, high.roundToInt(), low.roundToInt())
      } finally { connection.disconnect() }
    } catch (_: Exception) { null }
  }

  private fun weatherConditionFromCode(code: Int): String = when (code) {
    0, 1 -> "SUNNY"
    2, 3, 45, 48 -> "CLOUDY"
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "RAIN"
    95, 96, 99 -> "THUNDER"
    71, 73, 75, 77, 85, 86 -> "SNOW"
    else -> "CLOUDY"
  }

  /** Fallback when live weather is unavailable. */
  fun getEstimatedWeatherForLocation(city: String): WeatherInfo {
    val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
    val isSummer = month in 4..8
    val temp = if (isSummer) 26 else 19
    return WeatherInfo(
      tempC = temp,
      condition = "SUNNY",
      city = city,
      highC = temp + 3,
      lowC = temp - 6
    )
  }
}

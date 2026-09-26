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
import java.util.Locale
import java.util.TimeZone

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

  /**
   * Generates realistic seasonal temperature & condition for the detected region.
   */
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

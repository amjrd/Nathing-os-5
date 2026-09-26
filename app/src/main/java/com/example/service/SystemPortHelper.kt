package com.example.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.widget.Toast

/**
 * Helper to handle integration with Pixel / Modern Android / Android 17 application ports,
 * such as Pixel Weather, Google Clock, Google Calendar, and Health Connect.
 */
object SystemPortHelper {

  // 1. Pixel Weather Port / Google Weather Port
  fun launchPixelWeather(context: Context): Boolean {
    val pm = context.packageManager

    // Target 1: Standalone Pixel Weather port (Pixel 9 / Android 15/16/17 app)
    val pixelWeatherPackages = listOf(
      "com.google.android.apps.weather",
      "com.nothing.weather",
      "com.google.android.weather"
    )

    for (pkg in pixelWeatherPackages) {
      try {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(launchIntent)
          Toast.makeText(context, "Opening Weather Port ($pkg)", Toast.LENGTH_SHORT).show()
          return true
        }
      } catch (_: Exception) {}
    }

    // Target 2: Google App (GSA) Dynact Weather Activity
    try {
      val gsaIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("dynact://velour/weather/ProxyActivity")
        setPackage("com.google.android.googlequicksearchbox")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      if (gsaIntent.resolveActivity(pm) != null) {
        context.startActivity(gsaIntent)
        Toast.makeText(context, "Opening Google Weather", Toast.LENGTH_SHORT).show()
        return true
      }
    } catch (_: Exception) {}

    // Target 3: Generic Android Weather / Browser fallback
    try {
      val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=weather")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(webIntent)
      return true
    } catch (_: Exception) {
      Toast.makeText(context, "No weather app found", Toast.LENGTH_SHORT).show()
      return false
    }
  }

  // 2. Pixel Clock / Google Clock Port
  fun launchPixelClock(context: Context): Boolean {
    val pm = context.packageManager

    // Target 1: Google Pixel Deskclock
    val clockPackages = listOf(
      "com.google.android.deskclock",
      "com.android.deskclock",
      "com.sec.android.app.clockpackage",
      "com.oneplus.deskclock"
    )

    for (pkg in clockPackages) {
      try {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(launchIntent)
          return true
        }
      } catch (_: Exception) {}
    }

    // Target 2: Standard Android Alarm Intent
    try {
      val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(alarmIntent)
      return true
    } catch (_: Exception) {
      Toast.makeText(context, "Opening system clock", Toast.LENGTH_SHORT).show()
      return false
    }
  }

  // 3. Pixel Calendar Port
  fun launchPixelCalendar(context: Context): Boolean {
    val pm = context.packageManager
    val calendarPackages = listOf(
      "com.google.android.calendar",
      "com.android.calendar",
      "com.samsung.android.calendar"
    )

    for (pkg in calendarPackages) {
      try {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(launchIntent)
          return true
        }
      } catch (_: Exception) {}
    }

    try {
      val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_APP_CALENDAR)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
      return true
    } catch (_: Exception) {
      return false
    }
  }

  // 4. Android 17 / Health Connect Port
  fun launchHealthConnect(context: Context): Boolean {
    val pm = context.packageManager
    val healthPackages = listOf(
      "com.google.android.apps.healthdata", // Android Health Connect
      "com.google.android.apps.fitness",    // Google Fit
      "com.samsung.android.app.shealth"     // Samsung Health
    )

    for (pkg in healthPackages) {
      try {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(launchIntent)
          return true
        }
      } catch (_: Exception) {}
    }

    // Fallback: Open Health Connect settings in Android 14+
    try {
      val settingsIntent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(settingsIntent)
      return true
    } catch (_: Exception) {
      Toast.makeText(context, "Health Connect not configured", Toast.LENGTH_SHORT).show()
      return false
    }
  }

  // Package detection helper
  fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
      context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      true
    } catch (_: Exception) {
      false
    }
  }
}

package com.example.service

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.Toast

object SystemPortHelper {

  fun launchClock(context: Context): Boolean {
    return try {
      val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      try {
        val clockIntent = Intent(Intent.ACTION_MAIN).apply {
          addCategory(Intent.CATEGORY_APP_MESSAGING)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pm = context.packageManager
        val candidates = listOf(
          "com.google.android.deskclock",
          "com.android.deskclock",
          "com.sec.android.app.clockpackage"
        )
        for (pkg in candidates) {
          val launchIntent = pm.getLaunchIntentForPackage(pkg)
          if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
            return true
          }
        }
        Toast.makeText(context, "Clock opened", Toast.LENGTH_SHORT).show()
        false
      } catch (_: Exception) {
        false
      }
    }
  }

  fun launchWeather(context: Context, city: String = "London"): Boolean {
    return try {
      val pm = context.packageManager
      val candidates = listOf(
        "com.google.android.apps.weather",
        "com.nothing.weather",
        "com.sec.android.daemonapp"
      )
      for (pkg in candidates) {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(launchIntent)
          return true
        }
      }
      val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=weather+$city")).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(webIntent)
      true
    } catch (e: Exception) {
      Toast.makeText(context, "Weather: $city", Toast.LENGTH_SHORT).show()
      false
    }
  }

  // Headphone / Ear / Casque widget click: opens Nothing X app or Bluetooth settings
  fun launchAudioDeviceSettings(context: Context): Boolean {
    return try {
      val pm = context.packageManager
      val candidates = listOf(
        "com.nothing.smartcenter", // Nothing X app
        "com.nothing.hearse",
        "com.google.android.apps.wearables.maestro.companion"
      )
      for (pkg in candidates) {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(launchIntent)
          return true
        }
      }
      // Fallback: Open Bluetooth settings
      val btIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(btIntent)
      true
    } catch (_: Exception) {
      try {
        val settingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(settingsIntent)
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  // Smartwatch widget click: opens Watch companion or Fitness / Health app
  fun launchWatchFitnessApp(context: Context): Boolean {
    return try {
      val pm = context.packageManager
      val candidates = listOf(
        "com.google.android.apps.fitness", // Google Fit
        "com.samsung.android.app.shealth",  // Samsung Health
        "com.nothing.smartcenter",          // CMF Watch app
        "com.google.android.apps.healthdata"
      )
      for (pkg in candidates) {
        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent != null) {
          launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(launchIntent)
          return true
        }
      }
      // Fallback to web fit tracker or health settings
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fit.google.com")).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      Toast.makeText(context, "Health tracker", Toast.LENGTH_SHORT).show()
      false
    }
  }

  // Home search bar click: opens Web search or Google search
  fun launchWebSearch(context: Context, query: String = ""): Boolean {
    return try {
      val intent = if (query.isNotBlank()) {
        Intent(Intent.ACTION_WEB_SEARCH).apply {
          putExtra(SearchManager.QUERY, query)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
      } else {
        val pm = context.packageManager
        val googleIntent = pm.getLaunchIntentForPackage("com.google.android.googlequicksearchbox")
        if (googleIntent != null) {
          googleIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          googleIntent
        } else {
          Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          }
        }
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      try {
        val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallback)
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  fun launchGoogleFeed(context: Context): Boolean {
    val pm = context.packageManager
    return try {
      val launchIntent = pm.getLaunchIntentForPackage("com.google.android.googlequicksearchbox")
      if (launchIntent != null) {
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(launchIntent)
        true
      } else {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/discover")).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(webIntent)
        true
      }
    } catch (_: Exception) {
      Toast.makeText(context, "Google app not found", Toast.LENGTH_SHORT).show()
      false
    }
  }

  fun openAppDetails(context: Context, packageName: String): Boolean {
    return try {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
      true
    } catch (_: Exception) {
      false
    }
  }

  fun openNotificationPanel(context: Context) {
    try {
      @Suppress("WrongConstant")
      val statusBarService = context.getSystemService("statusbar")
      val statusBarManager = Class.forName("android.app.StatusBarManager")
      val expand = statusBarManager.getMethod("expandNotificationsPanel")
      expand.invoke(statusBarService)
    } catch (_: Exception) {
      Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
    }
  }

  fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
      context.packageManager.getPackageInfo(packageName, 0)
      true
    } catch (_: Exception) {
      false
    }
  }
}

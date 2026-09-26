package com.example.service

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

object SystemIntegrationHelper {

  // 1. Overlay (Superposition / Draw over other apps)
  fun isOverlayGranted(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
  }

  fun requestOverlayPermission(context: Context) {
    try {
      val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
      ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (_: Exception) {
      val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(fallback)
    }
  }

  // 2. Accessibility Service (Gestures: double tap to sleep, pull down notifications)
  fun isAccessibilityGranted(context: Context): Boolean {
    if (LauncherAccessibilityService.isConnected.value) return true
    val expectedComponentName = ComponentName(context, LauncherAccessibilityService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(
      context.contentResolver,
      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabledServices.contains(expectedComponentName)
  }

  fun openAccessibilitySettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open Accessibility settings", Toast.LENGTH_SHORT).show()
    }
  }

  // 3. Notification Listener (App Badges & Dots)
  fun isNotificationListenerGranted(context: Context): Boolean {
    val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
    return enabledPackages.contains(context.packageName)
  }

  fun openNotificationListenerSettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open Notification Listener settings", Toast.LENGTH_SHORT).show()
    }
  }

  // 4. Usage Access (App usage & screen time)
  @Suppress("DEPRECATION")
  fun isUsageAccessGranted(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
      )
    } else {
      @Suppress("DEPRECATION")
      appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
      )
    }
    return mode == AppOpsManager.MODE_ALLOWED
  }

  fun openUsageAccessSettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open Usage Access settings", Toast.LENGTH_SHORT).show()
    }
  }

  // 5. Do Not Disturb / Notification Policy
  fun isDndPolicyGranted(context: Context): Boolean {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    return nm?.isNotificationPolicyAccessGranted == true
  }

  fun openDndPolicySettings(context: Context) {
    try {
      val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open DND settings", Toast.LENGTH_SHORT).show()
    }
  }

  // 6. Default Launcher Check & Request
  fun isDefaultLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply {
      addCategory(Intent.CATEGORY_HOME)
    }
    val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      context.packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
    } else {
      @Suppress("DEPRECATION")
      context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }
    return resolveInfo?.activityInfo?.packageName == context.packageName
  }

  fun openDefaultLauncherSettings(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      } else {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      }
    } catch (_: Exception) {
      try {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      } catch (_: Exception) {
        openAppDetailsSettings(context)
      }
    }
  }

  // 7. General App Details Settings
  fun openAppDetailsSettings(context: Context) {
    try {
      val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}")
      ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open app settings", Toast.LENGTH_SHORT).show()
    }
  }

  // 8. Uninstall package directly
  fun requestUninstallPackage(context: Context, packageName: String) {
    try {
      val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Cannot uninstall system app", Toast.LENGTH_SHORT).show()
    }
  }

  // 9. Gestures / Actions
  fun lockScreen(context: Context): Boolean {
    val success = LauncherAccessibilityService.lockScreen()
    if (!success) {
      Toast.makeText(
        context,
        "Enable Accessibility Service in Settings to lock screen",
        Toast.LENGTH_LONG
      ).show()
      openAccessibilitySettings(context)
    }
    return success
  }

  fun openNotificationShade(context: Context): Boolean {
    val success = LauncherAccessibilityService.openNotifications()
    if (!success) {
      // Fallback via reflection for status bar expansion
      try {
        val service = context.getSystemService("statusbar")
        val statusbarManager = Class.forName("android.app.StatusBarManager")
        val method = statusbarManager.getMethod("expandNotificationsPanel")
        method.invoke(service)
        return true
      } catch (_: Exception) {
        Toast.makeText(
          context,
          "Enable Accessibility Service to pull down notifications",
          Toast.LENGTH_SHORT
        ).show()
      }
    }
    return success
  }
}

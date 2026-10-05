package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.LockScreenSettings
import com.example.model.LockSecurityType
import com.example.model.NosWidgetPortType

object LauncherPreferencesManager {
  private const val PREFS_NAME = "nothing_launcher_prefs"

  fun loadSettings(context: Context): LauncherSettings {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val themeModeName = prefs.getString("theme_mode", LauncherThemeMode.ORIGINAL.name) ?: LauncherThemeMode.ORIGINAL.name
    val themeMode = try { LauncherThemeMode.valueOf(themeModeName) } catch (_: Exception) { LauncherThemeMode.ORIGINAL }

    val iconPackName = prefs.getString("icon_pack", IconPackStyle.MONOCHROME.name) ?: IconPackStyle.MONOCHROME.name
    val iconPack = try { IconPackStyle.valueOf(iconPackName) } catch (_: Exception) { IconPackStyle.MONOCHROME }

    val securityTypeName = prefs.getString("lock_security_type", LockSecurityType.SWIPE.name) ?: LockSecurityType.SWIPE.name
    val securityType = try { LockSecurityType.valueOf(securityTypeName) } catch (_: Exception) { LockSecurityType.SWIPE }

    val activeWidgetsString = prefs.getString("active_widgets", null)
    val activeWidgets = if (activeWidgetsString != null) {
      activeWidgetsString.split(",")
        .filter { it.isNotBlank() }
        .mapNotNull {
          try { NosWidgetPortType.valueOf(it) } catch (_: Exception) { null }
        }
    } else {
      listOf(
        NosWidgetPortType.CALENDAR_DIGITAL_TIME,
        NosWidgetPortType.CLOCK_MAIN,
        NosWidgetPortType.WEATHER_MAIN,
        NosWidgetPortType.EAR_BATTERY,
        NosWidgetPortType.WATCH_STATS
      )
    }

    return LauncherSettings(
      themeMode = themeMode,
      iconPack = iconPack,
      accentColorIndex = prefs.getInt("accent_color_index", 0),
      showLabels = prefs.getBoolean("show_labels", true),
      drawerColumnCount = prefs.getInt("drawer_column_count", 4),
      drawerColoredIcons = prefs.getBoolean("drawer_colored_icons", false),
      drawerCardSizeLevel = prefs.getInt("drawer_card_size_level", 1),
      iconSizeLevel = prefs.getInt("icon_size_level", 1),
      showSearchBarOnDock = prefs.getBoolean("show_search_bar", true),
      swipeDownNotifications = prefs.getBoolean("swipe_down_notif", true),
      wallpaperIndex = prefs.getInt("wallpaper_index", 0),
      wallpaperDimPct = prefs.getInt("wallpaper_dim_pct", 20),
      customWallpaperUri = prefs.getString("custom_wallpaper_uri", null),
      lockScreen = LockScreenSettings(
        securityType = securityType,
        pinCode = prefs.getString("lock_pin", "1234") ?: "1234",
        customOwnerInfo = prefs.getString("owner_info", "NOTHING (R) OS 5") ?: "NOTHING (R) OS 5",
        showClock = prefs.getBoolean("lock_show_clock", true),
        showWidgets = prefs.getBoolean("lock_show_widgets", true),
        showNotifications = prefs.getBoolean("lock_show_notif", true)
      ),
      activeWidgets = activeWidgets
    )
  }

  fun saveSettings(context: Context, settings: LauncherSettings) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
      .putString("theme_mode", settings.themeMode.name)
      .putString("icon_pack", settings.iconPack.name)
      .putInt("accent_color_index", settings.accentColorIndex)
      .putBoolean("show_labels", settings.showLabels)
      .putInt("drawer_column_count", settings.drawerColumnCount)
      .putBoolean("drawer_colored_icons", settings.drawerColoredIcons)
      .putInt("drawer_card_size_level", settings.drawerCardSizeLevel)
      .putInt("icon_size_level", settings.iconSizeLevel)
      .putBoolean("show_search_bar", settings.showSearchBarOnDock)
      .putBoolean("swipe_down_notif", settings.swipeDownNotifications)
      .putInt("wallpaper_index", settings.wallpaperIndex)
      .putInt("wallpaper_dim_pct", settings.wallpaperDimPct)
      .putString("custom_wallpaper_uri", settings.customWallpaperUri)
      .putString("lock_security_type", settings.lockScreen.securityType.name)
      .putString("lock_pin", settings.lockScreen.pinCode)
      .putString("owner_info", settings.lockScreen.customOwnerInfo)
      .putBoolean("lock_show_clock", settings.lockScreen.showClock)
      .putBoolean("lock_show_widgets", settings.lockScreen.showWidgets)
      .putBoolean("lock_show_notif", settings.lockScreen.showNotifications)
      .putString("active_widgets", settings.activeWidgets.joinToString(",") { it.name })
      .apply()
  }
}

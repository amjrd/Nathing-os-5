package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.IconPackStyle
import com.example.model.LauncherClockStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.LockClockStyle
import com.example.model.LockScreenSettings
import com.example.model.LockSecurityType
import com.example.model.LockShortcutType
import com.example.model.NosWidgetPortType

/**
 * Robust SharedPreferences manager for Nothing Launcher.
 * Guarantees that all customizations (wallpapers, widgets, themes, icon styles, sizes, dock apps,
 * notes, lockscreen configurations) persist across app exits, background kills, and device reboots.
 */
object LauncherPreferencesManager {

  private const val PREFS_NAME = "nothing_launcher_settings"

  // Settings keys
  private const val KEY_ICON_PACK = "icon_pack"
  private const val KEY_THEME_MODE = "theme_mode"
  private const val KEY_CLOCK_STYLE = "clock_style"
  private const val KEY_ACCENT_COLOR_INDEX = "accent_color_index"
  private const val KEY_GRID_COLUMNS = "grid_columns"
  private const val KEY_SHOW_LABELS = "show_labels"
  private const val KEY_IS_12_HOUR = "is_12_hour"
  private const val KEY_TEMP_CELSIUS = "temp_celsius"
  private const val KEY_DOUBLE_TAP_SLEEP = "double_tap_sleep"
  private const val KEY_SWIPE_NOTIFS = "swipe_notifs"
  private const val KEY_SEARCH_BAR_DOCK = "search_bar_dock"
  private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
  private const val KEY_WALLPAPER_INDEX = "wallpaper_index"
  private const val KEY_CUSTOM_WALLPAPER_URI = "custom_wallpaper_uri"
  private const val KEY_LOCK_WALLPAPER_INDEX = "lock_wallpaper_index"
  private const val KEY_CUSTOM_LOCK_WALLPAPER_URI = "custom_lock_wallpaper_uri"
  private const val KEY_WALLPAPER_DIM_PCT = "wallpaper_dim_pct"
  private const val KEY_ICON_SIZE_LEVEL = "icon_size_level"
  private const val KEY_DRAWER_CARD_SIZE_LEVEL = "drawer_card_size_level"
  private const val KEY_WIDGET_SIZE_LEVEL = "widget_size_level"
  private const val KEY_ACTIVE_WIDGETS = "active_widgets"

  // Lockscreen keys
  private const val KEY_LOCK_ENABLED = "lock_enabled"
  private const val KEY_LOCK_PREVENT_OVERLAP = "lock_prevent_overlap"
  private const val KEY_LOCK_SECURITY_TYPE = "lock_security_type"
  private const val KEY_LOCK_PIN_CODE = "lock_pin_code"
  private const val KEY_LOCK_CLOCK_STYLE = "lock_clock_style"
  private const val KEY_LOCK_SHOW_WIDGETS = "lock_show_widgets"
  private const val KEY_LOCK_SHOW_NOTIFS = "lock_show_notifs"
  private const val KEY_LOCK_SHOW_BATTERY = "lock_show_battery"
  private const val KEY_LOCK_LEFT_SHORTCUT = "lock_left_shortcut"
  private const val KEY_LOCK_RIGHT_SHORTCUT = "lock_right_shortcut"
  private const val KEY_LOCK_OWNER_INFO = "lock_owner_info"

  // Other persistent state
  private const val KEY_DOCK_PACKAGES = "dock_packages"
  private const val KEY_QUICK_NOTE = "quick_note"
  private const val PREFIX_FOLDER_ENLARGED = "folder_enlarged_"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  fun saveSettings(context: Context, settings: LauncherSettings) {
    val prefs = getPrefs(context)
    val widgetsString = settings.activeWidgets.joinToString(",") { it.name }
    prefs.edit().apply {
      putString(KEY_ICON_PACK, settings.iconPack.name)
      putString(KEY_THEME_MODE, settings.themeMode.name)
      putString(KEY_CLOCK_STYLE, settings.clockStyle.name)
      putInt(KEY_ACCENT_COLOR_INDEX, settings.accentColorIndex)
      putInt(KEY_GRID_COLUMNS, settings.gridColumns)
      putBoolean(KEY_SHOW_LABELS, settings.showLabels)
      putBoolean(KEY_IS_12_HOUR, settings.is12HourFormat)
      putBoolean(KEY_TEMP_CELSIUS, settings.tempUnitCelsius)
      putBoolean(KEY_DOUBLE_TAP_SLEEP, settings.doubleTapToSleep)
      putBoolean(KEY_SWIPE_NOTIFS, settings.swipeDownNotifications)
      putBoolean(KEY_SEARCH_BAR_DOCK, settings.showSearchBarOnDock)
      putBoolean(KEY_HAPTIC_ENABLED, settings.hapticFeedbackEnabled)
      putInt(KEY_WALLPAPER_INDEX, settings.wallpaperIndex)
      putString(KEY_CUSTOM_WALLPAPER_URI, settings.customWallpaperUri)
      putInt(KEY_LOCK_WALLPAPER_INDEX, settings.lockScreenWallpaperIndex)
      putString(KEY_CUSTOM_LOCK_WALLPAPER_URI, settings.customLockScreenWallpaperUri)
      putInt(KEY_WALLPAPER_DIM_PCT, settings.wallpaperDimPct)
      putInt(KEY_ICON_SIZE_LEVEL, settings.iconSizeLevel)
      putInt(KEY_DRAWER_CARD_SIZE_LEVEL, settings.drawerCardSizeLevel)
      putInt(KEY_WIDGET_SIZE_LEVEL, settings.widgetSizeLevel)
      putString(KEY_ACTIVE_WIDGETS, widgetsString)

      // Lock Screen Settings
      putBoolean(KEY_LOCK_ENABLED, settings.lockScreen.isLockScreenEnabled)
      putBoolean(KEY_LOCK_PREVENT_OVERLAP, settings.lockScreen.preventSystemLockOverlap)
      putString(KEY_LOCK_SECURITY_TYPE, settings.lockScreen.securityType.name)
      putString(KEY_LOCK_PIN_CODE, settings.lockScreen.pinCode)
      putString(KEY_LOCK_CLOCK_STYLE, settings.lockScreen.clockStyle.name)
      putBoolean(KEY_LOCK_SHOW_WIDGETS, settings.lockScreen.showWidgets)
      putBoolean(KEY_LOCK_SHOW_NOTIFS, settings.lockScreen.showNotifications)
      putBoolean(KEY_LOCK_SHOW_BATTERY, settings.lockScreen.showBatteryGlyph)
      putString(KEY_LOCK_LEFT_SHORTCUT, settings.lockScreen.leftShortcut.name)
      putString(KEY_LOCK_RIGHT_SHORTCUT, settings.lockScreen.rightShortcut.name)
      putString(KEY_LOCK_OWNER_INFO, settings.lockScreen.customOwnerInfo)

      apply()
    }
  }

  fun loadSettings(context: Context): LauncherSettings {
    val prefs = getPrefs(context)
    val defaults = LauncherSettings()

    // Safely parse enums
    val iconPack = runCatching {
      val name = prefs.getString(KEY_ICON_PACK, null)
      if (name != null) IconPackStyle.valueOf(name) else defaults.iconPack
    }.getOrDefault(defaults.iconPack)

    val themeMode = runCatching {
      val name = prefs.getString(KEY_THEME_MODE, null)
      if (name != null) LauncherThemeMode.valueOf(name) else defaults.themeMode
    }.getOrDefault(defaults.themeMode)

    val clockStyle = runCatching {
      val name = prefs.getString(KEY_CLOCK_STYLE, null)
      if (name != null) LauncherClockStyle.valueOf(name) else defaults.clockStyle
    }.getOrDefault(defaults.clockStyle)

    val widgetsStr = prefs.getString(KEY_ACTIVE_WIDGETS, null)
    val activeWidgets = if (!widgetsStr.isNullOrEmpty()) {
      widgetsStr.split(",").mapNotNull { item ->
        runCatching { NosWidgetPortType.valueOf(item.trim()) }.getOrNull()
      }.ifEmpty { defaults.activeWidgets }
    } else {
      defaults.activeWidgets
    }

    // Lock screen
    val lockDefaults = defaults.lockScreen
    val securityType = runCatching {
      val name = prefs.getString(KEY_LOCK_SECURITY_TYPE, null)
      if (name != null) LockSecurityType.valueOf(name) else lockDefaults.securityType
    }.getOrDefault(lockDefaults.securityType)

    val lockClockStyle = runCatching {
      val name = prefs.getString(KEY_LOCK_CLOCK_STYLE, null)
      if (name != null) LockClockStyle.valueOf(name) else lockDefaults.clockStyle
    }.getOrDefault(lockDefaults.clockStyle)

    val leftShortcut = runCatching {
      val name = prefs.getString(KEY_LOCK_LEFT_SHORTCUT, null)
      if (name != null) LockShortcutType.valueOf(name) else lockDefaults.leftShortcut
    }.getOrDefault(lockDefaults.leftShortcut)

    val rightShortcut = runCatching {
      val name = prefs.getString(KEY_LOCK_RIGHT_SHORTCUT, null)
      if (name != null) LockShortcutType.valueOf(name) else lockDefaults.rightShortcut
    }.getOrDefault(lockDefaults.rightShortcut)

    val loadedLockScreen = LockScreenSettings(
      isLockScreenEnabled = prefs.getBoolean(KEY_LOCK_ENABLED, lockDefaults.isLockScreenEnabled),
      preventSystemLockOverlap = prefs.getBoolean(KEY_LOCK_PREVENT_OVERLAP, lockDefaults.preventSystemLockOverlap),
      securityType = securityType,
      pinCode = prefs.getString(KEY_LOCK_PIN_CODE, lockDefaults.pinCode) ?: lockDefaults.pinCode,
      clockStyle = lockClockStyle,
      showWidgets = prefs.getBoolean(KEY_LOCK_SHOW_WIDGETS, lockDefaults.showWidgets),
      showNotifications = prefs.getBoolean(KEY_LOCK_SHOW_NOTIFS, lockDefaults.showNotifications),
      showBatteryGlyph = prefs.getBoolean(KEY_LOCK_SHOW_BATTERY, lockDefaults.showBatteryGlyph),
      leftShortcut = leftShortcut,
      rightShortcut = rightShortcut,
      customOwnerInfo = prefs.getString(KEY_LOCK_OWNER_INFO, lockDefaults.customOwnerInfo) ?: lockDefaults.customOwnerInfo
    )

    return LauncherSettings(
      iconPack = iconPack,
      themeMode = themeMode,
      clockStyle = clockStyle,
      accentColorIndex = prefs.getInt(KEY_ACCENT_COLOR_INDEX, defaults.accentColorIndex),
      gridColumns = prefs.getInt(KEY_GRID_COLUMNS, defaults.gridColumns),
      showLabels = prefs.getBoolean(KEY_SHOW_LABELS, defaults.showLabels),
      is12HourFormat = prefs.getBoolean(KEY_IS_12_HOUR, defaults.is12HourFormat),
      tempUnitCelsius = prefs.getBoolean(KEY_TEMP_CELSIUS, defaults.tempUnitCelsius),
      doubleTapToSleep = prefs.getBoolean(KEY_DOUBLE_TAP_SLEEP, defaults.doubleTapToSleep),
      swipeDownNotifications = prefs.getBoolean(KEY_SWIPE_NOTIFS, defaults.swipeDownNotifications),
      showSearchBarOnDock = prefs.getBoolean(KEY_SEARCH_BAR_DOCK, defaults.showSearchBarOnDock),
      hapticFeedbackEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, defaults.hapticFeedbackEnabled),
      wallpaperIndex = prefs.getInt(KEY_WALLPAPER_INDEX, defaults.wallpaperIndex),
      customWallpaperUri = prefs.getString(KEY_CUSTOM_WALLPAPER_URI, null),
      lockScreenWallpaperIndex = prefs.getInt(KEY_LOCK_WALLPAPER_INDEX, defaults.lockScreenWallpaperIndex),
      customLockScreenWallpaperUri = prefs.getString(KEY_CUSTOM_LOCK_WALLPAPER_URI, null),
      wallpaperDimPct = prefs.getInt(KEY_WALLPAPER_DIM_PCT, defaults.wallpaperDimPct),
      lockScreen = loadedLockScreen,
      iconSizeLevel = prefs.getInt(KEY_ICON_SIZE_LEVEL, defaults.iconSizeLevel),
      drawerCardSizeLevel = prefs.getInt(KEY_DRAWER_CARD_SIZE_LEVEL, defaults.drawerCardSizeLevel),
      widgetSizeLevel = prefs.getInt(KEY_WIDGET_SIZE_LEVEL, defaults.widgetSizeLevel),
      activeWidgets = activeWidgets
    )
  }

  fun saveDockAppPackages(context: Context, packageNames: List<String>) {
    getPrefs(context).edit().putString(KEY_DOCK_PACKAGES, packageNames.joinToString(",")).apply()
  }

  fun loadDockAppPackages(context: Context): List<String>? {
    val raw = getPrefs(context).getString(KEY_DOCK_PACKAGES, null) ?: return null
    if (raw.isBlank()) return emptyList()
    return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
  }

  fun saveQuickNote(context: Context, note: String) {
    getPrefs(context).edit().putString(KEY_QUICK_NOTE, note).apply()
  }

  fun loadQuickNote(context: Context): String {
    val defaultNote = "NOTHING OS 5.0\n• Pure Minimalism\n• Zero Bloatware\n• Dot Matrix Engine"
    return getPrefs(context).getString(KEY_QUICK_NOTE, defaultNote) ?: defaultNote
  }

  fun saveFolderEnlarged(context: Context, folderId: String, isEnlarged: Boolean) {
    getPrefs(context).edit().putBoolean(PREFIX_FOLDER_ENLARGED + folderId, isEnlarged).apply()
  }

  fun isFolderEnlarged(context: Context, folderId: String, defaultVal: Boolean = true): Boolean {
    return getPrefs(context).getBoolean(PREFIX_FOLDER_ENLARGED + folderId, defaultVal)
  }
}

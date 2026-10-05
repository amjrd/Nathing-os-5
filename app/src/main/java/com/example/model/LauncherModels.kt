package com.example.model

import android.graphics.drawable.Drawable

data class AppItem(
  val packageName: String,
  val activityName: String = "",
  val label: String,
  val iconDrawable: Drawable? = null,
  val category: String = "Tools",
  val notificationCount: Int = 0,
  val isPinned: Boolean = false
)

enum class IconPackStyle {
  MONOCHROME,
  COLORFUL,
  NOTHING_ORIGINAL
}

enum class LauncherThemeMode {
  ORIGINAL,          // Nothing OS 2 Signature Black/White Dot-matrix
  MONOCHROME_STUDIO, // High contrast black & white studio
  ATMOSPHERE_PASTEL, // Soft teal/mint atmosphere
  GLYPH_RED          // Dark graphite with Nothing Red glyph accents
}

enum class LauncherClockStyle {
  DOT_MATRIX_DIGITAL,
  ANALOG_CLASSIC,
  NOTHING_BOLD,
  MINIMAL_VERTICAL
}

enum class NosWidgetPortType {
  CALENDAR_DIGITAL_TIME,
  MINI_CLUSTER_2X2,
  CLOCK_MAIN,
  WEATHER_MAIN,
  QUICK_LOOK,
  EAR_BATTERY,
  WATCH_STATS,
  SYSTEM_RESOURCES,
  GIANT_CIRCLES_CLUSTER
}

enum class LockSecurityType {
  SWIPE,
  PIN,
  PASSWORD
}

data class LockScreenSettings(
  val securityType: LockSecurityType = LockSecurityType.SWIPE,
  val pinCode: String = "1234",
  val customOwnerInfo: String = "NOTHING (R) OS 5",
  val showClock: Boolean = true,
  val clockStyle: LauncherClockStyle = LauncherClockStyle.DOT_MATRIX_DIGITAL,
  val showWidgets: Boolean = true,
  val showNotifications: Boolean = true
)

data class LauncherSettings(
  val themeMode: LauncherThemeMode = LauncherThemeMode.ORIGINAL,
  val iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  val accentColorIndex: Int = 0,
  val showLabels: Boolean = true,
  val gridColumns: Int = 4,
  val drawerColumnCount: Int = 4,
  val drawerColoredIcons: Boolean = false,
  val drawerCardSizeLevel: Int = 1,
  val iconSizeLevel: Int = 1, // 0: 44dp, 1: 52dp, 2: 60dp, 3: 68dp
  val showSearchBarOnDock: Boolean = true,
  val swipeDownNotifications: Boolean = true,
  val wallpaperIndex: Int = 0,
  val wallpaperDimPct: Int = 20,
  val customWallpaperUri: String? = null,
  val lockScreen: LockScreenSettings = LockScreenSettings(),
  val activeWidgets: List<NosWidgetPortType> = listOf(
    NosWidgetPortType.CALENDAR_DIGITAL_TIME,
    NosWidgetPortType.CLOCK_MAIN,
    NosWidgetPortType.WEATHER_MAIN,
    NosWidgetPortType.EAR_BATTERY,
    NosWidgetPortType.WATCH_STATS
  )
)

data class AudioState(
  val connected: Boolean = true,
  val deviceName: String = "Ear (open)",
  val batteryLeft: Int = 85,
  val batteryRight: Int = 90,
  val batteryCase: Int = 75,
  val ancMode: String = "TRANSPARENCY",
  val isPlaying: Boolean = false,
  val title: String = "Nothing Track",
  val artist: String = "Teenage Engineering"
)

data class FitnessStats(
  val steps: Int = 6842,
  val goal: Int = 10000,
  val heartRate: Int = 72,
  val calories: Int = 340,
  val distanceKm: Float = 4.8f,
  val watchBattery: Int = 88
)

data class WeatherData(
  val temperatureC: Int = 22,
  val condition: String = "Clear",
  val city: String = "London",
  val highC: Int = 24,
  val lowC: Int = 16
)

data class WeatherInfo(
  val tempC: Int = 22,
  val condition: String = "SUNNY",
  val city: String = "TUNIS",
  val highC: Int = 25,
  val lowC: Int = 18
)

data class QuickToggleState(
  val isTorchOn: Boolean = false,
  val soundMode: Int = 2, // 0: Silent, 1: Vibrate, 2: Normal
  val batteryLevel: Int = 85,
  val isCharging: Boolean = false
)

data class FolderItem(
  val id: String,
  val name: String,
  val appPackages: List<String>,
  val isEnlarged: Boolean = false
)

enum class LauncherScreen {
  HOME,
  LOCK,
  APP_DRAWER,
  SETTINGS,
  SEARCH
}

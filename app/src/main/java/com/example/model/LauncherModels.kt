package com.example.model

import android.graphics.drawable.Drawable

enum class IconPackStyle {
  MONOCHROME,      // Signature Nothing black/white circles with high contrast glyphs ("Nothing")
  COLOUR,          // Nothing OS 3.0/5.0 scalloped 12-point flower badge with vibrant pastel background ("Colour")
  MINIMAL_DARK,    // Dark matte background with sleek white outlines
  SYSTEM_DEFAULT   // Original system app icons ("Default")
}

enum class LauncherScreen {
  LOCK_SCREEN,
  HOME,
  APP_DRAWER,
  SETTINGS
}

enum class LockClockStyle {
  DOT_MATRIX_BIG,     // Large NDOT font style
  VERTICAL_STACK,     // Hour on top, minute on bottom in bold typography
  MINIMAL_ANALOG,     // Minimalist Nothing watch face with dots & ticking second
  CLASSIC_DIGITAL     // Crisp clean digital line with glyph date
}

enum class LockSecurityType {
  SWIPE,              // Swipe up to unlock
  PIN                 // 4-digit Nothing PIN lock
}

enum class LockShortcutType {
  TORCH,
  CAMERA,
  CALCULATOR,
  VOICE_RECORDER,
  NONE
}

enum class WallpaperTarget {
  HOME,
  LOCK,
  BOTH
}

enum class LauncherThemeMode {
  ORIGINAL,          // Signature: Nothing OS 2 black/white dot-matrix home
  MONOCHROME_STUDIO, // Monochrome photographic studio
  ATMOSPHERE_PASTEL, // Teal/blue atmospheric glass
  GLYPH_RED          // Dark graphite with Nothing red glyph accents
}

enum class LauncherClockStyle {
  ANALOG,  // Round disc analog clock with hour/minute hands & accent dot (as in Image 3)
  DIGITAL  // Segmented dot matrix clock (as in Image 2)
}

data class LockNotificationItem(
  val id: String,
  val packageName: String,
  val appName: String,
  val title: String,
  val text: String,
  val timeFormatted: String = "NOW"
)

data class LockScreenSettings(
  val isLockScreenEnabled: Boolean = true,
  val preventSystemLockOverlap: Boolean = true, // Prevents launcher widgets from showing behind Android system lockscreen
  val securityType: LockSecurityType = LockSecurityType.SWIPE,
  val pinCode: String = "1234",
  val clockStyle: LockClockStyle = LockClockStyle.DOT_MATRIX_BIG,
  val showWidgets: Boolean = true,
  val showNotifications: Boolean = true,
  val showBatteryGlyph: Boolean = true,
  val leftShortcut: LockShortcutType = LockShortcutType.TORCH,
  val rightShortcut: LockShortcutType = LockShortcutType.CAMERA,
  val customOwnerInfo: String = "NOTHING PHONE (2) • NOTHING OS 5"
)

data class AppItem(
  val packageName: String,
  val activityName: String = "",
  val label: String,
  val icon: Drawable? = null,
  val isPinned: Boolean = false,
  val isDock: Boolean = false,
  val category: String = "General",
  val notificationCount: Int = 0
)

data class FolderItem(
  val id: String,
  val name: String,
  val isEnlarged: Boolean = true, // Signature Nothing 2x2 enlarged folder
  val apps: List<AppItem> = emptyList()
)

data class WeatherInfo(
  val tempC: Int = 22,
  val condition: String = "SUNNY", // SUNNY, CLOUDY, RAIN, THUNDER, SNOW
  val city: String = "LONDON",
  val highC: Int = 25,
  val lowC: Int = 16
)

data class AudioState(
  val isPlaying: Boolean = false,
  val title: String = "Nothing (R)",
  val artist: String = "Tape Reel 01",
  val progress: Float = 0.42f
)

data class FitnessStats(
  val steps: Int = 7420,
  val goal: Int = 10000,
  val calories: Int = 345,
  val distanceKm: Float = 5.2f
)

data class QuickToggleState(
  val isTorchOn: Boolean = false,
  val soundMode: Int = 2, // 0 = Silent, 1 = Vibrate, 2 = Normal
  val batteryLevel: Int = 84,
  val isCharging: Boolean = false,
  val wifiEnabled: Boolean = true
)

enum class NosWidgetPortType {
  CALENDAR_DIGITAL_TIME, // Screenshot 2: JUL TUESDAY 07H 10M
  MINI_CLUSTER_2X2,      // Screenshot 2: 14° + Cloud glyph + ECG pulse + Red recorder
  GLANCE_TEXT_SUMMARY,   // Screenshot 2: "TODAY IS TUESDAY AND TIME IS..."
  CIRCULAR_GAUGES,       // Screenshot 1: 73% Music + 57°C Flame + 98% Bell
  DECIBEL_SOUND_METER,   // Screenshot 1: 103 dB with vertical dot LED meter
  QUICK_CHECKLIST,       // Screenshot 1: "Get groceries. Read a book..."
  CONTACT_PILL,          // Screenshot 1: Contact card with call & chat
  CLOCK_MAIN,            // Main Nothing Clock
  WEATHER_MAIN,          // Main Weather & Quick Toggles
  CASSETTE_PLAYER,       // Teenage Cassette Player
  PEDOMETER_GAUGE,       // Pedometer & RAM
  GIANT_CIRCLES_CLUSTER, // Screenshot 3: Giant Camera circle, Dot-Matrix Rain Weather circle & Dot-Matrix Glyph
  STICKER_FOCUS_CLUSTER, // Screenshot 5: Focus concentric lines, Retro Car sticker & Capsule pill
  NOTHING_X_EARBUDS      // Screenshot 5: Headphones 90% battery & Noise Cancellation toggle
}

data class LauncherSettings(
  val iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  val themeMode: LauncherThemeMode = LauncherThemeMode.ORIGINAL,
  val clockStyle: LauncherClockStyle = LauncherClockStyle.ANALOG, // ANALOG (Image 3) or DIGITAL (Image 2)
  val accentColorIndex: Int = 0, // 0: Red, 1: White, 2: Orange, 3: Yellow
  val gridColumns: Int = 4,
  val showLabels: Boolean = true,
  val is12HourFormat: Boolean = false,
  val tempUnitCelsius: Boolean = true,
  val doubleTapToSleep: Boolean = true,
  val swipeDownNotifications: Boolean = true,
  val showSearchBarOnDock: Boolean = true,
  val hapticFeedbackEnabled: Boolean = true,
  val wallpaperIndex: Int = 0, // 0: Dot Matrix, 1: Carbon Matte, 2: Circuit Glow, 3: Light Dots, 4: Glyph Neon, 5: Retro Grid, 6: Red Abstract, 7: Custom Photo
  val customWallpaperUri: String? = null,
  val lockScreenWallpaperIndex: Int = -1, // -1 means same as launcher wallpaper
  val customLockScreenWallpaperUri: String? = null,
  val wallpaperDimPct: Int = 30, // 0% to 70% dim overlay for icon clarity
  val lockScreen: LockScreenSettings = LockScreenSettings(),
  val iconSizeLevel: Int = 1, // 0: Small (44dp), 1: Standard (52dp), 2: Large (60dp), 3: Extra Large (68dp)
  val drawerCardSizeLevel: Int = 1, // 0: Compact, 1: Standard, 2: Large
  val widgetSizeLevel: Int = 1, // 0: Compact (85%), 1: Standard (100%), 2: Expanded (115%)
  // Custom widgets start empty; the user adds them from Home.
  val activeWidgets: List<NosWidgetPortType> = emptyList()
)

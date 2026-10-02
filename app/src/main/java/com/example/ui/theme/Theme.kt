package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.LauncherThemeMode

// Theme Nuit (Dark - Image 2)
private val NothingDarkColorScheme = darkColorScheme(
  primary = NothingRed,
  onPrimary = NothingWhite,
  primaryContainer = NothingElevated,
  onPrimaryContainer = NothingWhite,
  secondary = NothingWhite,
  onSecondary = NothingBlack,
  secondaryContainer = NothingDarkSurface,
  onSecondaryContainer = NothingWhite,
  tertiary = NothingRedLight,
  background = NothingBlack,
  onBackground = NothingWhite,
  surface = NothingMatteBlack,
  onSurface = NothingWhite,
  surfaceVariant = NothingDarkSurface,
  onSurfaceVariant = NothingDimWhite,
  outline = NothingBorder,
  outlineVariant = NothingUnlitDot
)

// Theme Jour (Light - Image 3)
private val NothingLightColorScheme = lightColorScheme(
  primary = NothingRed,
  onPrimary = NothingWhite,
  primaryContainer = NothingLightElevated,
  onPrimaryContainer = NothingLightTextPrimary,
  secondary = NothingLightTextPrimary,
  onSecondary = NothingLightSurface,
  secondaryContainer = NothingLightSurface,
  onSecondaryContainer = NothingLightTextPrimary,
  tertiary = NothingGreenAccent,
  background = NothingLightBackground,
  onBackground = NothingLightTextPrimary,
  surface = NothingLightSurface,
  onSurface = NothingLightTextPrimary,
  surfaceVariant = NothingLightElevated,
  onSurfaceVariant = NothingLightTextSecondary,
  outline = NothingLightBorder,
  outlineVariant = NothingLightUnlitDot
)

// Theme Retro Pastel (Image 3 & 5: Retro Car & Soft Sage/Mint aesthetic)
private val NothingRetroColorScheme = lightColorScheme(
  primary = NothingRetroAccent,
  onPrimary = NothingWhite,
  primaryContainer = NothingRetroElevated,
  onPrimaryContainer = NothingRetroTextPrimary,
  secondary = NothingRetroTextPrimary,
  onSecondary = NothingRetroSurface,
  secondaryContainer = NothingRetroElevated,
  onSecondaryContainer = NothingRetroTextPrimary,
  tertiary = NothingGreenAccent,
  background = NothingRetroBackground,
  onBackground = NothingRetroTextPrimary,
  surface = NothingRetroSurface,
  onSurface = NothingRetroTextPrimary,
  surfaceVariant = NothingRetroElevated,
  onSurfaceVariant = NothingRetroTextSecondary,
  outline = NothingRetroBorder,
  outlineVariant = NothingRetroUnlitDot
)

data class LauncherThemeColors(
  val isDark: Boolean = true,
  val background: Color = NothingBlack,
  val surface: Color = NothingDarkSurface,
  val elevated: Color = NothingElevated,
  val border: Color = NothingBorder,
  val textPrimary: Color = NothingWhite,
  val textSecondary: Color = NothingGrey,
  val unlitDot: Color = NothingUnlitDot,
  val dockBg: Color = NothingDarkSurface,
  val dockButtonBg: Color = NothingElevated,
  val dockIconTint: Color = NothingWhite,
  val searchPillBg: Color = NothingDarkSurface
)

val LocalLauncherTheme = staticCompositionLocalOf {
  LauncherThemeColors()
}

@Composable
fun MyApplicationTheme(
  themeMode: LauncherThemeMode = LauncherThemeMode.ORIGINAL,
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when (themeMode) {
    LauncherThemeMode.ORIGINAL -> NothingDarkColorScheme
    LauncherThemeMode.MONOCHROME_STUDIO -> NothingLightColorScheme
    LauncherThemeMode.ATMOSPHERE_PASTEL -> NothingRetroColorScheme
  }
  val themeColors = when (themeMode) {
    LauncherThemeMode.ORIGINAL -> {
      LauncherThemeColors(
        isDark = true,
        background = NothingBlack,
        surface = NothingDarkSurface,
        elevated = NothingElevated,
        border = NothingBorder,
        textPrimary = NothingWhite,
        textSecondary = NothingGrey,
        unlitDot = NothingUnlitDot,
        dockBg = NothingDarkSurface.copy(alpha = 0.88f),
        dockButtonBg = NothingElevated,
        dockIconTint = NothingWhite,
        searchPillBg = NothingDarkSurface
      )
    }
    LauncherThemeMode.MONOCHROME_STUDIO -> {
      LauncherThemeColors(
        isDark = true,
        background = Color(0xFF000000),
        surface = Color(0xFF0B0B0C),
        elevated = Color(0xFF151517),
        border = Color(0xFF29292C),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFB7B7BC),
        unlitDot = Color(0xFF252527),
        dockBg = Color(0xF5000000),
        dockButtonBg = Color(0xFF151517),
        dockIconTint = Color(0xFFFFFFFF),
        searchPillBg = Color(0xFF0F0F10)
      )
    }
    LauncherThemeMode.ATMOSPHERE_PASTEL -> {
      LauncherThemeColors(
        isDark = false,
        background = Color(0xFFE2E9DE),
        surface = Color(0xFFEEF3EC),
        elevated = Color(0xFFF7FAF5),
        border = Color(0xFFCDD6C8),
        textPrimary = Color(0xFF1E281D),
        textSecondary = Color(0xFF5D6B5A),
        unlitDot = NothingRetroUnlitDot,
        dockBg = Color(0xDDE4ECE1),
        dockButtonBg = Color.White,
        dockIconTint = Color(0xFF1E281D),
        searchPillBg = Color(0xFFEEF3EC)
      )
    }
  }

  CompositionLocalProvider(LocalLauncherTheme provides themeColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

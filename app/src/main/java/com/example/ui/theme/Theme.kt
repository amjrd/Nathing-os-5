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
  themeMode: LauncherThemeMode = LauncherThemeMode.SYSTEM,
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when (themeMode) {
    LauncherThemeMode.DARK -> NothingDarkColorScheme
    LauncherThemeMode.LIGHT -> NothingLightColorScheme
    LauncherThemeMode.RETRO_PASTEL -> NothingRetroColorScheme
    LauncherThemeMode.SYSTEM -> if (darkTheme) NothingDarkColorScheme else NothingLightColorScheme
  }
  val themeColors = when (themeMode) {
    LauncherThemeMode.DARK -> {
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
    LauncherThemeMode.LIGHT -> {
      LauncherThemeColors(
        isDark = false,
        background = NothingLightBackground,
        surface = NothingLightSurface,
        elevated = NothingLightElevated,
        border = NothingLightBorder,
        textPrimary = NothingLightTextPrimary,
        textSecondary = NothingLightTextSecondary,
        unlitDot = NothingLightUnlitDot,
        dockBg = NothingLightSurface.copy(alpha = 0.92f),
        dockButtonBg = Color.White,
        dockIconTint = Color(0xFF1A1A1A),
        searchPillBg = NothingLightSurface
      )
    }
    LauncherThemeMode.RETRO_PASTEL -> {
      LauncherThemeColors(
        isDark = false,
        background = NothingRetroBackground,
        surface = NothingRetroSurface,
        elevated = NothingRetroElevated,
        border = NothingRetroBorder,
        textPrimary = NothingRetroTextPrimary,
        textSecondary = NothingRetroTextSecondary,
        unlitDot = NothingRetroUnlitDot,
        dockBg = NothingRetroDockBg.copy(alpha = 0.92f),
        dockButtonBg = Color.White,
        dockIconTint = NothingRetroAccent,
        searchPillBg = NothingRetroSurface
      )
    }
    LauncherThemeMode.SYSTEM -> {
      if (darkTheme) {
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
      } else {
        LauncherThemeColors(
          isDark = false,
          background = NothingLightBackground,
          surface = NothingLightSurface,
          elevated = NothingLightElevated,
          border = NothingLightBorder,
          textPrimary = NothingLightTextPrimary,
          textSecondary = NothingLightTextSecondary,
          unlitDot = NothingLightUnlitDot,
          dockBg = NothingLightSurface.copy(alpha = 0.92f),
          dockButtonBg = Color.White,
          dockIconTint = Color(0xFF1A1A1A),
          searchPillBg = NothingLightSurface
        )
      }
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

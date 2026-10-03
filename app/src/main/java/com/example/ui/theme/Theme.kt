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
  primary = Color(0xFF000000),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFE9E9E9),
  onPrimaryContainer = Color(0xFF000000),
  secondary = Color(0xFF333333),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFE2E2E2),
  onSecondaryContainer = Color(0xFF000000),
  tertiary = NothingRed,
  onTertiary = Color(0xFFFFFFFF),
  background = Color(0xFFF4F5F8),
  onBackground = Color(0xFF000000),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF000000),
  surfaceVariant = Color(0xFFE8E8EA),
  onSurfaceVariant = Color(0xFF333333),
  outline = Color(0xFFB8B8BC),
  outlineVariant = Color(0xFFD0D0D3)
)

// Theme 3: Atmospheric pastel/teal
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

private val NothingGlyphRedColorScheme = darkColorScheme(
  primary = NothingRed,
  onPrimary = NothingWhite,
  primaryContainer = Color(0xFF2A1113),
  onPrimaryContainer = NothingWhite,
  secondary = Color(0xFFE8E8E8),
  onSecondary = NothingBlack,
  secondaryContainer = Color(0xFF202020),
  onSecondaryContainer = NothingWhite,
  tertiary = NothingRedLight,
  background = Color(0xFF0B0B0C),
  onBackground = NothingWhite,
  surface = Color(0xFF151517),
  onSurface = NothingWhite,
  surfaceVariant = Color(0xFF202024),
  onSurfaceVariant = Color(0xFFC7C7C9),
  outline = Color(0xFF3A3A3E),
  outlineVariant = Color(0xFF29292D)
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
    LauncherThemeMode.GLYPH_RED -> NothingGlyphRedColorScheme
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
        isDark = false,
        background = Color(0xFFF4F5F8),
        surface = Color.White,
        elevated = Color(0xFFEAEAEA),
        border = Color(0xFFB8B8BC),
        textPrimary = Color(0xFF000000),
        textSecondary = Color(0xFF4A4A4F),
        unlitDot = Color(0xFFD0D0D4),
        dockBg = Color(0xF5FFFFFF),
        dockButtonBg = Color(0xFFEAEAEA),
        dockIconTint = Color(0xFF000000),
        searchPillBg = Color(0xFFF0F0F0)
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
    LauncherThemeMode.GLYPH_RED -> {
      LauncherThemeColors(
        isDark = true,
        background = Color(0xFF09090A),
        surface = Color(0xFF151517),
        elevated = Color(0xFF202024),
        border = Color(0xFF343438),
        textPrimary = NothingWhite,
        textSecondary = Color(0xFF9A9A9E),
        unlitDot = Color(0xFF26262A),
        dockBg = Color(0xE8151517),
        dockButtonBg = Color(0xFF242428),
        dockIconTint = NothingWhite,
        searchPillBg = Color(0xFF19191C)
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

package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.LauncherThemeMode

private val NothingDarkColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = NothingWhite,
    primaryContainer = NothingElevated,
    onPrimaryContainer = NothingWhite,
    secondary = NothingWhite,
    onSecondary = NothingBlack,
    background = NothingBlack,
    onBackground = NothingWhite,
    surface = NothingDarkSurface,
    onSurface = NothingWhite,
    surfaceVariant = NothingElevated,
    onSurfaceVariant = NothingGrey,
    outline = NothingBorder,
    outlineVariant = NothingDotMatrix
)

private val NothingLightColorScheme = lightColorScheme(
    primary = NothingRed,
    onPrimary = NothingWhite,
    primaryContainer = NothingLightElevated,
    onPrimaryContainer = NothingLightTextPrimary,
    secondary = NothingLightTextPrimary,
    onSecondary = NothingWhite,
    background = NothingLightBackground,
    onBackground = NothingLightTextPrimary,
    surface = NothingLightSurface,
    onSurface = NothingLightTextPrimary,
    surfaceVariant = NothingLightElevated,
    onSurfaceVariant = NothingLightTextSecondary,
    outline = NothingLightBorder,
    outlineVariant = Color(0xFFD0D0D3)
)

private val NothingRetroColorScheme = lightColorScheme(
    primary = NothingRetroAccent,
    onPrimary = NothingWhite,
    primaryContainer = Color(0xFFE4EDE7),
    onPrimaryContainer = Color(0xFF1E3A33),
    secondary = Color(0xFF23443C),
    onSecondary = NothingWhite,
    background = Color(0xFFF3F7F4),
    onBackground = Color(0xFF132822),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF132822),
    surfaceVariant = Color(0xFFE7EFEA),
    onSurfaceVariant = Color(0xFF5D7A71),
    outline = Color(0xFFD3E0D8),
    outlineVariant = NothingRetroUnlitDot
)

private val NothingGlyphRedColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = NothingWhite,
    primaryContainer = Color(0xFF2A1113),
    onPrimaryContainer = NothingWhite,
    secondary = Color(0xFFE8E8E8),
    onSecondary = Color(0xFF09090A),
    background = Color(0xFF09090A),
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
    val unlitDot: Color = NothingDotMatrix,
    val dockBg: Color = Color(0xCC141416),
    val dockButtonBg: Color = Color(0xFF222226),
    val dockIconTint: Color = NothingWhite,
    val searchPillBg: Color = Color(0xFF18181A)
)

val LocalLauncherTheme = staticCompositionLocalOf {
    LauncherThemeColors()
}

@Composable
fun NothingOSLauncherTheme(
    themeMode: LauncherThemeMode = LauncherThemeMode.ORIGINAL,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val isDarkMode = when (themeMode) {
        LauncherThemeMode.MONOCHROME_STUDIO,
        LauncherThemeMode.ATMOSPHERE_PASTEL -> false

        else -> true
    }

    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkMode) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        when (themeMode) {
            LauncherThemeMode.ORIGINAL ->
                NothingDarkColorScheme

            LauncherThemeMode.MONOCHROME_STUDIO ->
                NothingLightColorScheme

            LauncherThemeMode.ATMOSPHERE_PASTEL ->
                NothingRetroColorScheme

            LauncherThemeMode.GLYPH_RED ->
                NothingGlyphRedColorScheme
        }
    }

    val baseThemeColors = when (themeMode) {

        LauncherThemeMode.ORIGINAL -> LauncherThemeColors(
            isDark = true,
            background = NothingBlack,
            surface = NothingDarkSurface,
            elevated = NothingElevated,
            border = NothingBorder,
            textPrimary = NothingWhite,
            textSecondary = NothingGrey,
            unlitDot = NothingUnlitDot,
            dockBg = Color(0xD8141416),
            dockButtonBg = Color(0xFF222226),
            dockIconTint = NothingWhite,
            searchPillBg = Color(0xFF18181B)
        )

        LauncherThemeMode.MONOCHROME_STUDIO -> LauncherThemeColors(
            isDark = false,
            background = Color(0xFFF4F5F8),
            surface = Color(0xFFFFFFFF),
            elevated = Color(0xFFF8F9FA),
            border = Color(0xFFE2E4E8),
            textPrimary = Color(0xFF111115),
            textSecondary = Color(0xFF707076),
            unlitDot = Color(0xFFD0D0D4),
            dockBg = Color(0xF5FFFFFF),
            dockButtonBg = Color(0xFFEAEAEA),
            dockIconTint = Color(0xFF000000),
            searchPillBg = Color(0xFFF0F0F0)
        )

        LauncherThemeMode.ATMOSPHERE_PASTEL -> LauncherThemeColors(
            isDark = false,
            background = Color(0xFFF1F5F2),
            surface = Color(0xFFFFFFFF),
            elevated = Color(0xFFE7ECE8),
            border = Color(0xFFD4DED7),
            textPrimary = Color(0xFF1B2F29),
            textSecondary = Color(0xFF678278),
            unlitDot = NothingRetroUnlitDot,
            dockBg = Color(0xE8FFFFFF),
            dockButtonBg = Color(0xFFE2ECE5),
            dockIconTint = Color(0xFF203830),
            searchPillBg = Color(0xFFEEF3EC)
        )

        LauncherThemeMode.GLYPH_RED -> LauncherThemeColors(
            isDark = true,
            background = Color(0xFF09090A),
            surface = Color(0xFF151517),
            elevated = Color(0xFF202024),
            border = Color(0xFF333338),
            textPrimary = NothingWhite,
            textSecondary = Color(0xFFA0A0A5),
            unlitDot = Color(0xFF26262A),
            dockBg = Color(0xE8151517),
            dockButtonBg = Color(0xFF242428),
            dockIconTint = NothingWhite,
            searchPillBg = Color(0xFF19191C)
        )
    }

    val themeColors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        baseThemeColors.copy(
            isDark = isDarkMode,
            background = colorScheme.background,
            surface = colorScheme.surface,
            elevated = colorScheme.surfaceVariant,
            border = colorScheme.outlineVariant,
            textPrimary = colorScheme.onBackground,
            textSecondary = colorScheme.onSurfaceVariant,
            dockBg = colorScheme.surface.copy(
                alpha = if (isDarkMode) 0.86f else 0.94f
            ),
            dockButtonBg = colorScheme.surfaceVariant,
            dockIconTint = colorScheme.onSurface,
            searchPillBg = colorScheme.surfaceVariant
        )
    } else {
        baseThemeColors
    }

    CompositionLocalProvider(
        LocalLauncherTheme provides themeColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    NothingOSLauncherTheme(
        themeMode = LauncherThemeMode.ORIGINAL,
        content = content
    )
}

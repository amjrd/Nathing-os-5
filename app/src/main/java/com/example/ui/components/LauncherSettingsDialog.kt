package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsDialog(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onDismiss: () -> Unit
) {
  val theme = LocalLauncherTheme.current
  val accentColor = ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth(0.92f)
      .clip(RoundedCornerShape(28.dp))
      .background(theme.background)
      .border(1.dp, theme.border, RoundedCornerShape(28.dp))
      .padding(20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "LAUNCHER CUSTOMIZATION",
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary,
          letterSpacing = 1.sp
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textPrimary)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. Theme Mode
      Text(
        text = "THEME MODE",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf(
          Triple(LauncherThemeMode.ORIGINAL, "ORIGINAL", Icons.Default.DarkMode),
          Triple(LauncherThemeMode.MONOCHROME_STUDIO, "STUDIO", Icons.Default.LightMode),
          Triple(LauncherThemeMode.ATMOSPHERE_PASTEL, "AURA", Icons.Default.Palette),
          Triple(LauncherThemeMode.GLYPH_RED, "GLYPH", Icons.Default.Radio)
        ).forEach { (mode, label, icon) ->
          val isSelected = settings.themeMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) accentColor else theme.surface)
              .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(14.dp))
              .clickable {
                val updated = when (mode) {
                  LauncherThemeMode.ORIGINAL -> settings.copy(themeMode = mode, wallpaperIndex = 0)
                  LauncherThemeMode.MONOCHROME_STUDIO -> settings.copy(themeMode = mode, wallpaperIndex = 1)
                  LauncherThemeMode.ATMOSPHERE_PASTEL -> settings.copy(themeMode = mode, wallpaperIndex = 2)
                  LauncherThemeMode.GLYPH_RED -> settings.copy(themeMode = mode, wallpaperIndex = 4)
                }
                onUpdateSettings(updated)
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else theme.textSecondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else theme.textSecondary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. Icon Pack Style
      Text(
        text = "ICON PACK",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Pair(IconPackStyle.MONOCHROME, "MONO"),
          Pair(IconPackStyle.COLORFUL, "COLORED"),
          Pair(IconPackStyle.NOTHING_ORIGINAL, "NOTHING")
        ).forEach { (pack, label) ->
          val isSelected = settings.iconPack == pack
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) accentColor else theme.surface)
              .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(14.dp))
              .clickable { onUpdateSettings(settings.copy(iconPack = pack)) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else theme.textSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Colored Icons in Drawer Toggle (Feature: "1 app drawer coulor icond")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "DRAWER COLOR ICONS",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "Show original colorful app icons in drawer",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = theme.textSecondary
          )
        }
        Switch(
          checked = settings.drawerColoredIcons,
          onCheckedChange = { onUpdateSettings(settings.copy(drawerColoredIcons = it)) },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = accentColor,
            uncheckedThumbColor = theme.textSecondary,
            uncheckedTrackColor = theme.elevated
          )
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 4. Accent Color
      Text(
        text = "ACCENT COLOR",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        ACCENT_COLORS.forEachIndexed { index, color ->
          val isSelected = settings.accentColorIndex == index
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(color)
              .border(
                2.dp,
                if (isSelected) theme.textPrimary else Color.Transparent,
                CircleShape
              )
              .clickable { onUpdateSettings(settings.copy(accentColorIndex = index)) },
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (color == Color.White) Color.Black else Color.White)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 5. Icon Size Selector
      Text(
        text = "ICON SCALE",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Pair(0, "44DP"),
          Pair(1, "52DP"),
          Pair(2, "60DP"),
          Pair(3, "68DP")
        ).forEach { (level, label) ->
          val isSelected = settings.iconSizeLevel == level
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) accentColor else theme.surface)
              .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(12.dp))
              .clickable { onUpdateSettings(settings.copy(iconSizeLevel = level)) }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else theme.textSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

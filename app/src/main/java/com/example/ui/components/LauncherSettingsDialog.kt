package com.example.ui.components

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.model.NosWidgetPortType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.model.IconPackStyle
import com.example.model.LauncherClockStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.LockClockStyle
import com.example.model.LockSecurityType
import com.example.model.LockShortcutType
import com.example.model.WallpaperTarget
import com.example.service.SystemIntegrationHelper
import com.example.service.SystemPortHelper
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingMatteBlack
import com.example.ui.theme.NothingOrange
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import com.example.ui.theme.NothingYellow
import java.io.File

val ACCENT_COLORS = listOf(
  NothingRed,
  NothingWhite,
  NothingOrange,
  NothingYellow
)

data class WallpaperChoice(
  val index: Int,
  val name: String,
  val badge: String,
  val desc: String
)

val WALLPAPER_CHOICES = listOf(
  WallpaperChoice(0, "NOTHING ORIGINAL", "CLASSIC", "Nothing signature Noir dot matrix"),
  WallpaperChoice(1, "MONOCHROME STUDIO", "PHOTO 1", "Artistic high-contrast black & white bokeh (Image 1)"),
  WallpaperChoice(2, "ATMOSPHERE PASTEL", "PHOTO 2", "Ambient aura mint green & lavender glow (Image 2)"),
  WallpaperChoice(3, "CUSTOM GALLERY PHOTO", "GALLERY", "Personal photo loaded from phone storage")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsDialog(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onPickCustomWallpaper: (android.net.Uri, WallpaperTarget) -> Unit = { _, _ -> },
  onRemoveCustomWallpaper: (WallpaperTarget) -> Unit = {},
  onOpenWidgetCustomizer: () -> Unit = {},
  onLockScreenNow: () -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  // Selected tab: 0 = HOME, 1 = THEMES, 2 = SYSTEM
  var selectedTab by remember { mutableIntStateOf(0) }
  var wallpaperTarget by remember { mutableStateOf(WallpaperTarget.BOTH) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onPickCustomWallpaper(uri, wallpaperTarget)
    }
  }

  // Permission statuses
  var isNotificationGranted by remember { mutableStateOf(SystemIntegrationHelper.isNotificationListenerGranted(context)) }
  var isOverlayGranted by remember { mutableStateOf(SystemIntegrationHelper.isOverlayGranted(context)) }
  var isAccessibilityGranted by remember { mutableStateOf(SystemIntegrationHelper.isAccessibilityGranted(context)) }
  var isUsageGranted by remember { mutableStateOf(SystemIntegrationHelper.isUsageAccessGranted(context)) }
  var isDndGranted by remember { mutableStateOf(SystemIntegrationHelper.isDndPolicyGranted(context)) }
  var isDefaultLauncher by remember { mutableStateOf(SystemIntegrationHelper.isDefaultLauncher(context)) }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isNotificationGranted = SystemIntegrationHelper.isNotificationListenerGranted(context)
        isOverlayGranted = SystemIntegrationHelper.isOverlayGranted(context)
        isAccessibilityGranted = SystemIntegrationHelper.isAccessibilityGranted(context)
        isUsageGranted = SystemIntegrationHelper.isUsageAccessGranted(context)
        isDndGranted = SystemIntegrationHelper.isDndPolicyGranted(context)
        isDefaultLauncher = SystemIntegrationHelper.isDefaultLauncher(context)
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val theme = LocalLauncherTheme.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.background,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .testTag("launcher_settings_dialog")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accentColor)
            )
            Text(
              text = "NOTHING OS 5.0",
              fontFamily = FontFamily.Monospace,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 2.sp
            )
          }
          Text(
            text = "LAUNCHER PREFERENCES",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = theme.textSecondary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = theme.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tab selector row (3 Clean Tabs)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(theme.surface)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        TabButton(
          title = "HOME",
          icon = Icons.Default.Home,
          selected = selectedTab == 0,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          com.example.util.VibrationHelper.vibrateTouch(context)
          selectedTab = 0
        }

        TabButton(
          title = "THEMES",
          icon = Icons.Default.Palette,
          selected = selectedTab == 1,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          com.example.util.VibrationHelper.vibrateTouch(context)
          selectedTab = 1
        }

        TabButton(
          title = "SYSTEM",
          icon = Icons.Default.Security,
          selected = selectedTab == 2,
          accentColor = accentColor,
          modifier = Modifier.weight(1f)
        ) {
          com.example.util.VibrationHelper.vibrateTouch(context)
          selectedTab = 2
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      when (selectedTab) {

        // TAB 0: HOME SCREEN & ICONS
        0 -> {
          // Icon Pack Style (Screenshot 4: Default, Nothing, Colour)
          Text(
            text = "ICON PACK STYLE (STYLE D'ICÔNE)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          VisualIconPackSwitcher(
            selectedPack = settings.iconPack,
            accentColor = accentColor,
            onSelectPack = { pack -> onUpdateSettings(settings.copy(iconPack = pack)) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Grid Columns (4x4 vs 5x5)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GRID DENSITY",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = NothingWhite
              )
              Text(
                text = "${settings.gridColumns} Columns Layout",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NothingGrey
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(4, 5).forEach { cols ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.gridColumns == cols) accentColor else NothingElevated)
                    .clickable { onUpdateSettings(settings.copy(gridColumns = cols)) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = "${cols}x${cols}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NothingWhite
                  )
                }
              }
            }
          }

          // 1. Icon Size (Taille des icônes)
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ICON SIZE (TAILLE)",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
              )
              Text(
                text = when (settings.iconSizeLevel) {
                  0 -> "Small (44dp)"
                  1 -> "Standard (52dp)"
                  2 -> "Large (60dp)"
                  else -> "Extra (68dp)"
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(
                0 to "S",
                1 to "M",
                2 to "L",
                3 to "XL"
              ).forEach { (level, lbl) ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.iconSizeLevel == level) accentColor else NothingElevated)
                    .clickable { onUpdateSettings(settings.copy(iconSizeLevel = level)) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = lbl,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (settings.iconSizeLevel == level) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "APP DRAWER CARDS",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
              )
              Text(
                text = when (settings.drawerCardSizeLevel) {
                  0 -> "Compact"
                  1 -> "Standard"
                  else -> "Large"
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(0 to "S", 1 to "M", 2 to "L").forEach { (level, label) ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.drawerCardSizeLevel == level) accentColor else NothingElevated)
                    .clickable { onUpdateSettings(settings.copy(drawerCardSizeLevel = level)) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = label,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (settings.drawerCardSizeLevel == level) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textPrimary
                  )
                }
              }
            }
          }

          // 2. Widget Scale (Échelle des widgets)
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "WIDGET SCALE (ÉCHELLE)",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
              )
              Text(
                text = when (settings.widgetSizeLevel) {
                  0 -> "Compact (85%)"
                  1 -> "Standard (100%)"
                  else -> "Expanded (115%)"
                },
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(
                0 to "MINI",
                1 to "STD",
                2 to "MAX"
              ).forEach { (level, lbl) ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.widgetSizeLevel == level) accentColor else NothingElevated)
                    .clickable { onUpdateSettings(settings.copy(widgetSizeLevel = level)) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = lbl,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (settings.widgetSizeLevel == level) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          SettingsSwitchRow(
            title = "SHOW APP LABELS",
            subtitle = "Display app titles underneath icons",
            checked = settings.showLabels,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(showLabels = it)) }
          )

          Spacer(modifier = Modifier.height(14.dp))

          SettingsSwitchRow(
            title = "DOCK SEARCH BAR",
            subtitle = "Show Google / App search on bottom dock",
            checked = settings.showSearchBarOnDock,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(showSearchBarOnDock = it)) }
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Customize NOS Widgets launcher button inside settings
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(theme.surface)
              .border(1.dp, theme.border, RoundedCornerShape(12.dp))
              .clickable { onOpenWidgetCustomizer() }
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
              )
              Column {
                Text(
                  text = "CUSTOMIZE WIDGETS",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = theme.textPrimary
                )
                Text(
                  text = "Enable or disable Nothing OS 5.0 home widgets",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = theme.textSecondary
                )
              }
            }
          }
        }

        // TAB 1: THEMES & WALLPAPERS
        1 -> {
          // 1. LAUNCHER THEME MODE: THEME JOUR (Image 3) vs THEME NUIT (Image 2) vs SYSTEM
          Text(
            text = "THEME MODE (THÈME DU LAUNCHER)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              Triple(LauncherThemeMode.ORIGINAL, "NOTHING 2", Icons.Default.DarkMode),
              Triple(LauncherThemeMode.MONOCHROME_STUDIO, "MONO", Icons.Default.Layers),
              Triple(LauncherThemeMode.ATMOSPHERE_PASTEL, "AURA", Icons.Default.Palette),
              Triple(LauncherThemeMode.GLYPH_RED, "GLYPH", Icons.Default.Radio)
            ).forEach { (mode, label, icon) ->
              val isSelected = settings.themeMode == mode
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) accentColor else theme.surface)
                  .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(10.dp))
                  .clickable {
                    val updatedSettings = when (mode) {
                      LauncherThemeMode.ORIGINAL -> settings.copy(
                        themeMode = mode,
                        wallpaperIndex = 0,
                        accentColorIndex = 0,
                        clockStyle = LauncherClockStyle.DIGITAL,
                        iconPack = IconPackStyle.MONOCHROME,
                        activeWidgets = listOf(
                          NosWidgetPortType.CLOCK_MAIN,
                          NosWidgetPortType.WEATHER_MAIN,
                          NosWidgetPortType.MINI_CLUSTER_2X2
                        )
                      )
                      LauncherThemeMode.MONOCHROME_STUDIO -> settings.copy(
                        themeMode = mode,
                        wallpaperIndex = 1,
                        accentColorIndex = 1,
                        clockStyle = LauncherClockStyle.ANALOG,
                        iconPack = IconPackStyle.SYSTEM_DEFAULT,
                        activeWidgets = listOf(
                          NosWidgetPortType.GIANT_CIRCLES_CLUSTER,
                          NosWidgetPortType.CALENDAR_DIGITAL_TIME
                        )
                      )
                      LauncherThemeMode.ATMOSPHERE_PASTEL -> settings.copy(
                        themeMode = mode,
                        wallpaperIndex = 2,
                        accentColorIndex = 1,
                        clockStyle = LauncherClockStyle.ANALOG,
                        iconPack = IconPackStyle.SYSTEM_DEFAULT,
                        activeWidgets = listOf(
                          NosWidgetPortType.MINI_CLUSTER_2X2,
                          NosWidgetPortType.CLOCK_MAIN,
                          NosWidgetPortType.WEATHER_MAIN
                        )
                      )
                      LauncherThemeMode.GLYPH_RED -> settings.copy(
                        themeMode = mode,
                        wallpaperIndex = 0,
                        accentColorIndex = 0,
                        clockStyle = LauncherClockStyle.DIGITAL,
                        iconPack = IconPackStyle.MONOCHROME,
                        activeWidgets = listOf(
                          NosWidgetPortType.CALENDAR_DIGITAL_TIME,
                          NosWidgetPortType.GIANT_CIRCLES_CLUSTER,
                          NosWidgetPortType.CLOCK_MAIN
                        )
                      )
                    }
                    onUpdateSettings(updatedSettings)
                  }
                  .padding(vertical = 10.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textSecondary,
                    modifier = Modifier.size(12.dp)
                  )
                  Text(
                    text = label,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 1.5. ICON PACK SWITCHER (Screenshot 4: Default, Nothing, Colour)
          Text(
            text = "ICON PACK STYLE (STYLE D'ICÔNE)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          VisualIconPackSwitcher(
            selectedPack = settings.iconPack,
            accentColor = accentColor,
            onSelectPack = { pack -> onUpdateSettings(settings.copy(iconPack = pack)) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          // 2. HOME CLOCK STYLE: DOT MATRIX vs ANALOG ROUND (Image 3)
          Text(
            text = "HOME CLOCK STYLE (STYLE D'HORLOGE)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              LauncherClockStyle.DIGITAL to "DOT MATRIX",
              LauncherClockStyle.ANALOG to "ANALOG ROUND"
            ).forEach { (style, label) ->
              val isSelected = settings.clockStyle == style
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) accentColor else theme.surface)
                  .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(10.dp))
                  .clickable { onUpdateSettings(settings.copy(clockStyle = style)) }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else theme.textPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Accent Color
          Text(
            text = "NOTHING ACCENT COLOR",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            ACCENT_COLORS.forEachIndexed { index, color ->
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(color)
                  .border(
                    width = if (settings.accentColorIndex == index) 2.dp else 0.dp,
                    color = if (settings.accentColorIndex == index) NothingWhite else Color.Transparent,
                    shape = CircleShape
                  )
                  .clickable { onUpdateSettings(settings.copy(accentColorIndex = index)) },
                contentAlignment = Alignment.Center
              ) {
                if (settings.accentColorIndex == index) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color == NothingWhite) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(22.dp))

          // Wallpaper Destination Selector
          Text(
            text = "APPLY WALLPAPER TO",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              WallpaperTarget.HOME to "HOME ONLY",
              WallpaperTarget.LOCK to "LOCK ONLY",
              WallpaperTarget.BOTH to "BOTH SCREENS"
            ).forEach { (target, label) ->
              val isSelected = wallpaperTarget == target
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) accentColor else NothingDarkSurface)
                  .border(1.dp, if (isSelected) accentColor else NothingBorder, RoundedCornerShape(10.dp))
                  .clickable { wallpaperTarget = target }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else NothingGrey
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Custom Gallery Photo Launcher Button
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(NothingDarkSurface)
              .border(1.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AddPhotoAlternate,
                  contentDescription = "Pick photo",
                  tint = accentColor,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "CHOOSE PHOTO FROM GALLERY",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NothingWhite
                )
                Text(
                  text = "Select any picture from device storage",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = NothingGrey
                )
              }
            }
            Icon(
              imageVector = Icons.Default.Image,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
          }

          // Active Custom Photo status indicator
          val activeCustomUri = when (wallpaperTarget) {
            WallpaperTarget.HOME -> settings.customWallpaperUri
            WallpaperTarget.LOCK -> settings.customLockScreenWallpaperUri ?: settings.customWallpaperUri
            WallpaperTarget.BOTH -> settings.customWallpaperUri ?: settings.customLockScreenWallpaperUri
          }
          if (!activeCustomUri.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NothingElevated)
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              val photoFile = File(activeCustomUri)
              AsyncImage(
                model = if (photoFile.exists()) photoFile else activeCustomUri,
                contentDescription = "Wallpaper preview",
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
              )
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ACTIVE CUSTOM PHOTO",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = accentColor
                )
                Text(
                  text = "Wallpaper photo active on device",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = NothingGrey
                )
              }

              // Red Box Action: Delete / Clear Custom Photo
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(NothingRed.copy(alpha = 0.16f))
                  .border(1.dp, NothingRed, RoundedCornerShape(8.dp))
                  .clickable { onRemoveCustomWallpaper(wallpaperTarget) }
                  .padding(horizontal = 8.dp, vertical = 6.dp)
                  .testTag("delete_custom_photo_button"),
                contentAlignment = Alignment.Center
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف الصورة",
                    tint = NothingRed,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "DELETE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingRed
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Dimming Scrim
          Text(
            text = "PHOTO DIMMING / CONTRAST",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Darkens custom photos so icons and clock remain sharp",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = NothingGrey
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(
              0 to "OFF (0%)",
              25 to "LIGHT (25%)",
              40 to "MEDIUM (40%)",
              60 to "DEEP (60%)"
            ).forEach { (pct, label) ->
              val isSelected = settings.wallpaperDimPct == pct
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) accentColor else NothingDarkSurface)
                  .border(1.dp, if (isSelected) accentColor else NothingBorder, RoundedCornerShape(8.dp))
                  .clickable { onUpdateSettings(settings.copy(wallpaperDimPct = pct)) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) (if (accentColor == NothingWhite) Color.Black else Color.White) else NothingGrey
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Nothing OS 5 Wallpapers List
          Text(
            text = "NOTHING OS 5 WALLPAPERS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WALLPAPER_CHOICES.forEach { choice ->
              val isCurrent = when (wallpaperTarget) {
                WallpaperTarget.HOME -> settings.wallpaperIndex == choice.index
                WallpaperTarget.LOCK -> if (settings.lockScreenWallpaperIndex >= 0) settings.lockScreenWallpaperIndex == choice.index else settings.wallpaperIndex == choice.index
                WallpaperTarget.BOTH -> settings.wallpaperIndex == choice.index && (settings.lockScreenWallpaperIndex < 0 || settings.lockScreenWallpaperIndex == choice.index)
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isCurrent) NothingElevated else NothingDarkSurface)
                  .border(
                    width = 1.dp,
                    color = if (isCurrent) accentColor else NothingBorder,
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable {
                    if (choice.index == 3 && (settings.customWallpaperUri == null && settings.customLockScreenWallpaperUri == null)) {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    } else {
                      when (wallpaperTarget) {
                        WallpaperTarget.HOME -> onUpdateSettings(settings.copy(wallpaperIndex = choice.index))
                        WallpaperTarget.LOCK -> onUpdateSettings(settings.copy(lockScreenWallpaperIndex = choice.index))
                        WallpaperTarget.BOTH -> onUpdateSettings(settings.copy(wallpaperIndex = choice.index, lockScreenWallpaperIndex = choice.index))
                      }
                    }
                  }
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = choice.name,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = NothingWhite
                    )
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = choice.badge,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = choice.desc,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NothingGrey
                  )
                }

                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          SettingsSwitchRow(
            title = "HAPTIC FEEDBACK",
            subtitle = "Subtle vibration clicks on touch",
            checked = settings.hapticFeedbackEnabled,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedbackEnabled = it)) }
          )
        }

        // TAB 2: SYSTEM PERMISSIONS & GESTURES
        2 -> {
          // 0. NOTHING OS 5 LOCK SCREEN (ÉCRAN DE VERROUILLAGE)
          Text(
            text = "NOTHING OS 5 LOCK SCREEN (ÉCRAN DE VERROUILLAGE)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          SettingsSwitchRow(
            title = "ENABLE LOCK SCREEN (ACTIVÉ PAR DÉFAUT)",
            subtitle = "Shows Nothing OS 5 Lock Screen on launch & sleep wake",
            checked = settings.lockScreen.isLockScreenEnabled,
            accentColor = accentColor,
            onCheckedChange = { isEnabled ->
              onUpdateSettings(
                settings.copy(
                  lockScreen = settings.lockScreen.copy(isLockScreenEnabled = isEnabled)
                )
              )
            }
          )

          // Warning banner if Superposition is Disabled on Android device
          if (settings.lockScreen.isLockScreenEnabled && !isOverlayGranted) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF2C1E0F))
                .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(10.dp))
                .clickable { SystemIntegrationHelper.requestOverlayPermission(context) }
                .padding(10.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Layers,
                  contentDescription = null,
                  tint = Color(0xFFFF9800),
                  modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "SUPERPOSITION REQUISE (DÉSACTIVÉE)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                  )
                  Text(
                    text = "Activez 'Superposition sur d'autres applis' pour afficher l'écran de verrouillage.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = Color(0xFFFFCC80)
                  )
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF9800))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "ACTIVER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Lock Screen Test & Lock Button
          Button(
            onClick = {
              com.example.util.VibrationHelper.vibrateTouch(context)
              onLockScreenNow()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = accentColor,
              contentColor = if (accentColor == Color.White) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "TEST LOCK SCREEN NOW (VERROUILLER)",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Security Type: Swipe vs PIN
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "SECURITY TYPE",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
              )
              Text(
                text = if (settings.lockScreen.securityType == LockSecurityType.SWIPE) "Swipe up to unlock" else "4-digit PIN lock",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(
                LockSecurityType.SWIPE to "SWIPE",
                LockSecurityType.PIN to "PIN"
              ).forEach { (type, label) ->
                val isSelected = settings.lockScreen.securityType == type
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) accentColor else theme.surface)
                    .border(1.dp, if (isSelected) accentColor else theme.border, RoundedCornerShape(8.dp))
                    .clickable {
                      onUpdateSettings(
                        settings.copy(
                          lockScreen = settings.lockScreen.copy(securityType = type)
                        )
                      )
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = label,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isSelected) (if (accentColor == Color.White) Color.Black else Color.White) else theme.textPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Gestures
          Text(
            text = "SYSTEM GESTURES",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingGrey,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          SettingsSwitchRow(
            title = "DOUBLE TAP TO SLEEP",
            subtitle = "Double tap home screen to lock",
            checked = settings.doubleTapToSleep,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(doubleTapToSleep = it)) }
          )

          Spacer(modifier = Modifier.height(10.dp))

          SettingsSwitchRow(
            title = "SWIPE DOWN FOR NOTIFICATIONS",
            subtitle = "Pull down anywhere on home for shade",
            checked = settings.swipeDownNotifications,
            accentColor = accentColor,
            onCheckedChange = { onUpdateSettings(settings.copy(swipeDownNotifications = it)) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          Text(
            text = "DEEP SYSTEM PERMISSIONS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.Notifications,
            title = "NOTIFICATION LISTENER",
            description = "Enables real-time app badges and lockscreen notifications",
            isGranted = isNotificationGranted,
            actionLabel = if (isNotificationGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openNotificationListenerSettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.Layers,
            title = "SUPERPOSITION (OVERLAY)",
            description = "Enables floating widgets & gesture overlays",
            isGranted = isOverlayGranted,
            actionLabel = if (isOverlayGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.requestOverlayPermission(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.AccessibilityNew,
            title = "ACCESSIBILITY SERVICE",
            description = "Enables native gestures (lock screen & shade pull)",
            isGranted = isAccessibilityGranted,
            actionLabel = if (isAccessibilityGranted) "ACTIVE" else "ENABLE",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openAccessibilitySettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.QueryStats,
            title = "USAGE STATS ACCESS",
            description = "Calculates screen time & frequent apps",
            isGranted = isUsageGranted,
            actionLabel = if (isUsageGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openUsageAccessSettings(context) }
          )

          Spacer(modifier = Modifier.height(8.dp))

          PermissionIntegrationCard(
            icon = Icons.Default.DoNotDisturb,
            title = "DND / SOUND ACCESS",
            description = "Toggles Silent & Vibrate mode from widget",
            isGranted = isDndGranted,
            actionLabel = if (isDndGranted) "ACTIVE" else "GRANT",
            accentColor = accentColor,
            onClick = { SystemIntegrationHelper.openDndPolicySettings(context) }
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = { SystemIntegrationHelper.openDefaultLauncherSettings(context) },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDefaultLauncher) NothingDarkSurface else accentColor,
              contentColor = NothingWhite
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .border(
                width = if (isDefaultLauncher) 1.dp else 0.dp,
                color = if (isDefaultLauncher) Color(0xFF00E676) else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
              )
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = if (isDefaultLauncher) Icons.Default.Check else Icons.Default.Home,
                contentDescription = null,
                tint = if (isDefaultLauncher) Color(0xFF00E676) else NothingWhite,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (isDefaultLauncher) "DEFAULT LAUNCHER (ACTIVE)" else "SET AS DEFAULT LAUNCHER",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // System Build & Version Info
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(theme.surface)
              .border(1.dp, theme.border, RoundedCornerShape(12.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "NOTHING OS 5 • RELEASE",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = accentColor
                )
                Text(
                  text = "v1.6.0",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = theme.textPrimary
                )
              }
              Text(
                text = "Build 7 (Stable Production) • 120Hz Ultra Smooth",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = theme.textSecondary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun TabButton(
  title: String,
  icon: ImageVector,
  selected: Boolean,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (selected) accentColor else Color.Transparent)
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (selected) (if (accentColor == NothingWhite) Color.Black else NothingWhite) else NothingGrey,
        modifier = Modifier.size(13.dp)
      )
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (selected) (if (accentColor == NothingWhite) Color.Black else NothingWhite) else NothingGrey
      )
    }
  }
}

@Composable
private fun SettingsSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  accentColor: Color,
  onCheckedChange: (Boolean) -> Unit
) {
  val theme = LocalLauncherTheme.current
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
      Text(
        text = subtitle,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = theme.textSecondary
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = if (accentColor == Color.White) Color.Black else Color.White,
        checkedTrackColor = accentColor,
        uncheckedTrackColor = theme.elevated
      )
    )
  }
}

@Composable
private fun PermissionIntegrationCard(
  icon: ImageVector,
  title: String,
  description: String,
  isGranted: Boolean,
  actionLabel: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  val theme = LocalLauncherTheme.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(theme.surface)
      .border(
        width = 1.dp,
        color = if (isGranted) Color(0xFF00E676).copy(alpha = 0.35f) else theme.border,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable { onClick() }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(if (isGranted) Color(0xFF00E676).copy(alpha = 0.15f) else theme.elevated),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = if (isGranted) Color(0xFF00E676) else theme.textSecondary,
          modifier = Modifier.size(16.dp)
        )
      }

      Column {
        Text(
          text = title,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary
        )
        Text(
          text = description,
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = theme.textSecondary,
          lineHeight = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.size(8.dp))

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(
          if (isGranted) Color(0xFF00E676).copy(alpha = 0.18f) else accentColor
        )
        .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
      Text(
        text = actionLabel,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (isGranted) Color(0xFF00E676) else (if (accentColor == Color.White) Color.Black else Color.White)
      )
    }
  }
}



/**
 * Visual Icon Pack Switcher Component matching Screenshot 4:
 * Displays side-by-side:
 * - "Default": Concentric circular badge
 * - "Nothing": Dark circle badge with overlapping square/circle glyph
 * - "Colour": 12-lobed scalloped flower badge in soft pastel blue
 */
@Composable
fun VisualIconPackSwitcher(
  selectedPack: IconPackStyle,
  accentColor: Color,
  onSelectPack: (IconPackStyle) -> Unit,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(16.dp))
      .padding(vertical = 16.dp, horizontal = 8.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // 1. Default (System Default)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onSelectPack(IconPackStyle.SYSTEM_DEFAULT) }
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(58.dp)
          .clip(CircleShape)
          .background(Color(0xFF28303C))
          .border(
            width = if (selectedPack == IconPackStyle.SYSTEM_DEFAULT) 3.dp else 1.5.dp,
            color = if (selectedPack == IconPackStyle.SYSTEM_DEFAULT) Color(0xFF90CAF9) else theme.border,
            shape = CircleShape
          )
          .padding(7.dp)
          .clip(CircleShape)
          .background(Color(0xFF5A6E85)),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F1520))
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Default",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = if (selectedPack == IconPackStyle.SYSTEM_DEFAULT) FontWeight.Bold else FontWeight.Normal,
        color = if (selectedPack == IconPackStyle.SYSTEM_DEFAULT) theme.textPrimary else theme.textSecondary
      )
    }

    // 2. Nothing (Monochrome)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onSelectPack(IconPackStyle.MONOCHROME) }
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(58.dp)
          .clip(CircleShape)
          .background(Color(0xFF121212))
          .border(
            width = if (selectedPack == IconPackStyle.MONOCHROME) 3.dp else 1.5.dp,
            color = if (selectedPack == IconPackStyle.MONOCHROME) NothingWhite else theme.border,
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        // Overlapping circle & square glyph (Screenshot 4)
        Box(
          modifier = Modifier
            .offset(x = (-4).dp, y = (-4).dp)
            .size(16.dp)
            .clip(CircleShape)
            .background(Color.White)
        )
        Box(
          modifier = Modifier
            .offset(x = 4.dp, y = 4.dp)
            .size(14.dp)
            .clip(RoundedCornerShape(3.dp))
            .border(1.5.dp, Color.White, RoundedCornerShape(3.dp))
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Nothing",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = if (selectedPack == IconPackStyle.MONOCHROME) FontWeight.Bold else FontWeight.Normal,
        color = if (selectedPack == IconPackStyle.MONOCHROME) theme.textPrimary else theme.textSecondary
      )
    }

    // 3. Colour (12-lobed Scalloped Flower Badge - Screenshot 4)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onSelectPack(IconPackStyle.COLOUR) }
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      val flowerShape = remember { ScallopedFlowerShape(lobes = 12) }
      Box(
        modifier = Modifier
          .size(58.dp)
          .clip(flowerShape)
          .background(Color(0xFFD6E4FF))
          .border(
            width = if (selectedPack == IconPackStyle.COLOUR) 3.dp else 1.5.dp,
            color = if (selectedPack == IconPackStyle.COLOUR) accentColor else Color(0xFFB4C8F5),
            shape = flowerShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E2D4B).copy(alpha = 0.4f))
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Colour",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = if (selectedPack == IconPackStyle.COLOUR) FontWeight.Bold else FontWeight.Normal,
        color = if (selectedPack == IconPackStyle.COLOUR) theme.textPrimary else theme.textSecondary
      )
    }
  }
}


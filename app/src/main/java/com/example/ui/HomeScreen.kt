package com.example.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.LauncherSettings
import com.example.model.NosWidgetPortType
import com.example.model.WeatherData
import com.example.service.SystemPortHelper
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppIconItem
import com.example.ui.components.EnlargedFolderView
import com.example.ui.components.LauncherSettingsDialog
import com.example.ui.components.NosCalendarDigitalTimeWidget
import com.example.ui.components.NosClockWidget
import com.example.ui.components.NosEarBatteryWidget
import com.example.ui.components.NosWatchStatsWidget
import com.example.ui.components.NosWeatherWidget
import com.example.ui.components.NosWidgetPortSheet
import com.example.ui.components.NothingAppInfoSheet
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.theme.LocalLauncherTheme
import com.example.util.VibrationHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  currentTime: String,
  currentDate: String,
  weather: WeatherData,
  audioState: AudioState,
  fitnessStats: FitnessStats,
  dockApps: List<AppItem>,
  pinnedApps: List<AppItem>,
  folders: List<FolderItem>,
  allApps: List<AppItem>,
  settings: LauncherSettings,
  onAppClick: (AppItem) -> Unit,
  onOpenAppDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onToggleDockApp: (AppItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current
  val haptic = LocalHapticFeedback.current
  val accentColor = ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }

  var isWidgetSheetOpen by remember { mutableStateOf(false) }
  var isSettingsDialogOpen by remember { mutableStateOf(false) }
  var selectedAppForInfo by remember { mutableStateOf<AppItem?>(null) }
  var searchQuery by remember { mutableStateOf("") }

  val currentIconSize = when (settings.iconSizeLevel) {
    0 -> 44.dp
    2 -> 60.dp
    3 -> 68.dp
    else -> 52.dp
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen_container")
  ) {
    // Wallpaper Layer
    NothingWallpaperBackground(
      wallpaperIndex = settings.wallpaperIndex,
      themeMode = settings.themeMode,
      wallpaperDimPct = settings.wallpaperDimPct,
      customWallpaperUri = settings.customWallpaperUri
    )

    // Main Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Status Bar: Quick Action Pills
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Pill: NOTHING OS 5
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surface.copy(alpha = 0.82f))
            .border(1.dp, theme.border.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = "NOTHING OS 5",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 1.sp
          )
        }

        // Right Quick Actions: Widgets + Customization
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Widgets Sheet Trigger
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(theme.surface.copy(alpha = 0.82f))
              .border(1.dp, theme.border, CircleShape)
              .clickable {
                VibrationHelper.vibrateTouch(context)
                isWidgetSheetOpen = true
              }
              .testTag("home_widget_sheet_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Widgets,
              contentDescription = "Widgets",
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
          }

          // Customization Dialog Trigger
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(theme.surface.copy(alpha = 0.82f))
              .border(1.dp, theme.border, CircleShape)
              .clickable {
                VibrationHelper.vibrateTouch(context)
                isSettingsDialogOpen = true
              }
              .testTag("home_settings_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Palette,
              contentDescription = "Customization",
              tint = theme.textPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Scrollable Widgets & Apps Body
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Active Nothing OS Widgets
        if (settings.activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME)) {
          item {
            NosCalendarDigitalTimeWidget(
              currentTime = currentTime,
              currentDate = currentDate,
              accentColor = accentColor,
              onClick = { SystemPortHelper.launchClock(context) }
            )
          }
        }

        if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
          item {
            NosClockWidget(
              currentTime = currentTime,
              accentColor = accentColor,
              onClick = { SystemPortHelper.launchClock(context) }
            )
          }
        }

        if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)) {
          item {
            NosWeatherWidget(
              weather = weather,
              accentColor = accentColor,
              onClick = { SystemPortHelper.launchWeather(context, weather.city) }
            )
          }
        }

        // Ear / Casque widget
        if (settings.activeWidgets.contains(NosWidgetPortType.EAR_BATTERY)) {
          item {
            NosEarBatteryWidget(
              audioState = audioState,
              accentColor = accentColor,
              onClick = { SystemPortHelper.launchAudioDeviceSettings(context) }
            )
          }
        }

        // Smartwatch widget
        if (settings.activeWidgets.contains(NosWidgetPortType.WATCH_STATS)) {
          item {
            NosWatchStatsWidget(
              fitness = fitnessStats,
              accentColor = accentColor,
              onClick = { SystemPortHelper.launchWatchFitnessApp(context) }
            )
          }
        }

        // Pinned Folders
        if (folders.isNotEmpty()) {
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              folders.forEach { folder ->
                EnlargedFolderView(
                  folder = folder,
                  apps = allApps,
                  onAppClick = onAppClick,
                  onFolderClick = { onOpenAppDrawer() },
                  iconSize = currentIconSize,
                  iconPack = settings.iconPack,
                  accentColor = accentColor
                )
              }
            }
          }
        }

        // Pinned Favorite Apps Row
        if (pinnedApps.isNotEmpty()) {
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(theme.surface.copy(alpha = 0.78f))
                .border(1.dp, theme.border.copy(alpha = 0.65f), RoundedCornerShape(26.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              pinnedApps.take(4).forEach { app ->
                AppIconItem(
                  app = app,
                  onClick = { onAppClick(app) },
                  onOpenAppInfo = { selectedAppForInfo = it },
                  onToggleDock = onToggleDockApp,
                  iconSize = currentIconSize,
                  showLabel = true,
                  iconPack = settings.iconPack,
                  accentColor = accentColor
                )
              }
            }
          }
        }

        // Long-Press Customization Zone on empty space
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(60.dp)
              .clip(RoundedCornerShape(18.dp))
              .combinedClickable(
                onClick = {},
                onLongClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  VibrationHelper.vibrateTouch(context)
                  isSettingsDialogOpen = true
                },
                onLongClickLabel = "Open Home Customization"
              )
              .testTag("home_empty_customization_zone"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "• • •",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = theme.textSecondary.copy(alpha = 0.4f)
            )
          }
        }
      }

      // Bottom Dock & Search Bar (Fixes Home Search and opens Drawer/Apps)
      NothingDock(
        dockApps = dockApps,
        onAppClick = onAppClick,
        onOpenDrawer = onOpenAppDrawer,
        onOpenSearch = {
          VibrationHelper.vibrateTouch(context)
          SystemPortHelper.launchWebSearch(context, searchQuery)
        },
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        iconPack = settings.iconPack,
        accentColor = accentColor,
        showSearchBar = settings.showSearchBarOnDock,
        iconSize = currentIconSize,
        onToggleDockApp = onToggleDockApp,
        onOpenAppInfo = { selectedAppForInfo = it }
      )
    }

    // Modal Widgets Sheet
    if (isWidgetSheetOpen) {
      NosWidgetPortSheet(
        activeWidgets = settings.activeWidgets,
        onToggleWidget = { widgetType ->
          val currentList = settings.activeWidgets.toMutableList()
          if (currentList.contains(widgetType)) {
            currentList.remove(widgetType)
          } else {
            currentList.add(widgetType)
          }
          onUpdateSettings(settings.copy(activeWidgets = currentList))
        },
        onDismiss = { isWidgetSheetOpen = false },
        accentColor = accentColor
      )
    }

    // Settings Customization Dialog
    if (isSettingsDialogOpen) {
      LauncherSettingsDialog(
        settings = settings,
        onUpdateSettings = onUpdateSettings,
        onDismiss = { isSettingsDialogOpen = false }
      )
    }

    // App Info Bottom Sheet
    if (selectedAppForInfo != null) {
      NothingAppInfoSheet(
        app = selectedAppForInfo!!,
        onDismiss = { selectedAppForInfo = null },
        onLaunchApp = onAppClick,
        accentColor = accentColor
      )
    }
  }
}

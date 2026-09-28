package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.LauncherClockStyle
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.NosWidgetPortType
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import com.example.service.SystemPortHelper
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppIconItem
import com.example.ui.components.EnlargedFolderView
import com.example.ui.components.NosCalendarDigitalTimeWidget
import com.example.ui.components.NosCircularGaugesWidget
import com.example.ui.components.NosContactPillWidget
import com.example.ui.components.NosDecibelWidget
import com.example.ui.components.NosGiantCirclesClusterWidget
import com.example.ui.components.NosGlanceTextWidget
import com.example.ui.components.NosMiniClusterWidget
import com.example.ui.components.NosNothingXEarbudsWidget
import com.example.ui.components.NosQuickListWidget
import com.example.ui.components.NosStickerFocusClusterWidget
import com.example.ui.components.NosWidgetPortSheet
import com.example.ui.components.NothingAnalogClockWidget
import com.example.ui.components.NothingAppInfoSheet
import com.example.ui.components.NothingCassetteWidget
import com.example.ui.components.NothingClockWidget
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingQuickNoteWidget
import com.example.ui.components.NothingQuickTogglesWidget
import com.example.ui.components.NothingResourceWidget
import com.example.ui.components.NothingStepWidget
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.components.NothingWeatherWidget
import com.example.ui.components.WidgetResizeFrame
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingWhite

@Composable
fun HomeScreen(
  currentTime: String,
  currentDate: String,
  weather: WeatherInfo,
  toggles: QuickToggleState,
  fitness: FitnessStats,
  audio: AudioState,
  quickNote: String,
  storagePct: Int,
  ramPct: Int,
  folders: List<FolderItem>,
  pinnedApps: List<AppItem>,
  dockApps: List<AppItem>,
  settings: LauncherSettings,
  onAppClick: (AppItem) -> Unit,
  onOpenFolder: (FolderItem) -> Unit,
  onToggleFolderEnlarged: (String) -> Unit,
  onToggleTorch: () -> Unit,
  onCycleSound: () -> Unit,
  onToggleWeather: () -> Unit,
  onAddStep: () -> Unit,
  onToggleAudioPlay: () -> Unit,
  onNextAudioTrack: () -> Unit,
  onEditNote: () -> Unit,
  onOpenDrawer: () -> Unit,
  onOpenSettings: () -> Unit,
  onSwipeDown: () -> Unit = {},
  onDoubleTap: () -> Unit = {},
  onToggleThemeMode: () -> Unit = {},
  onToggleClockStyle: () -> Unit = {},
  onReorderPinnedApps: (Int, Int) -> Unit = { _, _ -> },
  onRemovePinnedApp: (AppItem) -> Unit = {},
  onToggleDockApp: (AppItem) -> Unit = {},
  onToggleWidget: (NosWidgetPortType) -> Unit = {},
  onOpenAppInfo: (AppItem) -> Unit = {},
  onUpdateSettings: (LauncherSettings) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  var isReorderingFavorites by remember { mutableStateOf(false) }
  var showWidgetSheet by remember { mutableStateOf(false) }
  var selectedAppForInfo by remember { mutableStateOf<AppItem?>(null) }
  val lazyListState = rememberLazyListState()
  var isBarsVisible by remember { mutableStateOf(false) }
  var resizingWidget by remember { mutableStateOf<NosWidgetPortType?>(null) }
  val currentIconSize = when (settings.iconSizeLevel) {
    0 -> 44.dp
    2 -> 60.dp
    3 -> 68.dp
    else -> 52.dp
  }
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(theme.background)
      .pointerInput(settings.swipeDownNotifications) {
        if (settings.swipeDownNotifications) {
          var totalDrag = 0f
          detectVerticalDragGestures(
            onDragStart = { totalDrag = 0f },
            onDragEnd = { totalDrag = 0f },
            onDragCancel = { totalDrag = 0f },
            onVerticalDrag = { _, dragAmount ->
              totalDrag += dragAmount
              if (totalDrag > 120f) {
                // Reveal top bar on pull down
                isBarsVisible = true
                onSwipeDown()
                totalDrag = 0f
              }
            }
          )
        }
      }
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background (Supports built-in & custom gallery photos)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = false,
      accentColor = accentColor,
      onDoubleTap = onDoubleTap,
      onLongPress = {
        com.example.util.VibrationHelper.vibrateTouch(context)
        isBarsVisible = true
      }
    )

    // Scrollable Home Screen Body (Widgets, Folders, Pinned Apps - Edge-to-Edge without clipping!)
    LazyColumn(
      state = lazyListState,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(
        top = topInset + (if (isBarsVisible) 60.dp else 16.dp),
        bottom = bottomInset + 125.dp
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Calendar & Digital Time Widget (Screenshot 2: JUL TUESDAY 07H 10M)
        if (settings.activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.CALENDAR_DIGITAL_TIME,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.CALENDAR_DIGITAL_TIME },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.CALENDAR_DIGITAL_TIME)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosCalendarDigitalTimeWidget(
                currentTime = currentTime,
                accentColor = accentColor,
                onCalendarClick = { SystemPortHelper.launchPixelCalendar(context) },
                onClockClick = { SystemPortHelper.launchPixelClock(context) }
              )
            }
          }
        }

        // 2. 2x2 Mini Cluster (Screenshot 2) + Analog Clock / Weather
        if (settings.activeWidgets.contains(NosWidgetPortType.MINI_CLUSTER_2X2)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.MINI_CLUSTER_2X2,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.MINI_CLUSTER_2X2 },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.MINI_CLUSTER_2X2)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NosMiniClusterWidget(
                  weather = weather,
                  accentColor = accentColor,
                  onWeatherClick = { SystemPortHelper.launchPixelWeather(context) },
                  onHealthClick = { SystemPortHelper.launchHealthConnect(context) },
                  onRecorderClick = { SystemPortHelper.launchPixelClock(context) },
                  modifier = Modifier.weight(1f)
                )

                if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
                  val timeParts = currentTime.split(":")
                  val hours = timeParts.getOrNull(0) ?: "12"
                  val minutes = timeParts.getOrNull(1) ?: "00"
                  NothingAnalogClockWidget(
                    hours = hours,
                    minutes = minutes,
                    date = currentDate,
                    accentColor = accentColor,
                    onToggleStyle = onToggleClockStyle,
                    onOpenClockPort = { SystemPortHelper.launchPixelClock(context) },
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        } else if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
          // Signature Large Clock Widget (Dot Matrix or Round Analog)
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.CLOCK_MAIN,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.CLOCK_MAIN },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.CLOCK_MAIN)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              val timeParts = currentTime.split(":")
              val hours = timeParts.getOrNull(0) ?: "12"
              val minutes = timeParts.getOrNull(1) ?: "00"
              if (settings.clockStyle == LauncherClockStyle.ANALOG) {
                NothingAnalogClockWidget(
                  hours = hours,
                  minutes = minutes,
                  date = currentDate,
                  accentColor = accentColor,
                  onToggleStyle = onToggleClockStyle,
                  onOpenClockPort = { SystemPortHelper.launchPixelClock(context) }
                )
              } else {
                NothingClockWidget(
                  hours = hours,
                  minutes = minutes,
                  date = currentDate,
                  accentColor = accentColor,
                  onToggleStyle = onToggleClockStyle,
                  onOpenClockPort = { SystemPortHelper.launchPixelClock(context) }
                )
              }
            }
          }
        }

        // 3. Text Glance Summary Widget (Screenshot 2: "TODAY IS TUESDAY AND TIME IS...")
        if (settings.activeWidgets.contains(NosWidgetPortType.GLANCE_TEXT_SUMMARY)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.GLANCE_TEXT_SUMMARY,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.GLANCE_TEXT_SUMMARY },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.GLANCE_TEXT_SUMMARY)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosGlanceTextWidget(
                currentTime = currentTime,
                weather = weather,
                batteryPct = toggles.batteryLevel,
                isCharging = toggles.isCharging,
                onGlanceClick = { SystemPortHelper.launchPixelWeather(context) }
              )
            }
          }
        }

        // 3.5. Giant Circles Cluster (Screenshot 3: Giant Camera, Rain Weather, Globe Disc)
        if (settings.activeWidgets.contains(NosWidgetPortType.GIANT_CIRCLES_CLUSTER)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.GIANT_CIRCLES_CLUSTER,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.GIANT_CIRCLES_CLUSTER },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.GIANT_CIRCLES_CLUSTER)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosGiantCirclesClusterWidget(
                weather = weather,
                currentTime = currentTime,
                accentColor = accentColor,
                onLaunchCamera = {
                  val camApp = AppItem("com.google.android.GoogleCamera", "", "Camera")
                  onAppClick(camApp)
                },
                onLaunchWeather = {
                  SystemPortHelper.launchPixelWeather(context)
                }
              )
            }
          }
        }

        // 3.6. Sticker & Focus Cluster (Screenshot 5: Focus rings, Retro Car, Capsule)
        if (settings.activeWidgets.contains(NosWidgetPortType.STICKER_FOCUS_CLUSTER)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.STICKER_FOCUS_CLUSTER,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.STICKER_FOCUS_CLUSTER },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.STICKER_FOCUS_CLUSTER)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosStickerFocusClusterWidget(accentColor = accentColor)
            }
          }
        }

        // 3.7. Nothing X Earbuds Widget (Screenshot 5: Headphones 90%, ANC mode)
        if (settings.activeWidgets.contains(NosWidgetPortType.NOTHING_X_EARBUDS)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.NOTHING_X_EARBUDS,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.NOTHING_X_EARBUDS },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.NOTHING_X_EARBUDS)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosNothingXEarbudsWidget(accentColor = accentColor)
            }
          }
        }

        // 4. NOS 3.5 Circular Progress Gauges (Screenshot 1: Music 73%, Red Flame 57°C, Bell 98%)
        if (settings.activeWidgets.contains(NosWidgetPortType.CIRCULAR_GAUGES)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.CIRCULAR_GAUGES,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.CIRCULAR_GAUGES },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.CIRCULAR_GAUGES)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosCircularGaugesWidget(accentColor = accentColor)
            }
          }
        }

        // 5. Decibel Sound Meter & Tasks Checklist (Screenshot 1)
        if (settings.activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER) ||
            settings.activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST)) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              if (settings.activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER)) {
                NosDecibelWidget(
                  accentColor = accentColor,
                  modifier = Modifier.weight(1f)
                )
              }
              if (settings.activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST)) {
                NosQuickListWidget(
                  accentColor = accentColor,
                  modifier = Modifier.weight(1.2f)
                )
              }
            }
          }
        }

        // 6. Favorite Contact Pill (Screenshot 1)
        if (settings.activeWidgets.contains(NosWidgetPortType.CONTACT_PILL)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.CONTACT_PILL,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.CONTACT_PILL },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.CONTACT_PILL)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NosContactPillWidget(
                accentColor = accentColor,
                onCall = { SystemPortHelper.launchPixelClock(context) },
                onChat = { SystemPortHelper.launchPixelCalendar(context) }
              )
            }
          }
        }

        // 7. 2-Column Modular Widgets: Weather + Quick Toggles
        if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.WEATHER_MAIN,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.WEATHER_MAIN },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.WEATHER_MAIN)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NothingWeatherWidget(
                  weather = weather,
                  onToggleCondition = onToggleWeather,
                  accentColor = accentColor,
                  onOpenWeatherPort = { SystemPortHelper.launchPixelWeather(context) },
                  modifier = Modifier.weight(1f)
                )

                NothingQuickTogglesWidget(
                  toggles = toggles,
                  onToggleTorch = onToggleTorch,
                  onCycleSound = onCycleSound,
                  accentColor = accentColor,
                  modifier = Modifier.weight(1.1f)
                )
              }
            }
          }
        }

        // 8. Teenage Cassette Retro Player
        if (settings.activeWidgets.contains(NosWidgetPortType.CASSETTE_PLAYER)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.CASSETTE_PLAYER,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.CASSETTE_PLAYER },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.CASSETTE_PLAYER)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              NothingCassetteWidget(
                audio = audio,
                onTogglePlay = onToggleAudioPlay,
                onNextTrack = onNextAudioTrack,
                accentColor = accentColor
              )
            }
          }
        }

        // 9. 2-Column Widgets: Pedometer & Storage/RAM
        if (settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)) {
          item {
            WidgetResizeFrame(
              isSelected = resizingWidget == NosWidgetPortType.PEDOMETER_GAUGE,
              sizeMode = settings.widgetSizeLevel,
              onSelect = { resizingWidget = NosWidgetPortType.PEDOMETER_GAUGE },
              onCycleSize = {
                val next = (settings.widgetSizeLevel + 1) % 3
                onUpdateSettings(settings.copy(widgetSizeLevel = next))
              },
              onRemove = {
                onToggleWidget(NosWidgetPortType.PEDOMETER_GAUGE)
                resizingWidget = null
              },
              onDismiss = { resizingWidget = null },
              accentColor = accentColor
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NothingStepWidget(
                  fitness = fitness,
                  onAddStep = onAddStep,
                  accentColor = accentColor,
                  modifier = Modifier.weight(1.1f)
                )

                NothingResourceWidget(
                  storagePct = storagePct,
                  ramPct = ramPct,
                  accentColor = accentColor,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // 11. Signature Nothing OS 2x2 Enlarged Folders
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            folders.forEach { folder ->
              EnlargedFolderView(
                folder = folder,
                onAppClick = onAppClick,
                onOpenFolderSheet = { onOpenFolder(folder) },
                onToggleEnlarged = { onToggleFolderEnlarged(folder.id) },
                iconPack = settings.iconPack,
                accentColor = accentColor,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // 7. Pinned Apps on Home (With Full Touch Reordering & Quick Controls)
        if (pinnedApps.isNotEmpty()) {
          item {
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
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
                    text = "FAVORITES",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    letterSpacing = 1.sp
                  )
                }

                // Rearrange / Move Mode Toggle Pill
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isReorderingFavorites) accentColor else theme.surface)
                    .border(1.dp, if (isReorderingFavorites) accentColor else theme.border, RoundedCornerShape(12.dp))
                    .clickable { isReorderingFavorites = !isReorderingFavorites }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("rearrange_favorites_button")
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.SwapHoriz,
                      contentDescription = null,
                      tint = if (isReorderingFavorites) NothingBlack else accentColor,
                      modifier = Modifier.size(13.dp)
                    )
                    Text(
                      text = if (isReorderingFavorites) "DONE" else "REARRANGE",
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isReorderingFavorites) NothingBlack else NothingWhite,
                      letterSpacing = 1.sp
                    )
                  }
                }
              }

              // Non-nested clean grid using chunked Rows
              val chunkedApps = pinnedApps.chunked(settings.gridColumns)
              chunkedApps.forEachIndexed { rowIndex, rowApps ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                  rowApps.forEachIndexed { colIndex, app ->
                    val actualIndex = rowIndex * settings.gridColumns + colIndex
                    var itemDragOffset by remember(app.packageName) { mutableFloatStateOf(0f) }

                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .offset { IntOffset(itemDragOffset.roundToInt(), 0) }
                        .scale(if (isReorderingFavorites) 1.03f else 1f)
                        .then(
                          if (isReorderingFavorites) {
                            Modifier
                              .background(NothingDarkSurface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                              .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                              .pointerInput(app.packageName) {
                                detectHorizontalDragGestures(
                                  onHorizontalDrag = { _, dragAmount ->
                                    itemDragOffset += dragAmount
                                  },
                                  onDragEnd = {
                                    if (itemDragOffset > 40f && actualIndex < pinnedApps.size - 1) {
                                      onReorderPinnedApps(actualIndex, actualIndex + 1)
                                    } else if (itemDragOffset < -40f && actualIndex > 0) {
                                      onReorderPinnedApps(actualIndex, actualIndex - 1)
                                    }
                                    itemDragOffset = 0f
                                  },
                                  onDragCancel = {
                                    itemDragOffset = 0f
                                  }
                                )
                              }
                          } else Modifier
                        )
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        AppIconItem(
                          app = app,
                          onClick = {
                            if (!isReorderingFavorites) {
                              onAppClick(app)
                            }
                          },
                          onOpenAppInfo = { appTarget ->
                            selectedAppForInfo = appTarget
                            onOpenAppInfo(appTarget)
                          },
                          onTogglePin = { onRemovePinnedApp(app) },
                          onToggleDock = onToggleDockApp,
                          iconSize = currentIconSize,
                          onCycleIconSize = {
                            val nextLevel = (settings.iconSizeLevel + 1) % 4
                            onUpdateSettings(settings.copy(iconSizeLevel = nextLevel))
                          },
                          showLabel = settings.showLabels,
                          iconPack = settings.iconPack,
                          accentColor = accentColor
                        )

                        // Quick Rearrange Touch Controls when active
                        if (isReorderingFavorites) {
                          Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            if (actualIndex > 0) {
                              Box(
                                modifier = Modifier
                                  .size(24.dp)
                                  .clip(CircleShape)
                                  .background(NothingElevated)
                                  .clickable { onReorderPinnedApps(actualIndex, actualIndex - 1) },
                                contentAlignment = Alignment.Center
                              ) {
                                Icon(
                                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                  contentDescription = "Move Left",
                                  tint = NothingWhite,
                                  modifier = Modifier.size(14.dp)
                                )
                              }
                            }

                            if (actualIndex < pinnedApps.size - 1) {
                              Box(
                                modifier = Modifier
                                  .size(24.dp)
                                  .clip(CircleShape)
                                  .background(NothingElevated)
                                  .clickable { onReorderPinnedApps(actualIndex, actualIndex + 1) },
                                contentAlignment = Alignment.Center
                              ) {
                                Icon(
                                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                  contentDescription = "Move Right",
                                  tint = NothingWhite,
                                  modifier = Modifier.size(14.dp)
                                )
                              }
                            }

                            Box(
                              modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NothingElevated)
                                .clickable { onRemovePinnedApp(app) },
                              contentAlignment = Alignment.Center
                            ) {
                              Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = accentColor,
                                modifier = Modifier.size(14.dp)
                              )
                            }
                          }
                        }
                      }
                    }
                  }

                  // Pad with empty weights if row is incomplete
                  val missingInRow = settings.gridColumns - rowApps.size
                  repeat(missingInRow) {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

    // 2. Bottom Persistent Floating iOS-style Transparent Nothing Dock & Search
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding()
        .pointerInput(Unit) {
          var accumulatedUpDrag = 0f
          detectVerticalDragGestures(
            onDragStart = { accumulatedUpDrag = 0f },
            onDragEnd = { accumulatedUpDrag = 0f },
            onDragCancel = { accumulatedUpDrag = 0f },
            onVerticalDrag = { _, dragAmount ->
              accumulatedUpDrag += dragAmount
              // Deliberate swipe up (-60f threshold for natural, responsive opening)
              if (accumulatedUpDrag < -60f) {
                onOpenDrawer()
                accumulatedUpDrag = 0f
              }
            }
          )
        }
    ) {
      NothingDock(
        dockApps = dockApps,
        onAppClick = onAppClick,
        onOpenDrawer = onOpenDrawer,
        onOpenSearch = onOpenDrawer,
        iconPack = settings.iconPack,
        accentColor = accentColor,
        showSearchBar = settings.showSearchBarOnDock,
        iconSize = currentIconSize,
        onToggleDockApp = onToggleDockApp,
        onOpenAppInfo = { appTarget ->
          selectedAppForInfo = appTarget
          onOpenAppInfo(appTarget)
        },
        onCycleIconSize = {
          val nextLevel = (settings.iconSizeLevel + 1) % 4
          onUpdateSettings(settings.copy(iconSizeLevel = nextLevel))
        }
      )
    }

    // 3. Top Navigation & Settings Bar (Hidden by default, slides down smoothly when requested)
    AnimatedVisibility(
      visible = isBarsVisible,
      enter = slideInVertically { -it } + fadeIn(),
      exit = slideOutVertically { -it } + fadeOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(if (theme.isDark) Color(0xF018181C) else Color(0xF0FFFFFF))
          .border(1.dp, theme.border.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
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
            text = "NOTHING",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = theme.textPrimary,
            letterSpacing = 2.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Quick Resize Icons shortcut (تكبير وتصغير الأيقونات)
          IconButton(
            onClick = {
              com.example.util.VibrationHelper.vibrateTouch(context)
              val nextLevel = (settings.iconSizeLevel + 1) % 4
              onUpdateSettings(settings.copy(iconSizeLevel = nextLevel))
            },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AspectRatio,
              contentDescription = "Resize Icons",
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
          }

          // Settings Access Button (طلب الإعدادات)
          IconButton(
            onClick = {
              com.example.util.VibrationHelper.vibrateTouch(context)
              onOpenSettings()
            },
            modifier = Modifier.size(36.dp).testTag("home_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = "Launcher Settings",
              tint = theme.textSecondary,
              modifier = Modifier.size(18.dp)
            )
          }

          // Close / Hide top bar button (إخفاء الشريط العلوي)
          IconButton(
            onClick = {
              com.example.util.VibrationHelper.vibrateTouch(context)
              isBarsVisible = false
            },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Hide Bar",
              tint = theme.textSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 4. Subtle Top Pull/Access Handle when top bar is hidden (Tap or pull down opens Settings / Bar)
    if (!isBarsVisible) {
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .statusBarsPadding()
          .padding(top = 6.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(theme.textSecondary.copy(alpha = 0.35f))
          .clickable {
            com.example.util.VibrationHelper.vibrateTouch(context)
            isBarsVisible = true
          }
          .size(width = 38.dp, height = 5.dp)
          .testTag("top_settings_pull_handle")
      )
    }

    // Nothing OS 5 App Info & Diagnostics Sheet (Ensures App Info always displays)
    if (selectedAppForInfo != null) {
      NothingAppInfoSheet(
        app = selectedAppForInfo!!,
        onDismiss = { selectedAppForInfo = null },
        onLaunchApp = {
          onAppClick(selectedAppForInfo!!)
          selectedAppForInfo = null
        },
        accentColor = accentColor
      )
    }

    // NOS 3.5 Widgets Port Bottom Sheet Picker
    if (showWidgetSheet) {
      NosWidgetPortSheet(
        activeWidgets = settings.activeWidgets,
        onToggleWidget = onToggleWidget,
        onDismiss = { showWidgetSheet = false },
        accentColor = accentColor
      )
    }
  }
}

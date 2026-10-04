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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import android.os.SystemClock
import android.appwidget.AppWidgetHostView
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
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingWhite

@OptIn(ExperimentalFoundationApi::class)
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
  systemWidgetView: AppWidgetHostView? = null,
  onUpdateSettings: (LauncherSettings) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  val lazyListState = rememberLazyListState()
  var isBarsVisible by remember { mutableStateOf(false) }
  var isHomeCustomizationOpen by remember { mutableStateOf(false) }
  // Widgets use the same App Info surface as apps. This gives the Home Screen
  // a reliable App Info entry point without needing extra empty space.
  val widgetInfoApp = remember(context.packageName) {
    AppItem(
      packageName = context.packageName,
      label = "Nothing Launcher"
    )
  }
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
      .pointerInput(Unit) {
        // Google Feed gesture lives on HomeScreen so child widgets keep tap ownership.
        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          var previousX = down.position.x
          var totalRight = 0f
          var handled = false
          while (!handled) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            val deltaX = change.position.x - previousX
            previousX = change.position.x
            if (deltaX != 0f) totalRight += deltaX
            if (totalRight > 150f) {
              change.consume()
              SystemPortHelper.launchGoogleFeed(context)
              handled = true
            }
          }
        }
      }
      .nestedScroll(remember {
        object : NestedScrollConnection {
          var downDistance = 0f
          override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (settings.swipeDownNotifications && source == NestedScrollSource.UserInput) {
              if (available.y > 0f) {
                downDistance += available.y
                if (downDistance > 120f) {
                  isBarsVisible = true
                  onSwipeDown()
                  downDistance = 0f
                }
              } else if (available.y < -4f) {
                downDistance = 0f
              }
            }
            return Offset.Zero
          }
          override suspend fun onPostFling(
            consumed: androidx.compose.ui.unit.Velocity,
            available: androidx.compose.ui.unit.Velocity
          ): androidx.compose.ui.unit.Velocity {
            downDistance = 0f
            return androidx.compose.ui.unit.Velocity.Zero
          }
        }
      })
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background (Supports built-in & custom gallery photos)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = false,
      accentColor = accentColor,
    )

    // Scrollable Home Screen Body (Widgets, Folders, Pinned Apps - Edge-to-Edge without clipping!)
    LazyColumn(
      state = lazyListState,
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(
        top = topInset + (if (isBarsVisible) 60.dp else 16.dp),
        bottom = bottomInset + 185.dp
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Home widgets: main surface driven by the active widget set.
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_widget_area"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            val widgetScale = when (settings.widgetSizeLevel) {
              0 -> 0.85f
              2 -> 1.15f
              else -> 1f
            }

            if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .combinedClickable(
                    onClick = { onOpenAppInfo(widgetInfoApp) },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      isHomeCustomizationOpen = true
                    },
                    onLongClickLabel = "Open Home widgets"
                  )
              ) {
                CustomClockWidget(currentTime, currentDate, settings.clockStyle, accentColor, theme.isDark, widgetScale)
              }
            }

            if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN) ||
                settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)) {
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .combinedClickable(
                    onClick = { onOpenAppInfo(widgetInfoApp) },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      isHomeCustomizationOpen = true
                    },
                    onLongClickLabel = "Open Home widgets"
                  )
                  ) {
                    CustomWeatherWidget(weather, toggles, accentColor, theme.isDark, widgetScale, Modifier.fillMaxWidth())
                  }
                }
                if (settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)) {
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .combinedClickable(
                    onClick = { onOpenAppInfo(widgetInfoApp) },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      isHomeCustomizationOpen = true
                    },
                    onLongClickLabel = "Open Home widgets"
                  )
                  ) {
                    CustomPedometerWidget(fitness, ramPct, accentColor, theme.isDark, widgetScale, Modifier.fillMaxWidth())
                  }
                }
              }
            }

            if (settings.activeWidgets.contains(NosWidgetPortType.CASSETTE_PLAYER)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .combinedClickable(
                    onClick = { onOpenAppInfo(widgetInfoApp) },
                    onLongClick = {
                      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                      isHomeCustomizationOpen = true
                    },
                    onLongClickLabel = "Open Home widgets"
                  )
              ) {
                CustomCassetteWidget(audio, accentColor, theme.isDark, widgetScale)
              }
            }

            if (settings.activeWidgets.isEmpty()) {
              Box(
                modifier = Modifier.fillMaxWidth().height(110.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  "LONG-PRESS  •  ADD WIDGET",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  letterSpacing = 1.5.sp,
                  color = theme.textSecondary.copy(alpha = 0.65f)
                )
              }
            }
          }
        }

        // Media / Tools are no longer fixed on Home.
        // They are optional widgets from the Home customization panel.
        item {
          val mediaFolder = folders.firstOrNull { it.id == "folder_media" }
          val toolsFolder = folders.firstOrNull { it.id == "folder_tools" }
          val mediaActive = settings.activeWidgets.contains(NosWidgetPortType.MEDIA_FOLDER)
          val toolsActive = settings.activeWidgets.contains(NosWidgetPortType.TOOLS_FOLDER)

          if (mediaActive || toolsActive) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              if (mediaActive && mediaFolder != null) {
                EnlargedFolderView(
                  folder = mediaFolder,
                  onAppClick = onAppClick,
                  onOpenFolderSheet = { onOpenFolder(mediaFolder) },
                  onToggleEnlarged = { onToggleFolderEnlarged(mediaFolder.id) },
                  iconPack = settings.iconPack,
                  accentColor = accentColor,
                  modifier = Modifier.weight(1f)
                )
              }
              if (toolsActive && toolsFolder != null) {
                EnlargedFolderView(
                  folder = toolsFolder,
                  onAppClick = onAppClick,
                  onOpenFolderSheet = { onOpenFolder(toolsFolder) },
                  onToggleEnlarged = { onToggleFolderEnlarged(toolsFolder.id) },
                  iconPack = settings.iconPack,
                  accentColor = accentColor,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        // Real Android system widget selected through the launcher widget picker.
        // This is the same host/picker concept used by Nothing/Launcher3.
        if (systemWidgetView != null) {
          item {
            AndroidView(
              factory = { systemWidgetView!! },
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, theme.border, RoundedCornerShape(24.dp))
            )
          }
        }

        // Empty Home area: long-press opens the Nothing-style Home customization panel.
        // Kept as a dedicated interaction zone so app/folder gestures remain untouched.
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .combinedClickable(
                onClick = {},
                onLongClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  isHomeCustomizationOpen = true
                },
                onLongClickLabel = "Open Home customization"
              )
              .testTag("home_empty_customization_zone")
          )
        }
      }

    // 2. Bottom Persistent Floating iOS-style Transparent Nothing Dock & Search
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              (if (theme.isDark) Color(0xCC000000) else Color(0xCCFFFFFF)),
              (if (theme.isDark) Color(0xF5000000) else Color(0xF5FFFFFF))
            )
          )
        )
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
        onOpenAppInfo = onOpenAppInfo,
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

    if (isHomeCustomizationOpen) {
      HomeCustomizationPanel(
        settings = settings,
        accentColor = accentColor,
        isDark = theme.isDark,
        onToggle = { widgetType ->
          onToggleWidget(widgetType)
          isHomeCustomizationOpen = false
        },
        onUpdateSettings = { updatedSettings ->
          onUpdateSettings(updatedSettings)
        },
        onOpenSettings = {
          isHomeCustomizationOpen = false
          onOpenSettings()
        },
        onDismiss = { isHomeCustomizationOpen = false }
      )
    }


  }
}


@Composable
private fun CustomClockWidget(
  currentTime: String,
  currentDate: String,
  style: LauncherClockStyle,
  accentColor: Color,
  isDark: Boolean,
  scale: Float = 1f
) {
  val surface = if (isDark) Color(0xCC111114) else Color(0xEFFFFFFF)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  Row(
    modifier = Modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(28.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
      .padding(horizontal = 22.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(currentTime, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
        fontSize = if (style == LauncherClockStyle.DIGITAL) 42.sp else 38.sp, color = primary, letterSpacing = 1.sp)
      Text(currentDate.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 12.sp,
        color = secondary, letterSpacing = 1.5.sp)
    }
    Box(Modifier.size(12.dp).clip(CircleShape).background(accentColor))
  }
}

@Composable
private fun CustomWeatherWidget(
  weather: WeatherInfo,
  toggles: QuickToggleState,
  accentColor: Color,
  isDark: Boolean,
  scale: Float,
  modifier: Modifier = Modifier
) {
  val surface = if (isDark) Color(0xCC111114) else Color(0xEFFFFFFF)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  Column(
    modifier = modifier.scale(scale).clip(RoundedCornerShape(24.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(24.dp)).padding(16.dp)
  ) {
    Text("WEATHER", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary, letterSpacing = 1.5.sp)
    Row(verticalAlignment = Alignment.Bottom) {
      Text(weather.tempC.toString() + "°", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = primary)
      Spacer(Modifier.width(6.dp))
      Text(weather.condition, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary)
    }
    Text(weather.city.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary)
    Spacer(Modifier.height(8.dp))
    Text("BAT " + toggles.batteryLevel + "%  •  WIFI " + if (toggles.wifiEnabled) "ON" else "OFF",
      fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = secondary)
  }
}

@Composable
private fun CustomPedometerWidget(
  fitness: FitnessStats,
  ramPct: Int,
  accentColor: Color,
  isDark: Boolean,
  scale: Float,
  modifier: Modifier = Modifier
) {
  val surface = if (isDark) Color(0xCC111114) else Color(0xEFFFFFFF)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  val progress = (fitness.steps.toFloat() / fitness.goal.coerceAtLeast(1)).coerceIn(0f, 1f)
  Column(
    modifier = modifier.scale(scale).clip(RoundedCornerShape(24.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(24.dp)).padding(16.dp)
  ) {
    Text("ACTIVITY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary, letterSpacing = 1.5.sp)
    Text(fitness.steps.toString(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = primary)
    Text("STEPS / " + fitness.goal, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = secondary)
    Spacer(Modifier.height(7.dp))
    Row(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(secondary.copy(alpha = 0.18f))) {
      Box(Modifier.fillMaxWidth(progress).fillMaxSize().background(accentColor))
    }
    Spacer(Modifier.height(6.dp))
    Text(fitness.calories.toString() + " KCAL  •  RAM " + ramPct + "%", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = secondary)
  }
}

@Composable
private fun CustomCassetteWidget(
  audio: AudioState,
  accentColor: Color,
  isDark: Boolean,
  scale: Float
) {
  val surface = if (isDark) Color(0xCC111114) else Color(0xEFFFFFFF)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  Row(
    modifier = Modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(24.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(24.dp)).padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(Modifier.size(58.dp).clip(RoundedCornerShape(12.dp)).background(primary.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
      Text("PLAY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = accentColor, letterSpacing = 1.sp)
    }
    Spacer(Modifier.width(12.dp))
    Column(Modifier.weight(1f)) {
      Text("CASSETTE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary, letterSpacing = 1.5.sp)
      Text(audio.title, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primary, maxLines = 1)
      Text(audio.artist, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = secondary, maxLines = 1)
    }
    Text(if (audio.isPlaying) "▶" else "Ⅱ", fontSize = 18.sp, color = accentColor)
  }
}

@Composable
private fun HomeCustomizationPanel(
  settings: LauncherSettings,
  accentColor: Color,
  isDark: Boolean,
  onToggle: (NosWidgetPortType) -> Unit,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val surface = if (isDark) Color(0xFF111114) else Color(0xFFF6F6F6)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  val clockActive = settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)
  val weatherActive = settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)
  val activityActive = settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)
  val cassetteActive = settings.activeWidgets.contains(NosWidgetPortType.CASSETTE_PLAYER)
  val mediaActive = settings.activeWidgets.contains(NosWidgetPortType.MEDIA_FOLDER)
  val toolsActive = settings.activeWidgets.contains(NosWidgetPortType.TOOLS_FOLDER)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.48f))
      .clickable(onClick = onDismiss)
      .padding(horizontal = 18.dp, vertical = 24.dp),
    contentAlignment = Alignment.BottomCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(30.dp))
        .background(surface)
        .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(30.dp))
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "HOME CUSTOMIZE",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = secondary,
        letterSpacing = 2.sp
      )
      Text(
        text = "Customize your Home Screen",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = primary
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            com.example.util.VibrationHelper.vibrateTouch(context)
            val next = (settings.widgetSizeLevel + 1) % 3
            onUpdateSettings(settings.copy(widgetSizeLevel = next))
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = primary, contentColor = surface)
        ) {
          Text("WIDGET SIZE")
        }
        Button(
          onClick = {
            com.example.util.VibrationHelper.vibrateTouch(context)
            val next = (settings.iconSizeLevel + 1) % 4
            onUpdateSettings(settings.copy(iconSizeLevel = next))
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = primary, contentColor = surface)
        ) {
          Text("ICON SIZE")
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            com.example.util.VibrationHelper.vibrateTouch(context)
            val next = if (settings.clockStyle == LauncherClockStyle.ANALOG)
              LauncherClockStyle.DIGITAL else LauncherClockStyle.ANALOG
            onUpdateSettings(settings.copy(clockStyle = next))
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = primary, contentColor = surface)
        ) {
          Text("CLOCK: " + settings.clockStyle.name)
        }
        Button(
          onClick = {
            com.example.util.VibrationHelper.vibrateTouch(context)
            onOpenSettings()
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black)
        ) {
          Text("MORE")
        }
      }

      Text(
        text = "WIDGETS",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = secondary,
        letterSpacing = 1.5.sp
      )

      WidgetToggleButton("CLOCK", clockActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.CLOCK_MAIN)
      }
      WidgetToggleButton("WEATHER + BATTERY", weatherActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.WEATHER_MAIN)
      }
      WidgetToggleButton("ACTIVITY + RAM", activityActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.PEDOMETER_GAUGE)
      }
      WidgetToggleButton("CASSETTE PLAYER", cassetteActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.CASSETTE_PLAYER)
      }

      WidgetToggleButton("MEDIA", mediaActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.MEDIA_FOLDER)
      }
      WidgetToggleButton("TOOLS", toolsActive, accentColor, primary, surface) {
        onToggle(NosWidgetPortType.TOOLS_FOLDER)
      }

      Text(
        text = "Add or remove Home widgets, change their size, adjust icon size and switch the clock style.",
        fontSize = 12.sp,
        color = secondary
      )
    }
  }
}

@Composable
private fun WidgetToggleButton(
  label: String,
  active: Boolean,
  accentColor: Color,
  primary: Color,
  surface: Color,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (active) accentColor else primary,
      contentColor = if (active) Color.Black else surface
    )
  ) {
    Text(if (active) "REMOVE  •  " + label else "ADD  •  " + label)
  }
}

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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import android.os.SystemClock
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
import com.example.ui.components.NothingAppInfoSheet
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
  onUpdateSettings: (LauncherSettings) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  var selectedAppForInfo by remember { mutableStateOf<AppItem?>(null) }
  val lazyListState = rememberLazyListState()
  var isBarsVisible by remember { mutableStateOf(false) }
  var isCustomWidgetPickerOpen by remember { mutableStateOf(false) }
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
        // Custom widget area. Long-press opens our own picker.
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) 150.dp else 120.dp)
              .combinedClickable(
                onClick = {},
                onLongClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  isCustomWidgetPickerOpen = true
                },
                onLongClickLabel = "Open widgets"
              ),
            contentAlignment = Alignment.Center
          ) {
            if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
              CustomClockWidget(
                currentTime = currentTime,
                currentDate = currentDate,
                style = settings.clockStyle,
                accentColor = accentColor,
                isDark = theme.isDark
              )
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

        item {
          Spacer(modifier = Modifier.height(16.dp))
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

    if (isCustomWidgetPickerOpen) {
      CustomWidgetPicker(
        activeWidgets = settings.activeWidgets,
        accentColor = accentColor,
        isDark = theme.isDark,
        onToggle = { widgetType ->
          onToggleWidget(widgetType)
          isCustomWidgetPickerOpen = false
        },
        onDismiss = { isCustomWidgetPickerOpen = false }
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

  }
}


@Composable
private fun CustomClockWidget(
  currentTime: String,
  currentDate: String,
  style: LauncherClockStyle,
  accentColor: Color,
  isDark: Boolean
) {
  val surface = if (isDark) Color(0xCC111114) else Color(0xEFFFFFFF)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  Row(
    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
      .padding(horizontal = 22.dp, vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = currentTime, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
        fontSize = if (style == LauncherClockStyle.DIGITAL) 42.sp else 38.sp, color = primary, letterSpacing = 1.sp)
      Text(text = currentDate.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 12.sp,
        color = secondary, letterSpacing = 1.5.sp)
    }
    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(accentColor))
  }
}

@Composable
private fun CustomWidgetPicker(
  activeWidgets: List<NosWidgetPortType>,
  accentColor: Color,
  isDark: Boolean,
  onToggle: (NosWidgetPortType) -> Unit,
  onDismiss: () -> Unit
) {
  val surface = if (isDark) Color(0xFF111114) else Color(0xFFF6F6F6)
  val primary = if (isDark) NothingWhite else NothingBlack
  val secondary = if (isDark) NothingGrey else Color(0xFF66666A)
  val clockActive = activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)
  Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.48f)).clickable(onClick = onDismiss)
    .padding(horizontal = 18.dp, vertical = 24.dp), contentAlignment = Alignment.BottomCenter) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)).background(surface)
      .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(30.dp)).padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(text = "CUSTOM WIDGETS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
        fontSize = 13.sp, color = secondary, letterSpacing = 2.sp)
      Text(text = "Choose a widget for your Home Screen", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = primary)
      Button(onClick = { onToggle(NosWidgetPortType.CLOCK_MAIN) }, modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = if (clockActive) accentColor else primary,
          contentColor = if (clockActive) Color.Black else surface)) {
        Text(if (clockActive) "REMOVE  •  CLOCK" else "ADD  •  CLOCK")
      }
      Text(text = "More custom widgets will be added here.", fontSize = 12.sp, color = secondary)
    }
  }
}

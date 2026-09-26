package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.ui.components.NothingCassetteWidget
import com.example.ui.components.NothingClockWidget
import com.example.ui.components.NothingDock
import com.example.ui.components.NothingQuickNoteWidget
import com.example.ui.components.NothingQuickTogglesWidget
import com.example.ui.components.NothingResourceWidget
import com.example.ui.components.NothingStepWidget
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.components.NothingWeatherWidget
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
  val lazyListState = rememberLazyListState()
  var isBarsVisible by remember { mutableStateOf(true) }

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // Auto-hide when scrolling down (Full Immersive Widgets Mode)
        if (available.y < -12f && isBarsVisible) {
          isBarsVisible = false
        }
        // Auto-show when scrolling up
        else if (available.y > 12f && !isBarsVisible) {
          isBarsVisible = true
        }
        return Offset.Zero
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(theme.background)
      .testTag("home_screen_container")
  ) {
    // Dynamic Nothing OS 5 Wallpaper Background (Supports built-in & custom gallery photos)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = false,
      accentColor = accentColor,
      onDoubleTap = onDoubleTap
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Navigation / Glance Bar (With Animated Visibility & Auto-hide)
      AnimatedVisibility(
        visible = isBarsVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .pointerInput(settings.swipeDownNotifications) {
              if (settings.swipeDownNotifications) {
                detectVerticalDragGestures { _, dragAmount ->
                  if (dragAmount > 30f) {
                    onSwipeDown()
                  }
                }
              }
            },
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              isBarsVisible = false
            }
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
            // Quick Day / Night Theme Toggle (Direct 1-tap switch between Image 3 Theme Jour and Image 2 Theme Nuit)
            IconButton(
              onClick = onToggleThemeMode,
              modifier = Modifier.testTag("home_theme_toggle_button")
            ) {
              Icon(
                imageVector = when (settings.themeMode) {
                  LauncherThemeMode.DARK -> Icons.Default.DarkMode
                  LauncherThemeMode.LIGHT -> Icons.Default.LightMode
                  LauncherThemeMode.RETRO_PASTEL -> Icons.Default.Palette
                  LauncherThemeMode.SYSTEM -> if (theme.isDark) Icons.Default.DarkMode else Icons.Default.LightMode
                },
                contentDescription = "Toggle Theme Jour / Nuit / Retro",
                tint = if (settings.themeMode != LauncherThemeMode.DARK) accentColor else theme.textSecondary,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = onOpenSettings,
              modifier = Modifier.testTag("home_settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Launcher Settings",
                tint = theme.textSecondary
              )
            }

            // Quick Collapse button for Top Bar
            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isBarsVisible = false
              },
              modifier = Modifier.testTag("home_hide_top_bar_button")
            ) {
              Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Hide Top Bar",
                tint = theme.textSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // Minimal Show-on-demand Pill for Top Bar ("ظهورها عند الطلب")
      AnimatedVisibility(
        visible = !isBarsVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(theme.surface.copy(alpha = 0.85f))
              .border(1.dp, theme.border.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isBarsVisible = true
              }
              .padding(horizontal = 14.dp, vertical = 6.dp)
              .testTag("show_top_bar_pill")
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
                text = "NOTHING",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = theme.textPrimary
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Show Top Bar",
                tint = theme.textSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // Scrollable Home Screen Body (Widgets, Folders, Pinned Apps)
      LazyColumn(
        state = lazyListState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .nestedScroll(nestedScrollConnection)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
          top = if (isBarsVisible) 6.dp else 2.dp,
          bottom = if (isBarsVisible) 14.dp else 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Calendar & Digital Time Widget (Screenshot 2: JUL TUESDAY 07H 10M)
        if (settings.activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME)) {
          item {
            NosCalendarDigitalTimeWidget(
              currentTime = currentTime,
              accentColor = accentColor,
              onCalendarClick = { SystemPortHelper.launchPixelCalendar(context) },
              onClockClick = { SystemPortHelper.launchPixelClock(context) }
            )
          }
        }

        // 2. 2x2 Mini Cluster (Screenshot 2) + Analog Clock / Weather
        if (settings.activeWidgets.contains(NosWidgetPortType.MINI_CLUSTER_2X2)) {
          item {
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
        } else if (settings.activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN)) {
          // Signature Large Clock Widget (Dot Matrix or Round Analog)
          item {
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

        // 3. Text Glance Summary Widget (Screenshot 2: "TODAY IS TUESDAY AND TIME IS...")
        if (settings.activeWidgets.contains(NosWidgetPortType.GLANCE_TEXT_SUMMARY)) {
          item {
            NosGlanceTextWidget(
              currentTime = currentTime,
              weather = weather,
              batteryPct = toggles.batteryLevel,
              isCharging = toggles.isCharging,
              onGlanceClick = { SystemPortHelper.launchPixelWeather(context) }
            )
          }
        }

        // 3.5. Giant Circles Cluster (Screenshot 3: Giant Camera, Rain Weather, Globe Disc)
        if (settings.activeWidgets.contains(NosWidgetPortType.GIANT_CIRCLES_CLUSTER)) {
          item {
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

        // 3.6. Sticker & Focus Cluster (Screenshot 5: Focus rings, Retro Car, Capsule)
        if (settings.activeWidgets.contains(NosWidgetPortType.STICKER_FOCUS_CLUSTER)) {
          item {
            NosStickerFocusClusterWidget(accentColor = accentColor)
          }
        }

        // 3.7. Nothing X Earbuds Widget (Screenshot 5: Headphones 90%, ANC mode)
        if (settings.activeWidgets.contains(NosWidgetPortType.NOTHING_X_EARBUDS)) {
          item {
            NosNothingXEarbudsWidget(accentColor = accentColor)
          }
        }

        // 4. NOS 3.5 Circular Progress Gauges (Screenshot 1: Music 73%, Red Flame 57°C, Bell 98%)
        if (settings.activeWidgets.contains(NosWidgetPortType.CIRCULAR_GAUGES)) {
          item {
            NosCircularGaugesWidget(accentColor = accentColor)
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
            NosContactPillWidget(
              accentColor = accentColor,
              onCall = { SystemPortHelper.launchPixelClock(context) },
              onChat = { SystemPortHelper.launchPixelCalendar(context) }
            )
          }
        }

        // 7. 2-Column Modular Widgets: Weather + Quick Toggles
        if (settings.activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN)) {
          item {
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

        // 8. Teenage Cassette Retro Player
        if (settings.activeWidgets.contains(NosWidgetPortType.CASSETTE_PLAYER)) {
          item {
            NothingCassetteWidget(
              audio = audio,
              onTogglePlay = onToggleAudioPlay,
              onNextTrack = onNextAudioTrack,
              accentColor = accentColor
            )
          }
        }

        // 9. 2-Column Widgets: Pedometer & Storage/RAM
        if (settings.activeWidgets.contains(NosWidgetPortType.PEDOMETER_GAUGE)) {
          item {
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

        // 10. Quick Memo
        item {
          NothingQuickNoteWidget(
            note = quickNote,
            onEditNote = onEditNote,
            accentColor = accentColor
          )
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
                          onOpenAppInfo = onOpenAppInfo,
                          onTogglePin = { onRemovePinnedApp(app) },
                          onToggleDock = onToggleDockApp,
                          iconSize = 52.dp,
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

        // Customize NOS Widgets Port Button
        item {
          Button(
            onClick = { showWidgetSheet = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = theme.surface,
              contentColor = theme.textPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .border(1.dp, theme.border, RoundedCornerShape(14.dp))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "+ CUSTOMIZE NOS 3.5 WIDGETS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // Bottom Persistent Nothing Dock & Search (With Animated Visibility & Show-on-demand)
      AnimatedVisibility(
        visible = isBarsVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Minimal collapse handle
          Box(
            modifier = Modifier
              .padding(bottom = 2.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(theme.surface.copy(alpha = 0.5f))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isBarsVisible = false
              }
              .padding(horizontal = 16.dp, vertical = 3.dp)
              .testTag("collapse_dock_handle"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Hide Dock",
              tint = theme.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }

          NothingDock(
            dockApps = dockApps,
            onAppClick = onAppClick,
            onOpenDrawer = onOpenDrawer,
            onOpenSearch = onOpenDrawer,
            iconPack = settings.iconPack,
            accentColor = accentColor,
            showSearchBar = settings.showSearchBarOnDock,
            onToggleDockApp = onToggleDockApp,
            onOpenAppInfo = onOpenAppInfo
          )
        }
      }

      // Minimal Show-on-demand Pill for Bottom Dock ("ظهورها عند الطلب")
      AnimatedVisibility(
        visible = !isBarsVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(22.dp))
              .background(theme.dockBg.copy(alpha = 0.90f))
              .border(1.dp, theme.border, RoundedCornerShape(22.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isBarsVisible = true
              }
              .padding(horizontal = 18.dp, vertical = 8.dp)
              .testTag("show_dock_pill")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Show Dock",
                tint = accentColor,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "DOCK & APPS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = theme.textPrimary
              )
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(theme.dockButtonBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Apps,
                  contentDescription = null,
                  tint = if (theme.isDark) accentColor else NothingBlack,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
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

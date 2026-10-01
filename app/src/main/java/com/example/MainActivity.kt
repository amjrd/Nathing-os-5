package com.example

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LauncherClockStyle
import com.example.model.LauncherScreen
import com.example.model.LauncherSettings
import com.example.model.LauncherThemeMode
import com.example.model.LockShortcutType
import com.example.ui.HomeScreen
import com.example.ui.NothingLockScreen
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppDrawerSheet
import com.example.ui.components.EditNoteDialog
import com.example.ui.components.ExpandedFolderSheet
import com.example.ui.components.LauncherSettingsDialog
import com.example.ui.components.NosWidgetPortSheet
import com.example.ui.components.NothingAppInfoSheet
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LauncherViewModel by viewModels()

  // Screen State receiver to lock the launcher screen when phone goes to sleep or wakes
  private val screenStateReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      when (intent?.action) {
        Intent.ACTION_SCREEN_OFF -> {
          if (viewModel.settings.value.lockScreen.isLockScreenEnabled) {
            viewModel.lockLauncherScreen()
          }
        }
        Intent.ACTION_SCREEN_ON -> {
          val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
          val isDeviceLocked = keyguardManager?.isKeyguardLocked == true
          if ((isDeviceLocked || viewModel.settings.value.lockScreen.isLockScreenEnabled) &&
            viewModel.currentScreen.value != LauncherScreen.LOCK_SCREEN
          ) {
            viewModel.lockLauncherScreen()
          }
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Allow Nothing OS lock screen to show over system lock when active and turn screen on
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
    } else {
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
      )
    }

    // Register screen state listener (using ContextCompat for Android 14+ receiver safety)
    val filter = IntentFilter().apply {
      addAction(Intent.ACTION_SCREEN_OFF)
      addAction(Intent.ACTION_SCREEN_ON)
      addAction(Intent.ACTION_USER_PRESENT)
    }
    try {
      androidx.core.content.ContextCompat.registerReceiver(
        this,
        screenStateReceiver,
        filter,
        androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
      )
    } catch (_: Exception) {
      @Suppress("UnspecifiedRegisterReceiverFlag")
      registerReceiver(screenStateReceiver, filter)
    }

    setContent {
      val settings by viewModel.settings.collectAsStateWithLifecycle()
      MyApplicationTheme(themeMode = settings.themeMode) {
        val theme = LocalLauncherTheme.current
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = theme.background,
          contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { _ ->
          NothingLauncherApp(
            viewModel = viewModel,
            settings = settings,
            onDismissKeyguard = { dismissSystemKeyguard() },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    val isDeviceLocked = keyguardManager?.isKeyguardLocked == true
    if (isDeviceLocked && viewModel.settings.value.lockScreen.isLockScreenEnabled) {
      if (viewModel.currentScreen.value != LauncherScreen.LOCK_SCREEN) {
        viewModel.lockLauncherScreen()
      }
    }
  }

  private fun dismissSystemKeyguard() {
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      keyguardManager?.requestDismissKeyguard(this, null)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    val isDeviceLocked = keyguardManager?.isKeyguardLocked == true
    if (isDeviceLocked && viewModel.settings.value.lockScreen.isLockScreenEnabled) {
      viewModel.lockLauncherScreen()
      return
    }

    // If Home button or Launcher icon pressed while currently in App Drawer, close drawer
    if (intent.hasCategory(Intent.CATEGORY_HOME) || intent.action == Intent.ACTION_MAIN) {
      if (viewModel.currentScreen.value == LauncherScreen.APP_DRAWER) {
        viewModel.setScreen(LauncherScreen.HOME)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      unregisterReceiver(screenStateReceiver)
    } catch (_: Exception) {}
  }
}

@Composable
fun NothingLauncherApp(
  viewModel: LauncherViewModel,
  settings: LauncherSettings,
  onDismissKeyguard: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
  val currentDate by viewModel.currentDate.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val toggles by viewModel.toggles.collectAsStateWithLifecycle()
  val fitness by viewModel.fitness.collectAsStateWithLifecycle()
  val audio by viewModel.audio.collectAsStateWithLifecycle()
  val quickNote by viewModel.quickNote.collectAsStateWithLifecycle()
  val storagePct by viewModel.storageUsedPercent.collectAsStateWithLifecycle()
  val ramPct by viewModel.ramUsedPercent.collectAsStateWithLifecycle()
  val folders by viewModel.folders.collectAsStateWithLifecycle()
  val pinnedApps by viewModel.pinnedApps.collectAsStateWithLifecycle()
  val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
  val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val activeFolder by viewModel.activeOpenFolder.collectAsStateWithLifecycle()
  val notifications by viewModel.notifications.collectAsStateWithLifecycle()

  var isEditingNote by remember { mutableStateOf(false) }
  var isSettingsOpen by remember { mutableStateOf(false) }
  var isWidgetSheetOpen by remember { mutableStateOf(false) }

  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  BackHandler(enabled = currentScreen == LauncherScreen.APP_DRAWER || currentScreen == LauncherScreen.LOCK_SCREEN || isSettingsOpen || isWidgetSheetOpen || activeFolder != null) {
    if (currentScreen == LauncherScreen.LOCK_SCREEN) {
      if (settings.lockScreen.securityType == com.example.model.LockSecurityType.SWIPE) {
        viewModel.unlockLauncherScreen()
      }
    } else if (activeFolder != null) {
      viewModel.openFolder(null)
    } else if (isWidgetSheetOpen) {
      isWidgetSheetOpen = false
    } else if (isSettingsOpen) {
      isSettingsOpen = false
    } else if (currentScreen == LauncherScreen.APP_DRAWER) {
      viewModel.setSearchQuery("")
      viewModel.setScreen(LauncherScreen.HOME)
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    HomeScreen(
      currentTime = currentTime,
      currentDate = currentDate,
      weather = weather,
      toggles = toggles,
      fitness = fitness,
      audio = audio,
      quickNote = quickNote,
      storagePct = storagePct,
      ramPct = ramPct,
      folders = folders,
      pinnedApps = pinnedApps,
      dockApps = dockApps,
      settings = settings,
      onAppClick = { app -> viewModel.launchApp(app) },
      onOpenFolder = { folder -> viewModel.openFolder(folder) },
      onToggleFolderEnlarged = { folderId -> viewModel.toggleFolderEnlarged(folderId) },
      onToggleTorch = { viewModel.toggleTorch() },
      onCycleSound = { viewModel.cycleSoundMode() },
      onToggleWeather = { viewModel.toggleWeatherCondition() },
      onAddStep = { viewModel.addSteps() },
      onToggleAudioPlay = { viewModel.toggleAudioPlayback() },
      onNextAudioTrack = { viewModel.nextAudioTrack() },
      onEditNote = { isEditingNote = true },
      onOpenDrawer = { viewModel.setScreen(LauncherScreen.APP_DRAWER) },
      onOpenSettings = { isSettingsOpen = true },
      onSwipeDown = { viewModel.openNotificationsPanel() },
      onDoubleTap = { viewModel.lockScreen() },
      onToggleThemeMode = {
        val newTheme = when (settings.themeMode) {
          LauncherThemeMode.ORIGINAL -> LauncherThemeMode.MONOCHROME_STUDIO
          LauncherThemeMode.MONOCHROME_STUDIO -> LauncherThemeMode.ATMOSPHERE_PASTEL
          LauncherThemeMode.ATMOSPHERE_PASTEL -> LauncherThemeMode.ORIGINAL
        }
        val newSettings = when (newTheme) {
          LauncherThemeMode.ORIGINAL -> settings.copy(
            themeMode = newTheme,
            wallpaperIndex = 0,
            accentColorIndex = 0,
            clockStyle = LauncherClockStyle.DIGITAL,
            iconPack = com.example.model.IconPackStyle.MONOCHROME,
            activeWidgets = listOf(
              com.example.model.NosWidgetPortType.CLOCK_MAIN,
              com.example.model.NosWidgetPortType.WEATHER_MAIN,
              com.example.model.NosWidgetPortType.MINI_CLUSTER_2X2
            )
          )
          LauncherThemeMode.MONOCHROME_STUDIO -> settings.copy(
            themeMode = newTheme,
            wallpaperIndex = 1,
            accentColorIndex = 1,
            clockStyle = LauncherClockStyle.ANALOG,
            iconPack = com.example.model.IconPackStyle.SYSTEM_DEFAULT,
            activeWidgets = listOf(
              com.example.model.NosWidgetPortType.GIANT_CIRCLES_CLUSTER,
              com.example.model.NosWidgetPortType.CALENDAR_DIGITAL_TIME
            )
          )
          LauncherThemeMode.ATMOSPHERE_PASTEL -> settings.copy(
            themeMode = newTheme,
            wallpaperIndex = 2,
            accentColorIndex = 0,
            clockStyle = LauncherClockStyle.ANALOG,
            iconPack = com.example.model.IconPackStyle.SYSTEM_DEFAULT,
            activeWidgets = listOf(
              com.example.model.NosWidgetPortType.STICKER_FOCUS_CLUSTER,
              com.example.model.NosWidgetPortType.CLOCK_MAIN,
              com.example.model.NosWidgetPortType.WEATHER_MAIN
            )
          )
        }
        viewModel.updateSettings(newSettings)
      },
      onToggleClockStyle = {
        val newClock = if (settings.clockStyle == LauncherClockStyle.ANALOG) {
          LauncherClockStyle.DIGITAL
        } else {
          LauncherClockStyle.ANALOG
        }
        viewModel.updateSettings(settings.copy(clockStyle = newClock))
      },
      onReorderPinnedApps = { from, to -> viewModel.movePinnedApp(from, to) },
      onRemovePinnedApp = { app -> viewModel.removePinnedApp(app) },
      onToggleDockApp = { app -> viewModel.toggleDockApp(app) },
      onToggleWidget = { widgetType -> viewModel.toggleWidgetActive(widgetType) },
      onOpenAppInfo = { app -> viewModel.openAppInfo(app) },
      onUpdateSettings = { newSettings -> viewModel.updateSettings(newSettings) },
      modifier = Modifier
        .fillMaxSize()
        .blur(if (currentScreen == LauncherScreen.APP_DRAWER) 22.dp else 0.dp)
    )

    // Single Home gesture arbiter:
    // - Swipe up starting in the bottom zone -> App Drawer
    // - Swipe left starting above the bottom zone -> Google Discover
    // Keeping both decisions in one detector prevents gesture competition.
    if (currentScreen == LauncherScreen.HOME) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            val bottomZonePx = 180.dp.toPx()
            var startX = 0f
            var startY = 0f
            var totalX = 0f
            var totalY = 0f

            detectDragGestures(
              onDragStart = { offset ->
                startX = offset.x
                startY = offset.y
                totalX = 0f
                totalY = 0f
              },
              onDragEnd = {
                val absX = kotlin.math.abs(totalX)
                val absY = kotlin.math.abs(totalY)
                val bottomZone = size.height - bottomZonePx

                when {
                  totalY < -90f && absY > absX && startY >= bottomZone -> {
                    viewModel.setScreen(LauncherScreen.APP_DRAWER)
                  }
                  totalX < -90f && absX > absY && startY < bottomZone -> {
                    com.example.service.SystemPortHelper.launchGoogleFeed(context)
                  }
                }

                startX = 0f
                startY = 0f
                totalX = 0f
                totalY = 0f
              },
              onDragCancel = {
                startX = 0f
                startY = 0f
                totalX = 0f
                totalY = 0f
              },
              onDrag = { change, dragAmount ->
                totalX += dragAmount.x
                totalY += dragAmount.y
                change.consume()
              }
            )
          }
      )
    }

    AnimatedVisibility(
      visible = currentScreen == LauncherScreen.LOCK_SCREEN,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
      NothingLockScreen(
        currentTime = currentTime,
        currentDate = currentDate,
        weather = weather,
        fitness = fitness,
        toggles = toggles,
        notifications = notifications,
        settings = settings,
        onUnlock = {
          viewModel.unlockLauncherScreen()
          onDismissKeyguard()
        },
        onToggleTorch = { viewModel.toggleTorch() },
        onLaunchShortcut = { shortcut: LockShortcutType -> viewModel.launchShortcut(shortcut) },
        onDismissNotification = { id: String -> viewModel.dismissNotification(id) }
      )
    }

    AnimatedVisibility(
      visible = currentScreen == LauncherScreen.APP_DRAWER,
      enter = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      ) + fadeIn(animationSpec = tween(220)),
      exit = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(220)
      ) + fadeOut(animationSpec = tween(180))
    ) {
      AppDrawerSheet(
        apps = installedApps,
        searchQuery = searchQuery,
        onSearchChange = { viewModel.setSearchQuery(it) },
        onAppClick = { app -> viewModel.launchApp(app) },
        onTogglePin = { app -> viewModel.togglePinApp(app) },
        onToggleDock = { app -> viewModel.toggleDockApp(app) },
        onOpenAppInfo = { app -> viewModel.openAppInfo(app) },
        onClose = {
          viewModel.setSearchQuery("")
          viewModel.setScreen(LauncherScreen.HOME)
        },
        iconPack = settings.iconPack,
        accentColor = accentColor,
        iconSizeLevel = settings.iconSizeLevel,
        onToggleThemeMode = {
          val newTheme = when (settings.themeMode) {
            LauncherThemeMode.ORIGINAL -> LauncherThemeMode.MONOCHROME_STUDIO
            LauncherThemeMode.MONOCHROME_STUDIO -> LauncherThemeMode.ATMOSPHERE_PASTEL
            LauncherThemeMode.ATMOSPHERE_PASTEL -> LauncherThemeMode.ORIGINAL
          }
          val newSettings = when (newTheme) {
            LauncherThemeMode.ORIGINAL -> settings.copy(
              themeMode = newTheme,
              wallpaperIndex = 0,
              accentColorIndex = 0,
              clockStyle = LauncherClockStyle.DIGITAL,
              iconPack = com.example.model.IconPackStyle.MONOCHROME,
              activeWidgets = listOf(
                com.example.model.NosWidgetPortType.CLOCK_MAIN,
                com.example.model.NosWidgetPortType.WEATHER_MAIN,
                com.example.model.NosWidgetPortType.MINI_CLUSTER_2X2
              )
            )
            LauncherThemeMode.MONOCHROME_STUDIO -> settings.copy(
              themeMode = newTheme,
              wallpaperIndex = 1,
              accentColorIndex = 1,
              clockStyle = LauncherClockStyle.ANALOG,
              iconPack = com.example.model.IconPackStyle.SYSTEM_DEFAULT,
              activeWidgets = listOf(
                com.example.model.NosWidgetPortType.GIANT_CIRCLES_CLUSTER,
                com.example.model.NosWidgetPortType.CALENDAR_DIGITAL_TIME
              )
            )
            LauncherThemeMode.ATMOSPHERE_PASTEL -> settings.copy(
              themeMode = newTheme,
              wallpaperIndex = 2,
              accentColorIndex = 0,
              clockStyle = LauncherClockStyle.ANALOG,
              iconPack = com.example.model.IconPackStyle.SYSTEM_DEFAULT,
              activeWidgets = listOf(
                com.example.model.NosWidgetPortType.STICKER_FOCUS_CLUSTER,
                com.example.model.NosWidgetPortType.CLOCK_MAIN,
                com.example.ui.theme.MyApplicationTheme@TODO
              )
            )
          }
          viewModel.updateSettings(newSettings)
        },
        onSelectIconPack = { pack ->
          viewModel.updateSettings(settings.copy(iconPack = pack))
        },
        onOpenSettings = { isSettingsOpen = true }
      )
    }

    activeFolder?.let { folder ->
      ExpandedFolderSheet(
        folder = folder,
        onDismiss = { viewModel.openFolder(null) },
        onAppClick = { app -> viewModel.launchApp(app) },
        onToggleEnlarged = { viewModel.toggleFolderEnlarged(folder.id) },
        iconPack = settings.iconPack,
        accentColor = accentColor
      )
    }

    if (isSettingsOpen) {
      LauncherSettingsDialog(
        settings = settings,
        onUpdateSettings = { viewModel.updateSettings(it) },
        onPickCustomWallpaper = { uri, target -> viewModel.setCustomWallpaper(uri, target) },
        onRemoveCustomWallpaper = { target -> viewModel.clearCustomWallpaper(target) },
        onOpenWidgetCustomizer = {
          isWidgetSheetOpen = true
          isSettingsOpen = false
        },
        onLockScreenNow = {
          viewModel.lockScreen()
          isSettingsOpen = false
        },
        onDismiss = { isSettingsOpen = false },
        accentColor = accentColor
      )
    }

    if (isWidgetSheetOpen) {
      NosWidgetPortSheet(
        activeWidgets = settings.activeWidgets,
        onToggleWidget = { widgetType -> viewModel.toggleWidgetActive(widgetType) },
        onDismiss = { isWidgetSheetOpen = false },
        accentColor = accentColor
      )
    }

    if (isEditingNote) {
      EditNoteDialog(
        initialNote = quickNote,
        onSave = { viewModel.updateQuickNote(it) },
        onDismiss = { isEditingNote = false },
        accentColor = accentColor
      )
    }

    val appForInfo by viewModel.selectedAppForInfo.collectAsStateWithLifecycle()
    if (appForInfo != null) {
      NothingAppInfoSheet(
        app = appForInfo!!,
        onDismiss = { viewModel.setAppInfo(null) },
        onLaunchApp = {
          viewModel.launchApp(appForInfo!!)
          viewModel.setAppInfo(null)
        },
        accentColor = accentColor
      )
    }
  }
}

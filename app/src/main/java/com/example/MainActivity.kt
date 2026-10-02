package com.example

import android.app.KeyguardManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
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

  // Android system widget host: keeps real system widgets managed by this launcher.
  private val appWidgetHost = AppWidgetHost(this, 0x4E4F53)
  private val systemWidgetViews = mutableStateListOf<AppWidgetHostView>()
  private var pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

  private val widgetConfigLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      val id = pendingWidgetId
      pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
      if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return@registerForActivityResult
      if (result.resultCode == RESULT_OK) addSystemWidgetView(id)
      else appWidgetHost.deleteAppWidgetId(id)
    }

  private val widgetPickerLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      val id = result.data?.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
      ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

      if (result.resultCode != RESULT_OK || id == AppWidgetManager.INVALID_APPWIDGET_ID) {
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) appWidgetHost.deleteAppWidgetId(id)
        return@registerForActivityResult
      }

      val info = AppWidgetManager.getInstance(this).getAppWidgetInfo(id)
      if (info?.configure != null) {
        pendingWidgetId = id
        try {
          widgetConfigLauncher.launch(
            Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
              component = info.configure
              putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }
          )
        } catch (_: Exception) {
          pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
          appWidgetHost.deleteAppWidgetId(id)
        }
      } else {
        addSystemWidgetView(id)
      }
    }

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
    restoreSystemWidgets()

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
            systemWidgetViews = systemWidgetViews,
            onAddSystemWidget = { launchSystemWidgetPicker() },
            onDismissKeyguard = { dismissSystemKeyguard() },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    try { appWidgetHost.startListening() } catch (_: Exception) {}
  }

  override fun onStop() {
    try { appWidgetHost.stopListening() } catch (_: Exception) {}
    super.onStop()
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

  private fun addSystemWidgetView(appWidgetId: Int) {
    val manager = AppWidgetManager.getInstance(this)
    val info = manager.getAppWidgetInfo(appWidgetId) ?: run {
      appWidgetHost.deleteAppWidgetId(appWidgetId)
      return
    }
    try {
      val view = appWidgetHost.createView(this, appWidgetId, info)
      view.setAppWidget(appWidgetId, info)
      systemWidgetViews.removeAll { it.appWidgetId == appWidgetId }
      systemWidgetViews.add(view)
      persistSystemWidgetIds()
    } catch (_: Exception) {
      appWidgetHost.deleteAppWidgetId(appWidgetId)
    }
  }

  private fun restoreSystemWidgets() {
    val ids = getSharedPreferences("system_widgets", MODE_PRIVATE)
      .getStringSet("ids", emptySet()).orEmpty()
      .mapNotNull { it.toIntOrNull() }
    ids.forEach { id ->
      if (AppWidgetManager.getInstance(this).getAppWidgetInfo(id) != null) {
        addSystemWidgetView(id)
      }
    }
  }

  private fun persistSystemWidgetIds() {
    getSharedPreferences("system_widgets", MODE_PRIVATE)
      .edit()
      .putStringSet("ids", systemWidgetViews.map { it.appWidgetId.toString() }.toSet())
      .apply()
  }

  private fun launchSystemWidgetPicker() {
    val id = appWidgetHost.allocateAppWidgetId()
    try {
      widgetPickerLauncher.launch(
        Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
          putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        }
      )
    } catch (_: Exception) {
      appWidgetHost.deleteAppWidgetId(id)
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
  systemWidgetViews: List<AppWidgetHostView> = emptyList(),
  onAddSystemWidget: () -> Unit = {},
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

  // Handle hardware back press gracefully
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
    // 1. Home Screen
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
      systemWidgetViews = systemWidgetViews,
      onAddSystemWidget = onAddSystemWidget,
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

    // Home gestures: keep Google Feed and App Drawer in separate touch zones.
    // Google Feed is deliberately disabled in the lower 220dp so an upward
    // swipe from the dock can NEVER be interpreted as a horizontal Google swipe.
    if (currentScreen == LauncherScreen.HOME) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxSize()
          .padding(bottom = 220.dp)
          .pointerInput(Unit) {
            var totalRight = 0f
            detectHorizontalDragGestures(
              onDragStart = { totalRight = 0f },
              onDragEnd = { totalRight = 0f },
              onDragCancel = { totalRight = 0f },
              onHorizontalDrag = { _, amount ->
                totalRight += amount
                // Require a deliberate horizontal gesture before launching Google.
                if (totalRight > 150f) {
                  com.example.service.SystemPortHelper.launchGoogleFeed(context)
                  totalRight = 0f
                }
              }
            )
          }
      )

    }

    // 1.5. Signature Nothing OS 5 Lock Screen
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

    // 2. App Drawer Screen (Animated slide in from bottom with spring & fade)
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
        drawerCardSizeLevel = settings.drawerCardSizeLevel,
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
        onSelectIconPack = { pack ->
          viewModel.updateSettings(settings.copy(iconPack = pack))
        },
        onOpenSettings = { isSettingsOpen = true }
      )
    }

    // 3. Expanded Folder Dialog
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

    // 4. Launcher Settings Dialog
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

    // 4.5. NOS Widgets Port Customizer Bottom Sheet
    if (isWidgetSheetOpen) {
      NosWidgetPortSheet(
        activeWidgets = settings.activeWidgets,
        onToggleWidget = { widgetType -> viewModel.toggleWidgetActive(widgetType) },
        onDismiss = { isWidgetSheetOpen = false },
        accentColor = accentColor
      )
    }

    // 5. Quick Memo Edit Dialog
    if (isEditingNote) {
      EditNoteDialog(
        initialNote = quickNote,
        onSave = { viewModel.updateQuickNote(it) },
        onDismiss = { isEditingNote = false },
        accentColor = accentColor
      )
    }

    // 6. Signature Nothing OS 5 App Info Sheet (Guaranteed App Info display)
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

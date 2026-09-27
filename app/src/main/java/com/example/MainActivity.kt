package com.example

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.example.ui.components.NothingAppInfoSheet
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LauncherViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

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
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    val km = getSystemService(KeyguardManager::class.java)
    if (km != null && !km.isKeyguardLocked && viewModel.settings.value.lockScreen.preventSystemLockOverlap) {
      if (viewModel.currentScreen.value == LauncherScreen.LOCK_SCREEN) {
        viewModel.unlockLauncherScreen()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
  }
}

@Composable
fun NothingLauncherApp(
  viewModel: LauncherViewModel,
  settings: LauncherSettings,
  modifier: Modifier = Modifier
) {
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

  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  // Handle hardware back press gracefully
  BackHandler(enabled = currentScreen == LauncherScreen.APP_DRAWER || currentScreen == LauncherScreen.LOCK_SCREEN || isSettingsOpen || activeFolder != null) {
    if (currentScreen == LauncherScreen.LOCK_SCREEN) {
      if (settings.lockScreen.securityType == com.example.model.LockSecurityType.SWIPE) {
        viewModel.unlockLauncherScreen()
      }
    } else if (activeFolder != null) {
      viewModel.openFolder(null)
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
        viewModel.updateSettings(settings.copy(themeMode = newTheme))
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
      onOpenAppInfo = { app -> viewModel.openAppInfo(app) }
    )

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
        onUnlock = { viewModel.unlockLauncherScreen() },
        onToggleTorch = { viewModel.toggleTorch() },
        onLaunchShortcut = { shortcut: LockShortcutType -> viewModel.launchShortcut(shortcut) },
        onDismissNotification = { id: String -> viewModel.dismissNotification(id) }
      )
    }

    // 2. App Drawer Screen (Animated slide in/out)
    AnimatedVisibility(
      visible = currentScreen == LauncherScreen.APP_DRAWER,
      enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
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
        onToggleThemeMode = {
          val newTheme = when (settings.themeMode) {
            LauncherThemeMode.ORIGINAL -> LauncherThemeMode.MONOCHROME_STUDIO
            LauncherThemeMode.MONOCHROME_STUDIO -> LauncherThemeMode.ATMOSPHERE_PASTEL
            LauncherThemeMode.ATMOSPHERE_PASTEL -> LauncherThemeMode.ORIGINAL
          }
          viewModel.updateSettings(settings.copy(themeMode = newTheme))
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
        onLockScreenNow = {
          viewModel.lockScreen()
          isSettingsOpen = false
        },
        onDismiss = { isSettingsOpen = false },
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

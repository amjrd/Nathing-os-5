package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.LauncherScreen
import com.example.ui.HomeScreen
import com.example.ui.NothingLockScreen
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.components.AppDrawerSheet
import com.example.ui.theme.NothingOSLauncherTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LauncherViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      val settings by viewModel.settings.collectAsState()
      val currentScreen by viewModel.currentScreen.collectAsState()
      val currentTime by viewModel.currentTime.collectAsState()
      val currentDate by viewModel.currentDate.collectAsState()
      val allApps by viewModel.allApps.collectAsState()
      val dockApps by viewModel.dockApps.collectAsState()
      val pinnedApps by viewModel.pinnedApps.collectAsState()
      val folders by viewModel.folders.collectAsState()
      val audioState by viewModel.audioState.collectAsState()
      val fitnessStats by viewModel.fitnessStats.collectAsState()
      val weather by viewModel.weather.collectAsState()

      val accentColor = ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }

      NothingOSLauncherTheme(themeMode = settings.themeMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
          when (currentScreen) {
            LauncherScreen.HOME -> {
              HomeScreen(
                currentTime = currentTime,
                currentDate = currentDate,
                weather = weather,
                audioState = audioState,
                fitnessStats = fitnessStats,
                dockApps = dockApps,
                pinnedApps = pinnedApps,
                folders = folders,
                allApps = allApps,
                settings = settings,
                onAppClick = { app -> viewModel.launchApp(app) },
                onOpenAppDrawer = { viewModel.navigateTo(LauncherScreen.APP_DRAWER) },
                onOpenSettings = {},
                onUpdateSettings = { updated -> viewModel.updateSettings(updated) },
                onToggleDockApp = { app -> viewModel.toggleDockApp(app) }
              )
            }

            LauncherScreen.APP_DRAWER -> {
              AppDrawerSheet(
                apps = allApps,
                onAppClick = { app ->
                  viewModel.launchApp(app)
                  viewModel.navigateTo(LauncherScreen.HOME)
                },
                onDismiss = { viewModel.navigateTo(LauncherScreen.HOME) },
                iconPack = settings.iconPack,
                accentColor = accentColor,
                iconSizeLevel = settings.iconSizeLevel,
                drawerColoredIcons = settings.drawerColoredIcons,
                onToggleColoredIcons = { colored ->
                  viewModel.updateSettings(settings.copy(drawerColoredIcons = colored))
                },
                onOpenAppInfo = { app ->
                  // Handled in dialog/sheet
                }
              )
            }

            LauncherScreen.LOCK -> {
              NothingLockScreen(
                currentTime = currentTime,
                currentDate = currentDate,
                lockSettings = settings.lockScreen,
                audioState = audioState,
                fitnessStats = fitnessStats,
                weatherData = weather,
                wallpaperIndex = settings.wallpaperIndex,
                wallpaperDimPct = settings.wallpaperDimPct,
                customWallpaperUri = settings.customWallpaperUri,
                themeMode = settings.themeMode,
                onUnlock = { viewModel.navigateTo(LauncherScreen.HOME) },
                accentColor = accentColor
              )
            }

            else -> {
              HomeScreen(
                currentTime = currentTime,
                currentDate = currentDate,
                weather = weather,
                audioState = audioState,
                fitnessStats = fitnessStats,
                dockApps = dockApps,
                pinnedApps = pinnedApps,
                folders = folders,
                allApps = allApps,
                settings = settings,
                onAppClick = { app -> viewModel.launchApp(app) },
                onOpenAppDrawer = { viewModel.navigateTo(LauncherScreen.APP_DRAWER) },
                onOpenSettings = {},
                onUpdateSettings = { updated -> viewModel.updateSettings(updated) },
                onToggleDockApp = { app -> viewModel.toggleDockApp(app) }
              )
            }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.loadInstalledApps()
  }
}

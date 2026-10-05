package com.example.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppItem
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.FolderItem
import com.example.model.LauncherScreen
import com.example.model.LauncherSettings
import com.example.model.NosWidgetPortType
import com.example.model.WeatherData
import com.example.service.NothingNotificationListenerService
import com.example.util.LauncherPreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

  private val context: Context get() = getApplication<Application>().applicationContext

  private val _settings = MutableStateFlow(LauncherPreferencesManager.loadSettings(context))
  val settings = _settings.asStateFlow()

  private val _currentScreen = MutableStateFlow(LauncherScreen.HOME)
  val currentScreen = _currentScreen.asStateFlow()

  private val _currentTime = MutableStateFlow("12:00")
  val currentTime = _currentTime.asStateFlow()

  private val _currentDate = MutableStateFlow("Tuesday, Oct 05")
  val currentDate = _currentDate.asStateFlow()

  private val _allApps = MutableStateFlow<List<AppItem>>(emptyList())
  val allApps = _allApps.asStateFlow()

  private val _dockApps = MutableStateFlow<List<AppItem>>(emptyList())
  val dockApps = _dockApps.asStateFlow()

  private val _pinnedApps = MutableStateFlow<List<AppItem>>(emptyList())
  val pinnedApps = _pinnedApps.asStateFlow()

  private val _folders = MutableStateFlow<List<FolderItem>>(emptyList())
  val folders = _folders.asStateFlow()

  private val _audioState = MutableStateFlow(AudioState())
  val audioState = _audioState.asStateFlow()

  private val _fitnessStats = MutableStateFlow(FitnessStats())
  val fitnessStats = _fitnessStats.asStateFlow()

  private val _weather = MutableStateFlow(WeatherData())
  val weather = _weather.asStateFlow()

  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level >= 0 && scale > 0) {
          val pct = (level * 100) / scale
          _fitnessStats.value = _fitnessStats.value.copy(watchBattery = pct)
        }
      }
    }
  }

  private val packageReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      loadInstalledApps()
    }
  }

  init {
    startClockUpdates()
    loadInstalledApps()
    observeNotifications()
    registerReceivers()
    setupDefaultFolders()
  }

  private fun registerReceivers() {
    try {
      context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    } catch (_: Exception) {}

    try {
      val filter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
      }
      context.registerReceiver(packageReceiver, filter)
    } catch (_: Exception) {}
  }

  private fun observeNotifications() {
    viewModelScope.launch {
      NothingNotificationListenerService.packageNotificationCounts.collect { counts ->
        val updated = _allApps.value.map { app ->
          val count = counts[app.packageName] ?: 0
          if (app.notificationCount != count) app.copy(notificationCount = count) else app
        }
        _allApps.value = updated
        _dockApps.value = _dockApps.value.map { app ->
          val count = counts[app.packageName] ?: 0
          if (app.notificationCount != count) app.copy(notificationCount = count) else app
        }
      }
    }
  }

  private fun startClockUpdates() {
    viewModelScope.launch {
      val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
      val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
      while (true) {
        val now = Date()
        _currentTime.value = timeFormat.format(now)
        _currentDate.value = dateFormat.format(now)
        delay(1000L)
      }
    }
  }

  fun loadInstalledApps() {
    viewModelScope.launch {
      try {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
          addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val appList = mutableListOf<AppItem>()

        for (resolveInfo in resolveInfos) {
          val pkg = resolveInfo.activityInfo.packageName
          if (pkg == context.packageName) continue // Don't list launcher itself inside drawer

          val label = resolveInfo.loadLabel(pm).toString()
          val icon = resolveInfo.loadIcon(pm)
          val category = categorizeApp(label, pkg)

          appList.add(
            AppItem(
              packageName = pkg,
              activityName = resolveInfo.activityInfo.name,
              label = label,
              iconDrawable = icon,
              category = category
            )
          )
        }

        appList.sortBy { it.label.lowercase(Locale.ROOT) }
        _allApps.value = appList

        // Populate Dock
        val dockList = appList.take(5)
        _dockApps.value = dockList

        // Populate Pinned
        val pinned = appList.drop(5).take(4)
        _pinnedApps.value = pinned
      } catch (_: Exception) {}
    }
  }

  private fun categorizeApp(label: String, pkg: String): String {
    val low = (label + " " + pkg).lowercase(Locale.ROOT)
    return when {
      low.contains("game") || low.contains("play") -> "Games"
      low.contains("chat") || low.contains("message") || low.contains("whatsapp") || low.contains("telegram") -> "Social"
      low.contains("mail") || low.contains("gmail") || low.contains("phone") || low.contains("call") -> "Communication"
      low.contains("camera") || low.contains("photo") || low.contains("gallery") || low.contains("video") || low.contains("youtube") || low.contains("music") -> "Media"
      else -> "Tools"
    }
  }

  private fun setupDefaultFolders() {
    _folders.value = listOf(
      FolderItem(
        id = "f_media",
        name = "MEDIA",
        appPackages = listOf("com.google.android.youtube", "com.google.android.apps.photos")
      )
    )
  }

  fun updateSettings(newSettings: LauncherSettings) {
    _settings.value = newSettings
    LauncherPreferencesManager.saveSettings(context, newSettings)
  }

  fun toggleDockApp(app: AppItem) {
    val current = _dockApps.value.toMutableList()
    if (current.any { it.packageName == app.packageName }) {
      current.removeAll { it.packageName == app.packageName }
    } else {
      if (current.size < 5) {
        current.add(app)
      } else {
        current[4] = app
      }
    }
    _dockApps.value = current
  }

  fun toggleWidgetActive(type: NosWidgetPortType) {
    val current = _settings.value.activeWidgets.toMutableList()
    if (current.contains(type)) {
      current.remove(type)
    } else {
      current.add(type)
    }
    updateSettings(_settings.value.copy(activeWidgets = current))
  }

  fun navigateTo(screen: LauncherScreen) {
    _currentScreen.value = screen
  }

  fun launchApp(app: AppItem) {
    try {
      val pm = context.packageManager
      val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
      if (launchIntent != null) {
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(launchIntent)
      }
    } catch (_: Exception) {}
  }

  override fun onCleared() {
    super.onCleared()
    try { context.unregisterReceiver(batteryReceiver) } catch (_: Exception) {}
    try { context.unregisterReceiver(packageReceiver) } catch (_: Exception) {}
  }
}

package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LauncherAccessibilityService : AccessibilityService() {

  override fun onServiceConnected() {
    super.onServiceConnected()
    instance = this
    _isConnected.value = true
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    // No-op for launcher gesture control
  }

  override fun onInterrupt() {
    _isConnected.value = false
    instance = null
  }

  override fun onDestroy() {
    super.onDestroy()
    _isConnected.value = false
    if (instance == this) {
      instance = null
    }
  }

  companion object {
    private var instance: LauncherAccessibilityService? = null
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun isAvailable(): Boolean = instance != null

    fun lockScreen(): Boolean {
      val service = instance ?: return false
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
      } else {
        false
      }
    }

    fun openNotifications(): Boolean {
      val service = instance ?: return false
      return service.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    fun openQuickSettings(): Boolean {
      val service = instance ?: return false
      return service.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    fun openRecents(): Boolean {
      val service = instance ?: return false
      return service.performGlobalAction(GLOBAL_ACTION_RECENTS)
    }
  }
}

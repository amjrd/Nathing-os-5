package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NothingNotificationListenerService : NotificationListenerService() {

  companion object {
    private val _packageNotificationCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val packageNotificationCounts = _packageNotificationCounts.asStateFlow()
  }

  override fun onListenerConnected() {
    super.onListenerConnected()
    updateCounts()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    updateCounts()
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    super.onNotificationRemoved(sbn)
    updateCounts()
  }

  private fun updateCounts() {
    try {
      val activeNotifs = activeNotifications ?: return
      val counts = mutableMapOf<String, Int>()
      for (notif in activeNotifs) {
        val pkg = notif.packageName ?: continue
        if (!notif.isOngoing) {
          counts[pkg] = (counts[pkg] ?: 0) + 1
        }
      }
      _packageNotificationCounts.value = counts
    } catch (_: Exception) {}
  }
}

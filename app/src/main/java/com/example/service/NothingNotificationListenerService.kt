package com.example.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.model.LockNotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NothingNotificationListenerService : NotificationListenerService() {

  override fun onListenerConnected() {
    super.onListenerConnected()
    instance = this
    _isListening.value = true
    updateActiveNotifications()
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    _isListening.value = false
    instance = null
    _activePackages.value = emptySet()
    _activeNotificationList.value = emptyList()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    updateActiveNotifications()
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    super.onNotificationRemoved(sbn)
    updateActiveNotifications()
  }

  private fun updateActiveNotifications() {
    try {
      val notifications = activeNotifications ?: emptyArray()
      val packageMap = mutableMapOf<String, Int>()
      val itemList = mutableListOf<LockNotificationItem>()
      val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

      for (sbn in notifications) {
        val pkg = sbn.packageName ?: continue
        if (!sbn.isOngoing) {
          packageMap[pkg] = (packageMap[pkg] ?: 0) + 1

          val extras = sbn.notification?.extras
          val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
          val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()

          if (!title.isNullOrBlank() || !text.isNullOrBlank()) {
            val appLabel = try {
              val appInfo = packageManager.getApplicationInfo(pkg, 0)
              packageManager.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
              pkg.substringAfterLast('.').uppercase()
            }

            val baseKey = sbn.key?.takeIf { it.isNotBlank() } ?: "${sbn.packageName}_${sbn.id}_${sbn.postTime}"
            val uniqueKey = "${baseKey}_${itemList.size}"
            if (itemList.none { it.id == uniqueKey }) {
              itemList.add(
                LockNotificationItem(
                  id = uniqueKey,
                  packageName = pkg,
                  appName = appLabel,
                  title = title ?: appLabel,
                  text = text ?: "",
                  timeFormatted = timeFormat.format(Date(sbn.postTime))
                )
              )
            }
          }
        }
      }
      _packageNotificationCounts.value = packageMap
      _activePackages.value = packageMap.keys
      _activeNotificationList.value = itemList
    } catch (_: Exception) {
      // Ignored if permission revoked
    }
  }

  companion object {
    private var instance: NothingNotificationListenerService? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _activePackages = MutableStateFlow<Set<String>>(emptySet())
    val activePackages: StateFlow<Set<String>> = _activePackages.asStateFlow()

    private val _packageNotificationCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val packageNotificationCounts: StateFlow<Map<String, Int>> = _packageNotificationCounts.asStateFlow()

    private val _activeNotificationList = MutableStateFlow<List<LockNotificationItem>>(emptyList())
    val activeNotificationList: StateFlow<List<LockNotificationItem>> = _activeNotificationList.asStateFlow()

    fun getNotificationCount(packageName: String): Int {
      return _packageNotificationCounts.value[packageName] ?: 0
    }
  }
}

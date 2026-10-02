package com.example.service

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Real Android AppWidget host for the launcher.
 *
 * The previous launcher had only an unused AppWidgetHostView hook in Compose.
 * This manager owns the real host lifecycle, widget picker/bind flow, persistence,
 * and creation of the provider's AppWidgetHostView.
 */
class SystemWidgetHostManager(
  private val activity: Activity,
  private val onViewChanged: (AppWidgetHostView?) -> Unit
) {
  companion object {
    const val HOST_ID = 0x4E4F53
    const val REQUEST_PICK = 0x501
    const val REQUEST_BIND = 0x502
    const val REQUEST_CONFIGURE = 0x503
    private const val PREFS = "system_widget_host"
    private const val KEY_WIDGET_ID = "widget_id"
    private const val INVALID_ID = AppWidgetManager.INVALID_APPWIDGET_ID
  }

  private val appWidgetManager = AppWidgetManager.getInstance(activity)
  private val host = AppWidgetHost(activity, HOST_ID)
  private val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
  private var pendingWidgetId = INVALID_ID

  init {
    host.startListening()
    restoreWidget()
  }

  fun startPicker() {
    pendingWidgetId = host.allocateAppWidgetId()
    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
      putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId)
    }
    activity.startActivityForResult(intent, REQUEST_PICK)
  }

  fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode != REQUEST_PICK && requestCode != REQUEST_BIND && requestCode != REQUEST_CONFIGURE) {
      return false
    }

    val returnedId = data?.getIntExtra(
      AppWidgetManager.EXTRA_APPWIDGET_ID,
      pendingWidgetId
    ) ?: pendingWidgetId

    if (resultCode != Activity.RESULT_OK) {
      if (returnedId != INVALID_ID) {
        host.deleteAppWidgetId(returnedId)
      }
      pendingWidgetId = INVALID_ID
      return true
    }

    pendingWidgetId = returnedId

    when (requestCode) {
      REQUEST_PICK -> {
        val info = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
        if (info == null) {
          host.deleteAppWidgetId(pendingWidgetId)
          pendingWidgetId = INVALID_ID
          onViewChanged(null)
          return true
        }

        if (appWidgetManager.bindAppWidgetIdIfAllowed(pendingWidgetId, info.provider)) {
          launchConfigurationIfNeeded(info)
        } else {
          val bindIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider)
          }
          activity.startActivityForResult(bindIntent, REQUEST_BIND)
        }
      }

      REQUEST_BIND -> {
        val info = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
        if (info == null) {
          host.deleteAppWidgetId(pendingWidgetId)
          pendingWidgetId = INVALID_ID
          onViewChanged(null)
        } else {
          launchConfigurationIfNeeded(info)
        }
      }

      REQUEST_CONFIGURE -> {
        persistAndDisplay(pendingWidgetId)
      }
    }

    return true
  }

  private fun launchConfigurationIfNeeded(info: AppWidgetProviderInfo) {
    val configure = info.configure
    if (configure != null) {
      val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
        component = configure
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId)
      }
      activity.startActivityForResult(intent, REQUEST_CONFIGURE)
    } else {
      persistAndDisplay(pendingWidgetId)
    }
  }

  private fun persistAndDisplay(widgetId: Int) {
    if (widgetId == INVALID_ID) return

    prefs.edit().putInt(KEY_WIDGET_ID, widgetId).apply()
    pendingWidgetId = INVALID_ID
    displayWidget(widgetId)
  }

  private fun restoreWidget() {
    val widgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
    if (widgetId == INVALID_ID) {
      onViewChanged(null)
      return
    }

    val info = appWidgetManager.getAppWidgetInfo(widgetId)
    if (info == null) {
      prefs.edit().remove(KEY_WIDGET_ID).apply()
      host.deleteAppWidgetId(widgetId)
      onViewChanged(null)
      return
    }

    displayWidget(widgetId)
  }

  private fun displayWidget(widgetId: Int) {
    try {
      val info = appWidgetManager.getAppWidgetInfo(widgetId)
      if (info == null) {
        onViewChanged(null)
        return
      }

      val view = host.createView(activity, widgetId, info)
      view.setAppWidget(widgetId, info)
      view.setPadding(0, 0, 0, 0)
      onViewChanged(view)
    } catch (_: Exception) {
      prefs.edit().remove(KEY_WIDGET_ID).apply()
      host.deleteAppWidgetId(widgetId)
      onViewChanged(null)
    }
  }

  fun removeWidget() {
    val widgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
    if (widgetId != INVALID_ID) {
      host.deleteAppWidgetId(widgetId)
    }
    prefs.edit().remove(KEY_WIDGET_ID).apply()
    pendingWidgetId = INVALID_ID
    onViewChanged(null)
  }

  fun onResume() {
    try {
      host.startListening()
      val widgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
      if (widgetId != INVALID_ID) displayWidget(widgetId)
    } catch (_: Exception) {}
  }

  fun onPause() {
    try {
      host.stopListening()
    } catch (_: Exception) {}
  }

  fun destroy() {
    try {
      host.stopListening()
    } catch (_: Exception) {}
  }
}

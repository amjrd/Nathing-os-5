package com.example.service

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.util.TypedValue

/**
 * Real Android AppWidget host for the launcher.
 *
 * Owns the complete picker -> bind -> configure -> display lifecycle and
 * persists the selected widget so it can be restored after restarting the launcher.
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
  private var previousWidgetId = INVALID_ID

  init {
    host.startListening()
    restoreWidget()
  }

  fun startPicker() {
    // Re-arm the host immediately before launching the system picker. Some
    // Android 16/17 builds stop delivering widget lifecycle callbacks after
    // returning from another Activity unless the host is listening again.
    try { host.startListening() } catch (_: Exception) {}

    // Keep the currently displayed widget alive until a replacement is
    // successfully selected. Cancelling the Android picker must not remove it.
    if (pendingWidgetId != INVALID_ID) {
      cleanupWidgetId(pendingWidgetId)
    }

    previousWidgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
    pendingWidgetId = host.allocateAppWidgetId()

    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
      putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId)
    }

    activity.startActivityForResult(intent, REQUEST_PICK)
  }

  fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (
      requestCode != REQUEST_PICK &&
      requestCode != REQUEST_BIND &&
      requestCode != REQUEST_CONFIGURE
    ) {
      return false
    }

    val returnedId = data?.getIntExtra(
      AppWidgetManager.EXTRA_APPWIDGET_ID,
      pendingWidgetId
    ) ?: pendingWidgetId

    if (resultCode != Activity.RESULT_OK) {
      cleanupWidgetId(returnedId)
      pendingWidgetId = INVALID_ID
      previousWidgetId = INVALID_ID
      return true
    }

    pendingWidgetId = returnedId

    when (requestCode) {
      REQUEST_PICK -> {
        val info = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
        if (info == null) {
          failPendingWidget()
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
          failPendingWidget()
        } else {
          launchConfigurationIfNeeded(info)
        }
      }

      REQUEST_CONFIGURE -> {
        // Configuration activities normally return RESULT_OK only after the
        // provider has finished configuring the allocated id.
        if (appWidgetManager.getAppWidgetInfo(pendingWidgetId) == null) {
          failPendingWidget()
        } else {
          persistAndDisplay(pendingWidgetId)
        }
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

    // Only release the old widget after the replacement is ready.
    if (previousWidgetId != INVALID_ID && previousWidgetId != widgetId) {
      cleanupWidgetId(previousWidgetId)
    }

    previousWidgetId = INVALID_ID
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
        failWidget(widgetId)
        return
      }

      val view = host.createView(activity, widgetId, info)

      // Explicitly attach the provider metadata and give Compose a concrete
      // minimum size. Some real Android widgets report WRAP_CONTENT/zero
      // height until their host view receives layout parameters.
      view.setAppWidget(widgetId, info)
      view.setPadding(0, 0, 0, 0)
      view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )

      // Give Android providers a real host size before Compose displays the view.
      val density = activity.resources.displayMetrics.density
      val minWidthDp = info.minWidth.coerceAtLeast(180)
      val minHeightDp = info.minHeight.coerceAtLeast(96)
      try {
        appWidgetManager.updateAppWidgetSize(
          widgetId,
          (minWidthDp * density).toInt(),
          (minHeightDp * density).toInt(),
          (minWidthDp * density).toInt(),
          (minHeightDp * density).toInt()
        )
      } catch (_: Exception) {}
      val minHeightPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        minHeightDp.toFloat(),
        activity.resources.displayMetrics
      ).toInt()
      view.minimumHeight = minHeightPx

      // Post the state change onto the Activity UI queue. This guarantees the
      // Compose state is updated after AppWidgetHostView has been fully created.
      activity.runOnUiThread {
        onViewChanged(view)
      }
    } catch (_: Exception) {
      failWidget(widgetId)
    }
  }

  private fun failPendingWidget() {
    val id = pendingWidgetId
    pendingWidgetId = INVALID_ID
    cleanupWidgetId(id)
    onViewChanged(null)
  }

  private fun failWidget(widgetId: Int) {
    prefs.edit().remove(KEY_WIDGET_ID).apply()
    cleanupWidgetId(widgetId)
    onViewChanged(null)
  }

  private fun cleanupWidgetId(widgetId: Int) {
    if (widgetId != INVALID_ID) {
      try {
        host.deleteAppWidgetId(widgetId)
      } catch (_: Exception) {}
    }
  }

  fun removeWidget() {
    val widgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
    cleanupWidgetId(widgetId)
    prefs.edit().remove(KEY_WIDGET_ID).apply()
    pendingWidgetId = INVALID_ID
    onViewChanged(null)
  }

  fun onResume() {
    try {
      host.startListening()
      val widgetId = prefs.getInt(KEY_WIDGET_ID, INVALID_ID)
      if (widgetId != INVALID_ID) {
        displayWidget(widgetId)
      }
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

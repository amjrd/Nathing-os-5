package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * High-performance, failsafe tactile vibration engine for physical Android devices.
 * Directly addresses "Vibreur عند اللمس لا يعمل" by activating hardware vibrators
 * with system flag bypass (FLAG_IGNORE_GLOBAL_SETTING) and AudioAttributes across Android 8 through 15+.
 */
object VibrationHelper {

  fun vibrateTouch(context: Context, view: View? = null, isHeavy: Boolean = false) {
    // 1. Android View haptic feedback (Direct window manager call with bypass flags)
    try {
      val feedbackConstant = when {
        isHeavy -> HapticFeedbackConstants.LONG_PRESS
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> HapticFeedbackConstants.CONFIRM
        else -> HapticFeedbackConstants.KEYBOARD_TAP
      }
      view?.performHapticFeedback(
        feedbackConstant,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING or HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
      )
    } catch (_: Exception) {}

    // 2. Hardware Vibrator API with AudioAttributes for physical motor actuation
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      if (vibrator != null && vibrator.hasVibrator()) {
        val duration = if (isHeavy) 55L else 30L
        val amplitude = if (isHeavy) 255 else 220

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
              val predefinedEffect = if (isHeavy) {
                VibrationEffect.EFFECT_HEAVY_CLICK
              } else {
                VibrationEffect.EFFECT_CLICK
              }
              VibrationEffect.createPredefined(predefinedEffect)
            } catch (_: Exception) {
              VibrationEffect.createOneShot(duration, amplitude)
            }
          } else {
            VibrationEffect.createOneShot(duration, amplitude)
          }

          val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .build()

          vibrator.vibrate(effect, audioAttrs)
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(duration)
        }
      }
    } catch (_: Exception) {}
  }
}

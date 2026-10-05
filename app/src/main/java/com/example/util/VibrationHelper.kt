package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationHelper {
  fun vibrateTouch(context: Context, isHeavy: Boolean = false) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        val effect = if (isHeavy) {
          VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
        } else {
          VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        }
        vibratorManager?.defaultVibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          val effect = if (isHeavy) {
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
          } else {
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
          }
          vibrator?.vibrate(effect)
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(if (isHeavy) 35L else 18L)
        }
      }
    } catch (_: Exception) {}
  }

  fun vibrateTick(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
      }
    } catch (_: Exception) {}
  }
}

package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import com.example.model.LauncherSettings
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import java.io.File

@Composable
fun NothingWallpaperBackground(
  settings: LauncherSettings,
  isLockScreen: Boolean = false,
  accentColor: Color,
  onDoubleTap: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  val effectiveIndex = if (isLockScreen && settings.lockScreenWallpaperIndex >= 0) {
    settings.lockScreenWallpaperIndex
  } else {
    settings.wallpaperIndex
  }

  val effectiveUri = if (isLockScreen && settings.lockScreenWallpaperIndex >= 0) {
    settings.customLockScreenWallpaperUri ?: settings.customWallpaperUri
  } else {
    settings.customWallpaperUri
  }

  val baseBackgroundColor = if (!theme.isDark && effectiveIndex in listOf(0, 1, 3)) {
    theme.background
  } else {
    NothingBlack
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(baseBackgroundColor)
      .testTag(if (isLockScreen) "lock_wallpaper_bg" else "home_wallpaper_bg")
  ) {
    // 1. Wallpaper Layer
    when (effectiveIndex) {
      0 -> {
        // DOT MATRIX NOIR / BLANC (Day mode: light grey dots on white; Night mode: subtle white dots on black)
        val dotColor = if (theme.isDark) Color.White else Color(0xFF666666)
        val dotAlpha = if (theme.isDark) 0.08f else 0.12f
        Canvas(modifier = Modifier.fillMaxSize().alpha(dotAlpha)) {
          val dotSpacing = 30f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(dotColor, 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }

      1 -> {
        // CARBON MATTE / LIGHT CANVAS (Stealth black in Night, Crisp clean Nothing light in Day)
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(baseBackgroundColor)
        )
      }

      2 -> {
        // RED CIRCUIT GLOW
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
          val w = size.width
          val h = size.height
          drawLine(accentColor, Offset(0f, h * 0.22f), Offset(w * 0.45f, h * 0.22f), strokeWidth = 2.5f)
          drawLine(accentColor, Offset(w * 0.45f, h * 0.22f), Offset(w * 0.7f, h * 0.36f), strokeWidth = 2.5f)
          drawLine(accentColor, Offset(w * 0.7f, h * 0.36f), Offset(w, h * 0.36f), strokeWidth = 2.5f)
          drawCircle(accentColor, 5f, Offset(w * 0.45f, h * 0.22f))
          drawCircle(accentColor, 5f, Offset(w * 0.7f, h * 0.36f))

          drawLine(accentColor, Offset(w * 0.15f, h), Offset(w * 0.15f, h * 0.65f), strokeWidth = 2f)
          drawLine(accentColor, Offset(w * 0.15f, h * 0.65f), Offset(w * 0.55f, h * 0.55f), strokeWidth = 2f)
          drawCircle(accentColor, 4.5f, Offset(w * 0.15f, h * 0.65f))
          drawCircle(accentColor, 4.5f, Offset(w * 0.55f, h * 0.55f))
        }
      }

      3 -> {
        // LIGHT MONOCHROME
        val dotColor = if (theme.isDark) Color.White else Color(0xFF444444)
        Canvas(modifier = Modifier.fillMaxSize().alpha(if (theme.isDark) 0.18f else 0.15f)) {
          val dotSpacing = 24f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(dotColor, 1.4f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }

      4 -> {
        // NOTHING GLYPH SIGNATURE (Asset image)
        Image(
          painter = painterResource(id = R.drawable.img_nothing_wallpaper),
          contentDescription = "Nothing Glyph Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }

      5 -> {
        // RETRO SAGE NOIR (Nothing OS 3.5 Procedural Dark Gradient Aura)
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          drawRect(
            brush = Brush.radialGradient(
              colors = listOf(
                if (theme.isDark) Color(0xFF1E261E) else Color(0xFFD0DDD0),
                if (theme.isDark) Color(0xFF0C0E0C) else Color(0xFFE2EBE2)
              ),
              center = Offset(w * 0.5f, h * 0.35f),
              radius = w * 0.9f
            )
          )
          val dotSpacing = 26f
          val cols = (w / dotSpacing).toInt()
          val rows = (h / dotSpacing).toInt()
          val dotColor = if (theme.isDark) Color(0xFFFFFFFF) else Color(0xFF2E5A2E)
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(dotColor.copy(alpha = 0.07f), 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }

      6 -> {
        // RETRO WIREFRAME GRID
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.14f)) {
          val w = size.width
          val h = size.height
          val stepX = w / 8f
          val stepY = h / 16f
          for (i in 0..8) {
            drawLine(accentColor, Offset(i * stepX, 0f), Offset(i * stepX, h), strokeWidth = 1f)
          }
          for (j in 0..16) {
            drawLine(accentColor, Offset(0f, j * stepY), Offset(w, j * stepY), strokeWidth = 1f)
          }
        }
      }

      7 -> {
        // CUSTOM PHOTO / GALLERY WALLPAPER
        if (!effectiveUri.isNullOrBlank()) {
          val file = File(effectiveUri)
          if (file.exists()) {
            AsyncImage(
              model = file,
              contentDescription = "Custom Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          } else {
            AsyncImage(
              model = effectiveUri,
              contentDescription = "Custom Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        } else {
          // Fallback if no custom photo picked yet
          Image(
            painter = painterResource(id = R.drawable.img_nothing_wallpaper),
            contentDescription = "Default Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
      }

      else -> {
        // Fallback to Dot Matrix
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
          val dotSpacing = 30f
          val cols = (size.width / dotSpacing).toInt()
          val rows = (size.height / dotSpacing).toInt()
          for (i in 0..cols) {
            for (j in 0..rows) {
              drawCircle(Color.White, 1.2f, Offset(i * dotSpacing, j * dotSpacing))
            }
          }
        }
      }
    }

    // 2. Dim Scrim Overlay (Crucial for high legibility of icons and clock over any wallpaper photo)
    val dimAlpha = (settings.wallpaperDimPct / 100f).coerceIn(0f, 0.85f)
    if (dimAlpha > 0f && (effectiveIndex == 4 || effectiveIndex == 5 || effectiveIndex == 7)) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = dimAlpha))
      )
    }

    // 3. Double-tap background detector (non-interfering)
    if (onDoubleTap != null && settings.doubleTapToSleep) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectTapGestures(
              onDoubleTap = { onDoubleTap() }
            )
          }
      )
    }
  }
}

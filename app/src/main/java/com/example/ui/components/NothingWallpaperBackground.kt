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
  onLongPress: (() -> Unit)? = null,
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
    // 1. Wallpaper Layer (Only the 3 Themes + Custom Gallery Photo)
    when (effectiveIndex) {
      0 -> {
        // THEME 1: NOTHING ORIGINAL (Signature Dot Matrix & Matte Noir)
        Box(modifier = Modifier.fillMaxSize().background(NothingBlack)) {
          Image(
            painter = painterResource(id = R.drawable.img_nothing_wallpaper),
            contentDescription = "Nothing Original Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
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

      1 -> {
        // THEME 2: MONOCHROME STUDIO (Screenshot 1: Black & White Bokeh Art)
        Image(
          painter = painterResource(id = R.drawable.img_monochrome_wallpaper),
          contentDescription = "Monochrome Studio Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }

      2 -> {
        // THEME 3: ATMOSPHERE PASTEL (Screenshot 2: Mint Green & Lavender Aura Glow)
        Image(
          painter = painterResource(id = R.drawable.img_pastel_wallpaper),
          contentDescription = "Atmosphere Pastel Wallpaper",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }

      4 -> {
        // THEME 4: GLYPH RED — graphite background with Nothing-style red glyph accents.
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF09090A))) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val red = Color(0xFFE50000)
            val muted = Color(0xFF242428)
            val cx = size.width * 0.72f
            val cy = size.height * 0.34f
            drawCircle(muted, size.minDimension * 0.24f, Offset(cx, cy))
            drawCircle(red.copy(alpha = 0.18f), size.minDimension * 0.20f, Offset(cx, cy))
            drawCircle(red, size.minDimension * 0.012f, Offset(cx, cy))
            drawLine(red, Offset(cx - size.minDimension * 0.16f, cy), Offset(cx + size.minDimension * 0.16f, cy), 3f)
            drawLine(red, Offset(cx, cy - size.minDimension * 0.16f), Offset(cx, cy + size.minDimension * 0.16f), 3f)
            for (row in 0..7) {
              for (col in 0..5) {
                val x = size.width * 0.10f + col * size.width * 0.07f
                val y = size.height * 0.70f + row * size.height * 0.035f
                drawCircle(
                  Color.White.copy(alpha = if ((row + col) % 3 == 0) 0.72f else 0.16f),
                  size.minDimension * 0.0045f,
                  Offset(x, y)
                )
              }
            }
          }
        }
      }

      3 -> {
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
          Image(
            painter = painterResource(id = R.drawable.img_nothing_wallpaper),
            contentDescription = "Default Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }
      }

      else -> {
        // Automatic fallback based on themeMode
        when (settings.themeMode) {
          com.example.model.LauncherThemeMode.MONOCHROME_STUDIO -> {
            Image(
              painter = painterResource(id = R.drawable.img_monochrome_wallpaper),
              contentDescription = "Monochrome Studio Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
          com.example.model.LauncherThemeMode.ATMOSPHERE_PASTEL -> {
            Image(
              painter = painterResource(id = R.drawable.img_pastel_wallpaper),
              contentDescription = "Atmosphere Pastel Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
          else -> {
            Image(
              painter = painterResource(id = R.drawable.img_nothing_wallpaper),
              contentDescription = "Nothing Original Wallpaper",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          }
        }
      }
    }

    // 2. Dim Scrim Overlay
    val dimAlpha = (settings.wallpaperDimPct / 100f).coerceIn(0f, 0.85f)
    if (dimAlpha > 0f) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            if (theme.isDark) Color.Black.copy(alpha = dimAlpha)
            else Color.White.copy(alpha = dimAlpha * 0.35f)
          )
      )
    }

    // 3. Tap & Long-press background detector (non-interfering)
    if ((onDoubleTap != null && settings.doubleTapToSleep) || onLongPress != null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectTapGestures(
              onDoubleTap = if (onDoubleTap != null && settings.doubleTapToSleep) { { onDoubleTap() } } else null,
              onLongPress = if (onLongPress != null) { { onLongPress() } } else null
            )
          }
      )
    }
  }
}

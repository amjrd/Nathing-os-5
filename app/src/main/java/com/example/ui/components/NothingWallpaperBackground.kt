package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import coil.compose.AsyncImage
import com.example.model.LauncherThemeMode
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed

@Composable
fun NothingWallpaperBackground(
  wallpaperIndex: Int,
  themeMode: LauncherThemeMode,
  wallpaperDimPct: Int,
  customWallpaperUri: String?,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current

  Box(modifier = modifier.fillMaxSize()) {
    when (wallpaperIndex) {
      0 -> {
        // Classic Nothing OS Signature Dot Matrix Grid
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 32f
            val dotRadius = 1.2f
            val dotColor = if (theme.isDark) Color(0xFF222226) else Color(0xFFDCDCE0)

            for (x in 0..(size.width / step).toInt()) {
              for (y in 0..(size.height / step).toInt()) {
                drawCircle(
                  color = dotColor,
                  radius = dotRadius,
                  center = Offset(x * step, y * step)
                )
              }
            }
          }
        }
      }

      1 -> {
        // Monochrome Studio Grid
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.4f)
            // Concentric geometric studio circles
            drawCircle(
              color = Color(0xFF26262B),
              radius = size.minDimension * 0.45f,
              center = center,
              style = Stroke(width = 1.5f)
            )
            drawCircle(
              color = Color(0xFF1E1E22),
              radius = size.minDimension * 0.70f,
              center = center,
              style = Stroke(width = 1.5f)
            )
          }
        }
      }

      2 -> {
        // Atmosphere Pastel (Teal / Sage subtle gradient aura)
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1614))
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.7f, size.height * 0.35f)
            drawCircle(
              color = Color(0x334A8478),
              radius = size.minDimension * 0.6f,
              center = center
            )
            drawCircle(
              color = Color(0x2236635A),
              radius = size.minDimension * 0.9f,
              center = center
            )
          }
        }
      }

      3 -> {
        // Custom photo
        if (!customWallpaperUri.isNullOrBlank()) {
          AsyncImage(
            model = customWallpaperUri,
            contentDescription = "Custom Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
          )
        } else {
          // Fallback to theme 0
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(theme.background)
          )
        }
      }

      4 -> {
        // Glyph Red: Dark graphite with signature Nothing red glyph arc
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090A))
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.38f)
            drawCircle(
              color = Color(0xFF1E1E24),
              radius = size.minDimension * 0.48f,
              center = center,
              style = Stroke(width = 2f)
            )
            // Glowing red glyph indicator
            drawCircle(
              color = NothingRed,
              radius = 5f,
              center = Offset(size.width * 0.75f, size.height * 0.38f)
            )
            // Concentric dotted arcs
            for (i in 0..36) {
              val angle = (i * 10) * (Math.PI / 180.0)
              val x = center.x + (size.minDimension * 0.35f * Math.cos(angle)).toFloat()
              val y = center.y + (size.minDimension * 0.35f * Math.sin(angle)).toFloat()
              drawCircle(
                color = if (i % 4 == 0) NothingRed.copy(alpha = 0.8f) else Color(0xFF323238),
                radius = 1.5f,
                center = Offset(x, y)
              )
            }
          }
        }
      }

      else -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
        )
      }
    }

    // Dimming Overlay for legibility
    if (wallpaperDimPct > 0) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = wallpaperDimPct / 100f))
      )
    }
  }
}

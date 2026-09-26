package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGreenAccent
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingUnlitDot
import com.example.ui.theme.NothingWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * Large Nothing OS 5 Clock & Date Widget with authentic Dot Matrix digits.
 */
@Composable
fun NothingClockWidget(
  hours: String,
  minutes: String,
  date: String,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onToggleStyle: () -> Unit = {},
  onOpenClockPort: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val digitColor = if (theme.isDark) NothingWhite else Color(0xFF111111)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable {
        onOpenClockPort()
      }
      .padding(horizontal = 20.dp, vertical = 18.dp)
      .testTag("clock_widget")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "NOTHING OS (5.0)",
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp,
          modifier = Modifier.clickable { onToggleStyle() }
        )
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(accentColor)
            .clickable { onToggleStyle() }
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Authentic Dot Matrix Display
      DotMatrixClock(
        hours = hours,
        minutes = minutes,
        colonColor = accentColor,
        digitColor = digitColor,
        unlitColor = theme.unlitDot,
        dotSize = 5.dp,
        dotSpacing = 2.dp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Date in dot-spaced retro monospace
      Text(
        text = date,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = theme.textPrimary,
        letterSpacing = 2.sp
      )
    }
  }
}

/**
 * Nothing Weather Widget with condition icon & toggleable preview.
 */
@Composable
fun NothingWeatherWidget(
  weather: WeatherInfo,
  onToggleCondition: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onOpenWeatherPort: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable { onOpenWeatherPort() }
      .padding(16.dp)
      .testTag("weather_widget")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = weather.city,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Icon(
          imageVector = if (weather.condition == "SUNNY") Icons.Default.WbSunny else Icons.Default.WbCloudy,
          contentDescription = "Weather condition",
          tint = if (weather.condition == "SUNNY") accentColor else theme.textPrimary,
          modifier = Modifier
            .size(18.dp)
            .clickable { onToggleCondition() }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        DotMatrixString(
          text = "${weather.tempC}°",
          litColor = theme.textPrimary,
          unlitColor = theme.unlitDot,
          dotSize = 3.5.dp,
          dotSpacing = 1.5.dp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = weather.condition,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary,
        letterSpacing = 1.sp
      )

      Text(
        text = "H:${weather.highC}° L:${weather.lowC}°",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        color = theme.textSecondary
      )
    }
  }
}

/**
 * Interactive Quick Toggles: Flashlight, Sound Profile, Battery Level ring.
 */
@Composable
fun NothingQuickTogglesWidget(
  toggles: QuickToggleState,
  onToggleTorch: () -> Unit,
  onCycleSound: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val torchActiveColor = if (!theme.isDark) NothingGreenAccent else accentColor

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(14.dp)
      .testTag("quick_toggles_widget")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "GLYPH & SYSTEM",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        // Battery status in dot matrix
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (toggles.isCharging) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Charging",
              tint = accentColor,
              modifier = Modifier.size(12.dp)
            )
          }
          Text(
            text = "${toggles.batteryLevel}%",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (toggles.batteryLevel <= 20) accentColor else theme.textPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Torch Button (Vibrant Green in Light Theme like Image 3, Red/Accent in Dark Theme)
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (toggles.isTorchOn) torchActiveColor else theme.elevated)
            .clickable { onToggleTorch() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (toggles.isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
            contentDescription = "Flashlight",
            tint = if (toggles.isTorchOn) NothingWhite else theme.textSecondary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Sound Mode Cycler
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (toggles.soundMode == 0) accentColor else theme.elevated)
            .clickable { onCycleSound() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (toggles.soundMode) {
              0 -> Icons.Default.NotificationsOff
              1 -> Icons.Default.Vibration
              else -> Icons.Default.Notifications
            },
            contentDescription = "Sound Mode",
            tint = if (toggles.soundMode == 0) NothingWhite else theme.textSecondary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Circular Battery Gauge
        Box(
          modifier = Modifier.size(44.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background track
            drawCircle(
              color = theme.unlitDot,
              radius = radius,
              center = center,
              style = Stroke(strokeWidth)
            )

            // Progress arc
            val sweep = (toggles.batteryLevel / 100f) * 360f
            drawArc(
              color = if (toggles.batteryLevel <= 20) accentColor else (if (theme.isDark) NothingWhite else NothingBlack),
              startAngle = -90f,
              sweepAngle = sweep,
              useCenter = false,
              topLeft = Offset(center.x - radius, center.y - radius),
              size = Size(radius * 2, radius * 2),
              style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
          }

          Text(
            text = "${toggles.batteryLevel}",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
        }
      }
    }
  }
}

/**
 * Nothing Step / Activity Tracker with circular dot track.
 */
@Composable
fun NothingStepWidget(
  fitness: FitnessStats,
  onAddStep: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable { onAddStep() }
      .padding(16.dp)
      .testTag("step_widget")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PEDOMETER",
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Icon(
          imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
          contentDescription = "Pedometer",
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Dot Progress Arc
        Box(
          modifier = Modifier.size(50.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val count = 20
            val radius = size.minDimension / 2f - 4.dp.toPx()
            val progressFraction = (fitness.steps.toFloat() / fitness.goal).coerceIn(0f, 1f)
            val litCount = (progressFraction * count).toInt()

            for (i in 0 until count) {
              val angle = Math.toRadians((i * (360.0 / count) - 90.0))
              val cx = (size.width / 2) + radius * cos(angle).toFloat()
              val cy = (size.height / 2) + radius * sin(angle).toFloat()
              val isLit = i <= litCount

              drawCircle(
                color = if (isLit) accentColor else theme.unlitDot,
                radius = if (isLit) 3.5.dp.toPx() else 2.5.dp.toPx(),
                center = Offset(cx, cy)
              )
            }
          }

          Text(
            text = "${(fitness.steps * 100 / fitness.goal)}%",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
        }

        Column {
          Text(
            text = "${fitness.steps}",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "${fitness.calories} kcal • ${fitness.distanceKm} km",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = theme.textSecondary
          )
        }
      }
    }
  }
}

/**
 * Nothing Retro Cassette / Media Player Widget with rotating reels.
 */
@Composable
fun NothingCassetteWidget(
  audio: AudioState,
  onTogglePlay: () -> Unit,
  onNextTrack: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val infiniteTransition = rememberInfiniteTransition(label = "cassette")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "reelRotation"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(16.dp)
      .testTag("cassette_widget")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TEENAGE CASSETTE (R)",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (audio.isPlaying) accentColor else theme.textSecondary)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Dual Reels Visualizer
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CassetteReel(
            rotation = if (audio.isPlaying) rotation else 0f,
            accentColor = accentColor
          )
          CassetteReel(
            rotation = if (audio.isPlaying) rotation else 0f,
            accentColor = accentColor
          )
        }

        // Track Info
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp)
        ) {
          Text(
            text = audio.title,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            maxLines = 1
          )
          Text(
            text = audio.artist,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = theme.textSecondary,
            maxLines = 1
          )
        }

        // Controls
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (audio.isPlaying) accentColor else theme.elevated)
              .clickable { onTogglePlay() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (audio.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = "Play/Pause",
              tint = if (audio.isPlaying) NothingWhite else theme.textPrimary,
              modifier = Modifier.size(18.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(theme.elevated)
              .clickable { onNextTrack() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SkipNext,
              contentDescription = "Next Track",
              tint = theme.textPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CassetteReel(rotation: Float, accentColor: Color) {
  val theme = LocalLauncherTheme.current
  Box(
    modifier = Modifier
      .size(36.dp)
      .clip(CircleShape)
      .background(theme.elevated)
      .border(1.dp, theme.border, CircleShape)
      .rotate(rotation),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(28.dp)) {
      val center = Offset(size.width / 2, size.height / 2)
      val spokes = 6
      val radius = size.minDimension / 2f

      for (i in 0 until spokes) {
        val angle = Math.toRadians((i * (360.0 / spokes)))
        val endX = center.x + radius * cos(angle).toFloat()
        val endY = center.y + radius * sin(angle).toFloat()
        drawLine(
          color = theme.textSecondary,
          start = center,
          end = Offset(endX, endY),
          strokeWidth = 1.5.dp.toPx()
        )
      }
      drawCircle(
        color = accentColor,
        radius = 3.dp.toPx(),
        center = center
      )
    }
  }
}

/**
 * Quick Note widget with live edit dialog trigger.
 */
@Composable
fun NothingQuickNoteWidget(
  note: String,
  onEditNote: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable { onEditNote() }
      .padding(16.dp)
      .testTag("quick_note_widget")
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MEMO",
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Edit Note",
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = note,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = theme.textPrimary,
        maxLines = 3,
        lineHeight = 16.sp
      )
    }
  }
}

/**
 * System Storage and RAM Gauge Widget.
 */
@Composable
fun NothingResourceWidget(
  storagePct: Int,
  ramPct: Int,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(16.dp)
      .testTag("resource_widget")
  ) {
    Column {
      Text(
        text = "STORAGE & RAM",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        ResourceMetric(label = "STORAGE", percent = storagePct, accentColor = accentColor)
        ResourceMetric(label = "RAM", percent = ramPct, accentColor = if (theme.isDark) NothingWhite else NothingBlack)
      }
    }
  }
}

@Composable
private fun ResourceMetric(label: String, percent: Int, accentColor: Color) {
  val theme = LocalLauncherTheme.current
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val stroke = 3.dp.toPx()
        val radius = (size.minDimension - stroke) / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
          color = theme.unlitDot,
          radius = radius,
          center = center,
          style = Stroke(stroke)
        )

        drawArc(
          color = accentColor,
          startAngle = -90f,
          sweepAngle = (percent / 100f) * 360f,
          useCenter = false,
          topLeft = Offset(center.x - radius, center.y - radius),
          size = Size(radius * 2, radius * 2),
          style = Stroke(stroke, cap = StrokeCap.Round)
        )
      }
      Text(
        text = "$percent%",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      color = theme.textSecondary
    )
  }
}

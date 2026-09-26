package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingGreenAccent
import com.example.ui.theme.NothingRed
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Signature Nothing OS Analog Clock Widget.
 * Inspired directly by Nothing OS 2.5 / 3.0 (Featured prominently in Image 3 top-left).
 * Features a circular disc dial, rounded minimalist hour & minute hands, and an iconic Nothing accent dot.
 */
@Composable
fun NothingAnalogClockWidget(
  hours: String,
  minutes: String,
  date: String,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onToggleStyle: () -> Unit = {},
  onOpenClockPort: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val isDark = theme.isDark

  val hInt = hours.toIntOrNull() ?: 12
  val mInt = minutes.toIntOrNull() ?: 0

  // Calculate angles
  val minuteAngle = (mInt * 6f) - 90f
  val hourAngle = ((hInt % 12 + mInt / 60f) * 30f) - 90f

  val dialBg = if (isDark) theme.surface else Color(0xFFDEE5DE)
  val dialBorder = if (isDark) theme.border else Color(0xFFCAD2CA)
  val handColor = if (isDark) Color(0xFFF0F0F0) else Color(0xFF161616)
  val dotColor = if (!isDark) NothingGreenAccent else accentColor

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(28.dp))
      .clickable { onOpenClockPort() }
      .padding(18.dp)
      .testTag("nothing_analog_clock_widget")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left side: Round Minimalist Analog Dial (Image 3)
      Box(
        modifier = Modifier
          .size(136.dp)
          .clip(CircleShape)
          .background(dialBg)
          .border(1.5.dp, dialBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
          val centerX = size.width / 2f
          val centerY = size.height / 2f
          val radius = size.width / 2f

          // Subtle hour ticks (12, 3, 6, 9)
          for (i in 0 until 12) {
            val angleRad = (i * 30f) * (PI.toFloat() / 180f)
            val isMain = i % 3 == 0
            val tickLen = if (isMain) 7f else 4f
            val startDist = radius - tickLen
            val endDist = radius - 1f

            val startX = centerX + startDist * cos(angleRad)
            val startY = centerY + startDist * sin(angleRad)
            val endX = centerX + endDist * cos(angleRad)
            val endY = centerY + endDist * sin(angleRad)

            drawLine(
              color = handColor.copy(alpha = if (isMain) 0.35f else 0.15f),
              start = Offset(startX, startY),
              end = Offset(endX, endY),
              strokeWidth = if (isMain) 2.5f else 1.5f,
              cap = StrokeCap.Round
            )
          }

          // Hour Hand (Shorter, robust rounded)
          val hourRad = hourAngle * (PI.toFloat() / 180f)
          val hourLength = radius * 0.52f
          val hourEndX = centerX + hourLength * cos(hourRad)
          val hourEndY = centerY + hourLength * sin(hourRad)
          drawLine(
            color = handColor,
            start = Offset(centerX, centerY),
            end = Offset(hourEndX, hourEndY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
          )

          // Minute Hand (Longer, slender)
          val minRad = minuteAngle * (PI.toFloat() / 180f)
          val minLength = radius * 0.78f
          val minEndX = centerX + minLength * cos(minRad)
          val minEndY = centerY + minLength * sin(minRad)
          drawLine(
            color = handColor,
            start = Offset(centerX, centerY),
            end = Offset(minEndX, minEndY),
            strokeWidth = 4.2f,
            cap = StrokeCap.Round
          )

          // Center Pivot Cap
          drawCircle(
            color = handColor,
            radius = 4f,
            center = Offset(centerX, centerY)
          )

          // Signature Accent Dot at bottom 6 o'clock position (as in Image 3)
          val dotAngleRad = 90f * (PI.toFloat() / 180f)
          val dotDistance = radius * 0.65f
          val dotX = centerX + dotDistance * cos(dotAngleRad)
          val dotY = centerY + dotDistance * sin(dotAngleRad)
          drawCircle(
            color = dotColor,
            radius = 3.5f,
            center = Offset(dotX, dotY)
          )
        }
      }

      // Right side: Info block (Nothing OS, digital time, date, toggle hint)
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(start = 18.dp),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(dotColor)
          )
          Text(
            text = "NOTHING OS",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textSecondary,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "$hours:$minutes",
          fontFamily = FontFamily.Monospace,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = date,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "TAP TO SWITCH CLOCK",
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = theme.textSecondary.copy(alpha = 0.7f),
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingUnlitDot
import com.example.ui.theme.NothingWhite

/**
 * 5x7 Dot Matrix definitions for authentic Nothing OS LED style display.
 */
object DotMatrixPatterns {
  // 5 columns x 7 rows bitmask (each Int in row represents 5 bits)
  val CHAR_MAP: Map<Char, IntArray> = mapOf(
    '0' to intArrayOf(
      0b01110,
      0b10001,
      0b10011,
      0b10101,
      0b11001,
      0b10001,
      0b01110
    ),
    '1' to intArrayOf(
      0b00100,
      0b01100,
      0b00100,
      0b00100,
      0b00100,
      0b00100,
      0b01110
    ),
    '2' to intArrayOf(
      0b01110,
      0b10001,
      0b00001,
      0b00010,
      0b00100,
      0b01000,
      0b11111
    ),
    '3' to intArrayOf(
      0b11110,
      0b00001,
      0b00001,
      0b01110,
      0b00001,
      0b00001,
      0b11110
    ),
    '4' to intArrayOf(
      0b00010,
      0b00110,
      0b01010,
      0b10010,
      0b11111,
      0b00010,
      0b00010
    ),
    '5' to intArrayOf(
      0b11111,
      0b10000,
      0b11110,
      0b00001,
      0b00001,
      0b10001,
      0b01110
    ),
    '6' to intArrayOf(
      0b00110,
      0b01000,
      0b10000,
      0b11110,
      0b10001,
      0b10001,
      0b01110
    ),
    '7' to intArrayOf(
      0b11111,
      0b00001,
      0b00010,
      0b00100,
      0b01000,
      0b01000,
      0b01000
    ),
    '8' to intArrayOf(
      0b01110,
      0b10001,
      0b10001,
      0b01110,
      0b10001,
      0b10001,
      0b01110
    ),
    '9' to intArrayOf(
      0b01110,
      0b10001,
      0b10001,
      0b01111,
      0b00001,
      0b00010,
      0b01100
    ),
    ':' to intArrayOf(
      0b00000,
      0b00100,
      0b00100,
      0b00000,
      0b00100,
      0b00100,
      0b00000
    ),
    '.' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00100,
      0b00100
    ),
    '%' to intArrayOf(
      0b11001,
      0b11010,
      0b00100,
      0b01000,
      0b01011,
      0b10011,
      0b00000
    ),
    '°' to intArrayOf(
      0b01100,
      0b10010,
      0b10010,
      0b01100,
      0b00000,
      0b00000,
      0b00000
    ),
    'C' to intArrayOf(
      0b01110,
      0b10001,
      0b10000,
      0b10000,
      0b10000,
      0b10001,
      0b01110
    ),
    'F' to intArrayOf(
      0b11111,
      0b10000,
      0b11110,
      0b10000,
      0b10000,
      0b10000,
      0b10000
    ),
    '-' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b11111,
      0b00000,
      0b00000,
      0b00000
    ),
    ' ' to intArrayOf(
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000,
      0b00000
    )
  )
}

@Composable
fun DotMatrixChar(
  char: Char,
  modifier: Modifier = Modifier,
  litColor: Color = NothingWhite,
  unlitColor: Color = NothingUnlitDot,
  dotSize: Dp = 3.5.dp,
  dotSpacing: Dp = 1.5.dp,
  showUnlitDots: Boolean = true
) {
  val pattern = DotMatrixPatterns.CHAR_MAP[char.uppercaseChar()]
    ?: DotMatrixPatterns.CHAR_MAP[' ']!!

  val cols = 5
  val rows = 7

  val totalWidth = (dotSize * cols) + (dotSpacing * (cols - 1))
  val totalHeight = (dotSize * rows) + (dotSpacing * (rows - 1))

  Canvas(
    modifier = modifier
      .width(totalWidth)
      .height(totalHeight)
  ) {
    val dotPx = dotSize.toPx()
    val radius = dotPx / 2f
    val spacingPx = dotSpacing.toPx()
    val step = dotPx + spacingPx

    for (row in 0 until rows) {
      val rowBits = pattern[row]
      for (col in 0 until cols) {
        val isLit = (rowBits and (1 shl (cols - 1 - col))) != 0
        val cx = col * step + radius
        val cy = row * step + radius

        if (isLit) {
          drawCircle(
            color = litColor,
            radius = radius,
            center = Offset(cx, cy)
          )
        } else if (showUnlitDots) {
          drawCircle(
            color = unlitColor,
            radius = radius * 0.7f,
            center = Offset(cx, cy)
          )
        }
      }
    }
  }
}

@Composable
fun DotMatrixClock(
  hours: String,
  minutes: String,
  modifier: Modifier = Modifier,
  colonColor: Color = NothingRed,
  digitColor: Color = NothingWhite,
  unlitColor: Color = NothingUnlitDot,
  dotSize: Dp = 4.5.dp,
  dotSpacing: Dp = 2.dp,
  blinkColon: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "clockColon")
  val alpha by if (blinkColon) {
    infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = 0.2f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 800),
        repeatMode = RepeatMode.Reverse
      ),
      label = "colonAlpha"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
  }

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Hours
    hours.forEach { c ->
      DotMatrixChar(char = c, litColor = digitColor, unlitColor = unlitColor, dotSize = dotSize, dotSpacing = dotSpacing)
    }

    // Colon
    DotMatrixChar(
      char = ':',
      litColor = colonColor.copy(alpha = alpha),
      unlitColor = unlitColor,
      dotSize = dotSize,
      dotSpacing = dotSpacing
    )

    // Minutes
    minutes.forEach { c ->
      DotMatrixChar(char = c, litColor = digitColor, unlitColor = unlitColor, dotSize = dotSize, dotSpacing = dotSpacing)
    }
  }
}

@Composable
fun DotMatrixString(
  text: String,
  modifier: Modifier = Modifier,
  litColor: Color = NothingWhite,
  unlitColor: Color = NothingUnlitDot,
  dotSize: Dp = 3.dp,
  dotSpacing: Dp = 1.dp,
  showUnlitDots: Boolean = false
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    text.forEach { char ->
      DotMatrixChar(
        char = char,
        litColor = litColor,
        unlitColor = unlitColor,
        dotSize = dotSize,
        dotSpacing = dotSpacing,
        showUnlitDots = showUnlitDots
      )
    }
  }
}

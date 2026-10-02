package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingWhite
import com.example.util.VibrationHelper

/**
 * Interactive Widget Resize & Edit Frame matching Screenshot 5 (Android 17 / Nothing OS style).
 * Displays:
 * - 8 circular resize handle dots on the 4 corners and 4 edges
 * - Selection border
 * - Top action pill: "✕ Supprimer", "⤢ Redimensionner (1x1 / 2x2 / 4x2)", "✓ OK"
 */
@Composable
fun WidgetResizeFrame(
  isSelected: Boolean,
  sizeMode: Int, // 0: Compact (1x1), 1: Standard (2x2), 2: Expanded (4x2)
  onSelect: () -> Unit,
  onCycleSize: () -> Unit,
  onRemove: () -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current
  val handleColor = Color(0xFFC084FC) // Signature Android 17 lilac / purple resize handle

  val animatedScale by animateFloatAsState(
    targetValue = when (sizeMode) {
      0 -> 0.88f
      1 -> 1.0f
      else -> 1.03f
    },
    animationSpec = spring(),
    label = "widget_scale"
  )

  Box(
    modifier = modifier
      .scale(animatedScale)
      .pointerInput(isSelected) {
        awaitEachGesture {
          val down = awaitFirstDown(requireUnconsumed = false)
          val longPress = awaitLongPressOrCancellation(down.id)
          if (longPress != null) {
            VibrationHelper.vibrateTouch(context, isHeavy = true)
            onSelect()
          }
        }
      }
  ) {
    // Inner Widget Content
    content()

    // Interactive Resize Frame Overlay when selected
    if (isSelected) {
      // 1. Selection Border
      Box(
        modifier = Modifier
          .matchParentSize()
          .border(
            width = 2.dp,
            color = handleColor,
            shape = RoundedCornerShape(24.dp)
          )
      )

      // 2. Eight Purple Circular Resize Handles (Corners + Center Edges)
      // Top-Left
      HandleDot(modifier = Modifier.align(Alignment.TopStart).offset((-5).dp, (-5).dp), color = handleColor)
      // Top-Center
      HandleDot(modifier = Modifier.align(Alignment.TopCenter).offset(0.dp, (-5).dp), color = handleColor, onClick = onCycleSize)
      // Top-Right
      HandleDot(modifier = Modifier.align(Alignment.TopEnd).offset(5.dp, (-5).dp), color = handleColor)

      // Center-Left
      HandleDot(modifier = Modifier.align(Alignment.CenterStart).offset((-5).dp, 0.dp), color = handleColor, onClick = onCycleSize)
      // Center-Right
      HandleDot(modifier = Modifier.align(Alignment.CenterEnd).offset(5.dp, 0.dp), color = handleColor, onClick = onCycleSize)

      // Bottom-Left
      HandleDot(modifier = Modifier.align(Alignment.BottomStart).offset((-5).dp, 5.dp), color = handleColor)
      // Bottom-Center
      HandleDot(modifier = Modifier.align(Alignment.BottomCenter).offset(0.dp, 5.dp), color = handleColor, onClick = onCycleSize)
      // Bottom-Right
      HandleDot(modifier = Modifier.align(Alignment.BottomEnd).offset(5.dp, 5.dp), color = handleColor)

      // 3. Floating Action Pill at Top (Screenshot 5: "✕ Supprimer", "⤢ Taille", "✓ OK")
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .offset(y = (-44).dp)
      ) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (theme.isDark) Color(0xF21C1C22) else Color(0xF2FFFFFF))
            .border(1.dp, handleColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Remove Button
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                VibrationHelper.vibrateTouch(context)
                onRemove()
              }
              .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Remove",
              tint = Color(0xFFEF5350),
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "إزالة",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFEF5350)
            )
          }

          // Cycle Size Button (تكبير / تصغير)
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(handleColor.copy(alpha = 0.15f))
              .clickable {
                VibrationHelper.vibrateTouch(context)
                onCycleSize()
              }
              .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AspectRatio,
              contentDescription = "Resize",
              tint = handleColor,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = when (sizeMode) {
                0 -> "صغير (88%)"
                1 -> "قياسي (100%)"
                else -> "كبير (112%)"
              },
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = handleColor
            )
          }

          // Done Button
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                VibrationHelper.vibrateTouch(context)
                onDismiss()
              }
              .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "OK",
              tint = if (theme.isDark) Color.White else Color(0xFF1E1E1E),
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "تم",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (theme.isDark) Color.White else Color(0xFF1E1E1E)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HandleDot(
  modifier: Modifier = Modifier,
  color: Color,
  onClick: (() -> Unit)? = null
) {
  Box(
    modifier = modifier
      .size(12.dp)
      .clip(CircleShape)
      .background(Color.White)
      .border(2.dp, color, CircleShape)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
  )
}

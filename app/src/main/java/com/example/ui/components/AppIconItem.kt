package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import com.example.util.VibrationHelper

val ACCENT_COLORS = listOf(
  NothingRed,
  Color(0xFFE8E8E8),
  Color(0xFF4A8478),
  Color(0xFFFF9800),
  Color(0xFF2196F3)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
  app: AppItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onOpenAppInfo: ((AppItem) -> Unit)? = null,
  onToggleDock: ((AppItem) -> Unit)? = null,
  onCycleIconSize: (() -> Unit)? = null,
  iconSize: Dp = 52.dp,
  showLabel: Boolean = true,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed,
  isColoredOverride: Boolean = false
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current
  var showContextMenu by remember { mutableStateOf(false) }

  val useColored = isColoredOverride || iconPack == IconPackStyle.COLORFUL

  Column(
    modifier = modifier
      .width(iconSize + 16.dp)
      .combinedClickable(
        onClick = onClick,
        onLongClick = {
          VibrationHelper.vibrateTouch(context)
          showContextMenu = true
        },
        onLongClickLabel = "Options for ${app.label}"
      )
      .padding(vertical = 4.dp)
      .testTag("app_icon_${app.packageName.replace('.', '_')}"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier.size(iconSize),
      contentAlignment = Alignment.Center
    ) {
      // Nothing Signature Icon Circular Base
      Box(
        modifier = Modifier
          .size(iconSize)
          .clip(CircleShape)
          .background(
            if (useColored) {
              theme.surface
            } else if (theme.isDark) {
              Color(0xFF161619)
            } else {
              Color(0xFFFFFFFF)
            }
          )
          .border(
            width = 1.dp,
            color = if (useColored) theme.border.copy(alpha = 0.5f) else theme.border,
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        val bitmap = remember(app.iconDrawable) {
          app.iconDrawable?.let { drawable ->
            try {
              drawable.toBitmap(width = 120, height = 120)
            } catch (_: Exception) {
              null
            }
          }
        }

        if (bitmap != null) {
          if (useColored) {
            // Native Colorful Icon
            androidx.compose.foundation.Image(
              bitmap = bitmap.asImageBitmap(),
              contentDescription = app.label,
              modifier = Modifier
                .size(iconSize * 0.72f)
                .clip(CircleShape)
            )
          } else {
            // Nothing OS Monochrome / Dot-matrix high-contrast filter
            val colorMatrix = remember(theme.isDark) {
              ColorMatrix().apply {
                setToSaturation(0f)
              }
            }
            androidx.compose.foundation.Image(
              bitmap = bitmap.asImageBitmap(),
              contentDescription = app.label,
              colorFilter = ColorFilter.colorMatrix(colorMatrix),
              modifier = Modifier
                .size(iconSize * 0.70f)
                .clip(CircleShape)
            )
          }
        } else {
          // Dot Matrix Initial Letter Fallback
          Canvas(modifier = Modifier.size(iconSize * 0.55f)) {
            val char = app.label.firstOrNull()?.uppercaseChar() ?: '?'
            // Draw minimalist stylized dot initial
            drawCircle(
              color = if (useColored) accentColor else if (theme.isDark) NothingWhite else NothingBlack,
              radius = size.minDimension * 0.35f,
              center = Offset(size.width / 2f, size.height / 2f)
            )
          }
          Text(
            text = (app.label.firstOrNull()?.uppercaseChar() ?: '?').toString(),
            fontFamily = FontFamily.Monospace,
            fontSize = (iconSize.value * 0.36f).sp,
            fontWeight = FontWeight.Bold,
            color = if (useColored) NothingWhite else if (theme.isDark) NothingBlack else NothingWhite
          )
        }
      }

      // Notification Badge - Signature Nothing Red Dot
      if (app.notificationCount > 0) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .align(Alignment.TopEnd)
            .offset(x = 2.dp, y = (-2).dp)
            .clip(CircleShape)
            .background(accentColor)
            .border(1.5.dp, theme.background, CircleShape)
        )
      }
    }

    if (showLabel) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = app.label,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = theme.textPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(iconSize + 16.dp)
      )
    }

    // Context Menu
    DropdownMenu(
      expanded = showContextMenu,
      onDismissRequest = { showContextMenu = false },
      modifier = Modifier
        .background(theme.surface)
        .border(1.dp, theme.border, RoundedCornerShape(12.dp))
    ) {
      DropdownMenuItem(
        text = {
          Text(
            text = "APP INFO",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = theme.textPrimary
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Info, contentDescription = null, tint = accentColor)
        },
        onClick = {
          showContextMenu = false
          onOpenAppInfo?.invoke(app)
        }
      )

      if (onToggleDock != null) {
        DropdownMenuItem(
          text = {
            Text(
              text = if (app.isPinned) "UNPIN FROM DOCK" else "PIN TO DOCK",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = theme.textPrimary
            )
          },
          leadingIcon = {
            Icon(Icons.Default.PushPin, contentDescription = null, tint = theme.textSecondary)
          },
          onClick = {
            showContextMenu = false
            onToggleDock.invoke(app)
          }
        )
      }
    }
  }
}

package com.example.ui.components

import android.content.Context
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NosWidgetPortType
import com.example.model.WeatherInfo
import com.example.service.SystemPortHelper
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic NOS 3.5 & KWGT Widgets Port Suite for Pixel 8 & Android 17.
 * Reproduces the widgets from user reference screenshots:
 * - JUL TUESDAY 07H 10M (Calendar + Digital Time with Dot Matrix)
 * - 4-Circle Mini Cluster (Temp, Dot-Matrix Cloud, ECG pulse wave, Red Recorder)
 * - Retro Text Glance ("TODAY IS TUESDAY AND TIME IS...")
 * - Decibel Sound Meter ("103 dB" with vertical LED dots)
 * - Circular Gauges (73% Music, 57°C Red Flame, 98% Bell)
 * - NOS Tasks Checklist ("Get groceries. Read a book...")
 * - Favorite Contact Pill
 */

/**
 * 1. Calendar & Digital Time Widget (Screenshot 2: JUL TUESDAY 07H 10M)
 */
@Composable
fun NosCalendarDigitalTimeWidget(
  currentTime: String,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onCalendarClick: () -> Unit = {},
  onClockClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val cal = remember { Calendar.getInstance() }
  val monthStr = remember { SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase() }
  val dayStr = remember { SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase() }

  val parts = currentTime.split(":")
  val hours = parts.getOrNull(0) ?: "07"
  val minutes = parts.getOrNull(1) ?: "10"

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable { onCalendarClick() }
      .padding(horizontal = 20.dp, vertical = 18.dp)
      .testTag("nos_calendar_digital_time_widget")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top row: Month in dot matrix + calendar icon + day name
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          DotMatrixString(
            text = monthStr,
            litColor = theme.textPrimary,
            unlitColor = theme.unlitDot,
            dotSize = 3.dp,
            dotSpacing = 1.dp,
            showUnlitDots = false
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = dayStr,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onCalendarClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Pixel Calendar",
            tint = theme.textPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom row: 07 H 10 M in authentic Nothing dot matrix
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onClockClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        // Hours
        DotMatrixString(
          text = hours,
          litColor = theme.textPrimary,
          unlitColor = theme.unlitDot,
          dotSize = 3.6.dp,
          dotSpacing = 1.2.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(6.dp))

        // "H"
        DotMatrixString(
          text = "H",
          litColor = accentColor,
          unlitColor = theme.unlitDot,
          dotSize = 2.4.dp,
          dotSpacing = 1.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Minutes
        DotMatrixString(
          text = minutes,
          litColor = theme.textPrimary,
          unlitColor = theme.unlitDot,
          dotSize = 3.6.dp,
          dotSpacing = 1.2.dp,
          showUnlitDots = false
        )

        Spacer(modifier = Modifier.width(6.dp))

        // "M"
        DotMatrixString(
          text = "M",
          litColor = accentColor,
          unlitColor = theme.unlitDot,
          dotSize = 2.4.dp,
          dotSpacing = 1.dp,
          showUnlitDots = false
        )
      }
    }
  }
}

/**
 * 2. 4-Circle Mini Cluster Widget (Screenshot 2)
 * Arranged in a 2x2 grid:
 * - Circle 1: Temp (14°)
 * - Circle 2: Dot-matrix Cloud glyph
 * - Circle 3: White ECG pulse wave circle
 * - Circle 4: Red Camera / Recorder circle
 */
@Composable
fun NosMiniClusterWidget(
  weather: WeatherInfo,
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  onWeatherClick: () -> Unit = {},
  onHealthClick: () -> Unit = {},
  onRecorderClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current

  Column(
    modifier = modifier.testTag("nos_mini_cluster_widget"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Row 1: Temp + Cloud glyph
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Circle 1: Temp (e.g. 14°)
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onWeatherClick() },
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Text(
            text = "${weather.tempC}",
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "°",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
      }

      // Circle 2: Dot-Matrix Cloud Glyph
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onWeatherClick() },
        contentAlignment = Alignment.Center
      ) {
        DotMatrixCloudGlyph(
          color = theme.textPrimary,
          modifier = Modifier.size(34.dp)
        )
      }
    }

    // Row 2: ECG Pulse Wave + Red Recorder
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Circle 3: White ECG Pulse Wave
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(if (theme.isDark) Color(0xFFE2E6E2) else Color.White)
          .border(1.dp, Color(0xFFCCCCCC), CircleShape)
          .clickable { onHealthClick() },
        contentAlignment = Alignment.Center
      ) {
        EcgPulseWaveCanvas(
          lineColor = Color(0xFF161616),
          modifier = Modifier
            .width(42.dp)
            .height(24.dp)
        )
      }

      // Circle 4: Nothing Red Recorder / Action Pill
      Box(
        modifier = Modifier
          .weight(1f)
          .height(76.dp)
          .clip(CircleShape)
          .background(accentColor)
          .clickable { onRecorderClick() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Videocam,
          contentDescription = "Quick Recorder / Camera",
          tint = NothingWhite,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

/**
 * 3. Text Glance Summary Widget (Screenshot 2)
 * Retro monospace status banner:
 * "TODAY IS TUESDAY AND TIME IS 7:06 AM. IT IS 14°C OUTSIDE AND CONDITION IS OVERCAST CLOUDS..."
 */
@Composable
fun NosGlanceTextWidget(
  currentTime: String,
  weather: WeatherInfo,
  batteryPct: Int,
  isCharging: Boolean,
  modifier: Modifier = Modifier,
  onGlanceClick: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val cal = remember { Calendar.getInstance() }
  val dayName = remember { SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase() }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(22.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(22.dp))
      .clickable { onGlanceClick() }
      .padding(18.dp)
      .testTag("nos_glance_text_widget")
  ) {
    Column {
      Text(
        text = "TODAY IS $dayName AND TIME IS $currentTime. IT IS ${weather.tempC}°C OUTSIDE AND CONDITION IS ${weather.condition} CLOUDS. BATTERY LEVEL IS $batteryPct% AND ${if (isCharging) "CHARGING" else "DISCHARGING"}.",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Wed 12:00-11:59 • Muharram & Pixel Glance",
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        color = theme.textSecondary,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * 4. Decibel Sound Meter Widget (Screenshot 1: 103 dB with vertical dot LED column)
 */
@Composable
fun NosDecibelWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  var dbValue by remember { mutableIntStateOf(103) }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .clickable {
        dbValue = (75..110).random()
      }
      .padding(18.dp)
      .testTag("nos_decibel_widget")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = "$dbValue",
          fontFamily = FontFamily.Monospace,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary
        )
        Text(
          text = "dB",
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          color = theme.textSecondary,
          modifier = Modifier.padding(bottom = 6.dp)
        )
      }

      // Vertical LED Dot Meter (Screenshot 1: dots stack with red peak)
      Column(
        verticalArrangement = Arrangement.spacedBy(3.5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val totalDots = 10
        val activeDots = ((dbValue - 60).coerceIn(0, 50) / 5)
        for (i in (totalDots - 1) downTo 0) {
          val isLit = i < activeDots
          val dotColor = when {
            !isLit -> theme.unlitDot
            i >= 8 -> NothingRed
            i >= 6 -> Color(0xFFFFB300)
            else -> theme.textPrimary
          }
          Box(
            modifier = Modifier
              .size(5.dp)
              .clip(CircleShape)
              .background(dotColor)
          )
        }
      }
    }
  }
}

/**
 * 5. NOS 3.5 Circular Progress Gauges (Screenshot 1)
 * - Music 73% circular arc
 * - Red Flame 57°C Device / CPU Temp
 * - Notification Bell 98% circular arc
 */
@Composable
fun NosCircularGaugesWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Music 73% circular ring
    NosProgressRingCircle(
      icon = Icons.Default.MusicNote,
      percent = 73,
      label = "73%",
      accentColor = accentColor,
      modifier = Modifier.weight(1f)
    )

    // 2. Red Flame 57°C (Pixel 8 Thermal & Performance) - True Circle
    Box(
      modifier = Modifier
        .weight(1f)
        .aspectRatio(1f)
        .clip(CircleShape)
        .background(NothingRed)
        .border(1.dp, NothingRed, CircleShape)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = "Device Temp",
          tint = NothingWhite,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "57°C",
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite
        )
      }
    }

    // 3. Bell 98% circular ring
    NosProgressRingCircle(
      icon = Icons.Default.Notifications,
      percent = 98,
      label = "98%",
      accentColor = accentColor,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun NosProgressRingCircle(
  icon: ImageVector,
  percent: Int,
  label: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(CircleShape)
      .background(theme.surface)
      .border(1.dp, theme.border, CircleShape)
      .padding(6.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokeW = 4.dp.toPx()
      val dotRadius = 3.dp.toPx()
      val radius = (size.minDimension / 2f) - strokeW - dotRadius - 4.dp.toPx()
      val center = Offset(size.width / 2f, size.height / 2f)

      // Background track
      drawCircle(
        color = theme.elevated,
        radius = radius,
        center = center,
        style = Stroke(width = strokeW)
      )

      // Foreground arc
      val sweep = 360f * (percent / 100f)
      drawArc(
        color = theme.textPrimary,
        startAngle = -90f,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeW, cap = StrokeCap.Round)
      )

      // Indicator white/accent dot on tip of arc
      val angleRad = Math.toRadians((-90f + sweep).toDouble())
      val dotX = (center.x + radius * cos(angleRad)).toFloat()
      val dotY = (center.y + radius * sin(angleRad)).toFloat()
      drawCircle(
        color = accentColor,
        radius = dotRadius,
        center = Offset(dotX, dotY)
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = theme.textPrimary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
    }
  }
}

/**
 * 6. NOS Tasks & Checklist Widget (Screenshot 1)
 */
@Composable
fun NosQuickListWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val tasks = remember {
    mutableStateListOf(
      "Get groceries." to true,
      "Read a book." to false,
      "Play a game." to false,
      "Enjoy your work." to false,
      "Meditate. Breathe. Live." to false
    )
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(18.dp)
      .testTag("nos_quick_list_widget")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      tasks.forEachIndexed { index, (task, isDone) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              tasks[index] = task to !isDone
            },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) accentColor else theme.textSecondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = task,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (isDone) theme.textSecondary else theme.textPrimary,
            style = androidx.compose.ui.text.TextStyle(
              textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
          )
        }
      }
    }
  }
}

/**
 * 7. Favorite Contact Pill Widget (Screenshot 1)
 */
@Composable
fun NosContactPillWidget(
  modifier: Modifier = Modifier,
  accentColor: Color = NothingRed,
  contactName: String = "Rohit Pakala...",
  onCall: () -> Unit = {},
  onChat: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(24.dp))
      .padding(14.dp)
      .testTag("nos_contact_pill_widget")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Avatar circle
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(theme.elevated),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "RP",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
        }

        Column {
          Text(
            text = contactName,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "FAVORITE CONTACT",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            color = theme.textSecondary
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onCall() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            tint = theme.textPrimary,
            modifier = Modifier.size(16.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .clickable { onChat() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "Message",
            tint = theme.textPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

/**
 * Custom Canvas drawing for Nothing OS Dot Matrix Cloud
 */
@Composable
private fun DotMatrixCloudGlyph(
  color: Color,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val cols = 8
    val rows = 5
    val dotR = 1.8.dp.toPx()
    val step = 3.8.dp.toPx()

    // Cloud silhouette mask: 1 = dot lit
    val cloudMask = arrayOf(
      intArrayOf(0, 0, 1, 1, 1, 0, 0, 0),
      intArrayOf(0, 1, 1, 1, 1, 1, 1, 0),
      intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
      intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
      intArrayOf(0, 1, 1, 1, 1, 1, 1, 0)
    )

    val startX = (size.width - (cols * step)) / 2f + dotR
    val startY = (size.height - (rows * step)) / 2f + dotR

    for (r in 0 until rows) {
      for (c in 0 until cols) {
        if (cloudMask[r][c] == 1) {
          drawCircle(
            color = color,
            radius = dotR,
            center = Offset(startX + c * step, startY + r * step)
          )
        }
      }
    }
  }
}

/**
 * Custom Canvas drawing for ECG pulse wave (Screenshot 2)
 */
@Composable
private fun EcgPulseWaveCanvas(
  lineColor: Color,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val path = Path().apply {
      val w = size.width
      val h = size.height
      val midY = h / 2f

      moveTo(0f, midY)
      lineTo(w * 0.25f, midY)
      lineTo(w * 0.35f, midY - h * 0.25f)
      lineTo(w * 0.45f, midY + h * 0.45f)
      lineTo(w * 0.60f, midY - h * 0.45f)
      lineTo(w * 0.70f, midY + h * 0.20f)
      lineTo(w * 0.80f, midY)
      lineTo(w, midY)
    }

    drawPath(
      path = path,
      color = lineColor,
      style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
  }
}

/**
 * 8. Bottom Sheet for Managing / Toggling NOS 3.5 Widgets Port on Pixel 8 / Android 17
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosWidgetPortSheet(
  activeWidgets: List<NosWidgetPortType>,
  onToggleWidget: (NosWidgetPortType) -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.background,
    scrimColor = Color.Black.copy(alpha = 0.65f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Widgets,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "NOS 3.5 WIDGETS PORT",
              fontFamily = FontFamily.Monospace,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "PIXEL 8 • ANDROID 17 LAUNCHER INTEGRATION",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = theme.textSecondary
          )
        }

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = if (accentColor == Color.White) Color.Black else Color.White
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("DONE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      // External KWGT launcher port action if present
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(14.dp))
          .clickable {
            // Attempt to launch KWGT port app or settings
            val intent = context.packageManager.getLaunchIntentForPackage("org.kustom.widget")
            if (intent != null) {
              context.startActivity(intent)
            } else {
              // fallback to web or system widget manager
              SystemPortHelper.launchPixelWeather(context)
            }
          }
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Extension,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "KWGT NOTHING WIDGETS LINK",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
              )
              Text(
                text = "Launch Kustom KWGT or third-party Nothing Port APKs",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = theme.textSecondary
              )
            }
          }
          Text(
            text = "OPEN",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
      }

      Text(
        text = "ACTIVE WIDGETS ON HOME SCREEN",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textSecondary,
        letterSpacing = 1.sp
      )

      // Toggles for each widget
      WidgetToggleItem(
        title = "CALENDAR + DIGITAL TIME",
        subtitle = "JUL TUESDAY 07H 10M (Screenshot 2)",
        checked = activeWidgets.contains(NosWidgetPortType.CALENDAR_DIGITAL_TIME),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CALENDAR_DIGITAL_TIME) }
      )

      WidgetToggleItem(
        title = "4-CIRCLE MINI CLUSTER (2x2)",
        subtitle = "14° Temp, Cloud Glyph, ECG Pulse, Red Cam",
        checked = activeWidgets.contains(NosWidgetPortType.MINI_CLUSTER_2X2),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.MINI_CLUSTER_2X2) }
      )

      WidgetToggleItem(
        title = "TEXT GLANCE SUMMARY",
        subtitle = "TODAY IS TUESDAY AND TIME IS... (Screenshot 2)",
        checked = activeWidgets.contains(NosWidgetPortType.GLANCE_TEXT_SUMMARY),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.GLANCE_TEXT_SUMMARY) }
      )

      WidgetToggleItem(
        title = "CIRCULAR GAUGES (3 RINGS)",
        subtitle = "73% Music, 57°C Red Flame, 98% Bell (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.CIRCULAR_GAUGES),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CIRCULAR_GAUGES) }
      )

      WidgetToggleItem(
        title = "DECIBEL SOUND METER",
        subtitle = "103 dB with vertical LED dots (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.DECIBEL_SOUND_METER),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.DECIBEL_SOUND_METER) }
      )

      WidgetToggleItem(
        title = "NOS TASKS CHECKLIST",
        subtitle = "Checklist with interactive items (Screenshot 1)",
        checked = activeWidgets.contains(NosWidgetPortType.QUICK_CHECKLIST),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.QUICK_CHECKLIST) }
      )

      WidgetToggleItem(
        title = "FAVORITE CONTACT PILL",
        subtitle = "Contact card with quick call & message",
        checked = activeWidgets.contains(NosWidgetPortType.CONTACT_PILL),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CONTACT_PILL) }
      )

      WidgetToggleItem(
        title = "NOTHING OS MAIN CLOCK",
        subtitle = "Dot Matrix or Minimalist Analog Clock",
        checked = activeWidgets.contains(NosWidgetPortType.CLOCK_MAIN),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.CLOCK_MAIN) }
      )

      WidgetToggleItem(
        title = "WEATHER & QUICK TOGGLES",
        subtitle = "Dynamic Weather + Torch & Sound switches",
        checked = activeWidgets.contains(NosWidgetPortType.WEATHER_MAIN),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.WEATHER_MAIN) }
      )

      WidgetToggleItem(
        title = "GIANT CIRCLES CLUSTER",
        subtitle = "Giant Camera, Rain Weather & Globe Disc (Screenshot 3)",
        checked = activeWidgets.contains(NosWidgetPortType.GIANT_CIRCLES_CLUSTER),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.GIANT_CIRCLES_CLUSTER) }
      )

      WidgetToggleItem(
        title = "STICKER & FOCUS CLUSTER",
        subtitle = "Focus rings, Retro Car sticker & Capsule (Screenshot 5)",
        checked = activeWidgets.contains(NosWidgetPortType.STICKER_FOCUS_CLUSTER),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.STICKER_FOCUS_CLUSTER) }
      )

      WidgetToggleItem(
        title = "NOTHING X EARBUDS WIDGET",
        subtitle = "Earbuds battery 90% & Noise Cancellation (Screenshot 5)",
        checked = activeWidgets.contains(NosWidgetPortType.NOTHING_X_EARBUDS),
        accentColor = accentColor,
        onCheckedChange = { onToggleWidget(NosWidgetPortType.NOTHING_X_EARBUDS) }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun WidgetToggleItem(
  title: String,
  subtitle: String,
  checked: Boolean,
  accentColor: Color,
  onCheckedChange: (Boolean) -> Unit
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(12.dp))
      .clickable { onCheckedChange(!checked) }
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = theme.textPrimary
      )
      Text(
        text = subtitle,
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        color = theme.textSecondary
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = if (accentColor == Color.White) Color.Black else Color.White,
        checkedTrackColor = accentColor,
        uncheckedTrackColor = theme.elevated
      )
    )
  }
}

/**
 * 8. Giant Circles Cluster Widget (Screenshot 3)
 * Displays the signature Nothing OS 5 layout:
 * - Giant Camera circle & Dot-Matrix Rain Weather circle
 * - 4-circle mini cluster (Clock, Composer, Camera, Clock)
 * - Square Weather/Date card ("WEDNESDAY 5 JUL MOSTLY CLOUDY 16°")
 * - Giant Dot-matrix globe/glyph circle
 */
@Composable
fun NosGiantCirclesClusterWidget(
  weather: WeatherInfo,
  currentTime: String,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onLaunchCamera: () -> Unit = {},
  onLaunchWeather: () -> Unit = {}
) {
  val theme = LocalLauncherTheme.current
  val circleBg = if (theme.isDark) Color(0xFFEBEBEB) else Color(0xFFFFFFFF)
  val circleTint = Color(0xFF111111)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Row: 2 Giant Circles (aspectRatio 1:1 prevents oval distortion & clipping)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Circle 1: Giant Camera (Screenshot 3)
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
          .clip(CircleShape)
          .background(circleBg)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onLaunchCamera() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Camera",
          tint = circleTint,
          modifier = Modifier.size(46.dp)
        )
      }

      // Circle 2: Giant Dot-Matrix Rain Weather (Screenshot 3)
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
          .clip(CircleShape)
          .background(circleBg)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onLaunchWeather() },
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Canvas(modifier = Modifier.size(54.dp, 38.dp)) {
            // Dot-matrix cloud outline
            val dotRadius = 2.2f
            for (r in 0..4) {
              for (c in 0..10) {
                val isCloudDot = when (r) {
                  0 -> c in 4..6
                  1 -> c in 2..8
                  2 -> c in 1..9
                  3 -> c in 0..10
                  else -> false
                }
                if (isCloudDot) {
                  drawCircle(
                    color = circleTint,
                    radius = dotRadius,
                    center = Offset(c * 5.2f + 2f, r * 5.2f + 4f)
                  )
                }
              }
            }
            // Rain drop dots
            for (drop in listOf(Offset(12f, 28f), Offset(22f, 32f), Offset(32f, 29f), Offset(42f, 33f))) {
              drawCircle(color = NothingRed, radius = 2f, center = drop)
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${weather.tempC}°",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = circleTint
          )
        }
      }
    }

    // Middle Row: 4 Mini Circles Cluster
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      listOf(
        Icons.Default.Schedule,
        Icons.Default.GraphicEq,
        Icons.Default.CameraAlt,
        Icons.Default.Schedule
      ).forEach { icon ->
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(circleBg)
            .border(1.dp, theme.border, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = circleTint,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    // Bottom Row: Square Rounded Card + Giant Dot-Matrix Glyph Circle (Screenshot 3)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Square rounded Weather Card
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(20.dp))
          .clickable { onLaunchWeather() }
          .padding(14.dp)
      ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
          Text(
            text = "WEDNESDAY\n5 JUL\nMOSTLY\nCLOUDY",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            lineHeight = 14.sp
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Cloud,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "${weather.tempC}°",
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }
        }
      }

      // Right: Giant Dot-Matrix Globe / Glyph Circle
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
          .clip(CircleShape)
          .background(circleBg)
          .border(1.dp, theme.border, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(60.dp)) {
          val dotR = 2.4f
          val cx = size.width / 2f
          val cy = size.height / 2f
          val maxR = 26f
          for (deg in 0 until 360 step 30) {
            val rad = Math.toRadians(deg.toDouble()).toFloat()
            drawCircle(circleTint, dotR, Offset(cx + maxR * cos(rad), cy + maxR * sin(rad)))
            drawCircle(circleTint, dotR, Offset(cx + (maxR * 0.6f) * cos(rad), cy + (maxR * 0.6f) * sin(rad)))
          }
          drawCircle(NothingRed, 3.5f, Offset(cx, cy))
        }
      }
    }
  }
}

/**
 * 9. Sticker & Focus Cluster Widget (Screenshot 5)
 * Displays:
 * - Concentric lines Focus widget
 * - Retro Car sticker badge
 * - Pill capsule widget: "8h 19m Away" with status light
 */
@Composable
fun NosStickerFocusClusterWidget(
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  var carBounced by remember { mutableStateOf(false) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Focus Concentric Lines Widget
    Box(
      modifier = Modifier
        .weight(1f)
        .height(110.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(theme.surface)
        .border(1.dp, theme.border, RoundedCornerShape(20.dp))
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Focus",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          color = theme.textSecondary
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(54.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            for (i in 1..4) {
              drawCircle(
                color = theme.textPrimary.copy(alpha = 0.25f * i),
                radius = 6f * i,
                center = center,
                style = Stroke(width = 1.2f)
              )
            }
            drawCircle(color = accentColor, radius = 3.5f, center = center)
          }
        }
      }
    }

    // Retro Car Sticker & Away Pill Column
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Capsule: 8h 19m Away
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .clip(RoundedCornerShape(19.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(19.dp))
        .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "8h 19m",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = "Away",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            color = theme.textSecondary
          )
        }
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50))
        )
      }

      // Retro Car Sticker Badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(64.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFD7E8D7))
          .clickable { carBounced = !carBounced },
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = "Retro Car",
            tint = Color(0xFF2E5A2E),
            modifier = Modifier.size(28.dp)
          )
          Text(
            text = "NOTHING (1)",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E5A2E)
          )
        }
      }
    }
  }
}

/**
 * 10. Nothing X Earbuds Widget (Screenshot 5)
 * Displays headphones 90% battery & noise cancellation mode switcher
 */
@Composable
fun NosNothingXEarbudsWidget(
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  val theme = LocalLauncherTheme.current
  var noiseMode by remember { mutableIntStateOf(1) } // 0: Off, 1: ANC, 2: Transparency

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Earbuds Battery Card
    Box(
      modifier = Modifier
        .weight(1f)
        .height(100.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(theme.surface)
        .border(1.dp, theme.border, RoundedCornerShape(20.dp))
        .padding(14.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = "Nothing Ear",
            tint = accentColor,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "EAR (2)",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textSecondary
          )
        }
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "90%",
            fontFamily = FontFamily.Monospace,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "BATTERY",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 3.dp)
          )
        }
      }
    }

    // ANC / Transparency Mode Card
    Box(
      modifier = Modifier
        .weight(1f)
        .height(100.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(theme.surface)
        .border(1.dp, theme.border, RoundedCornerShape(20.dp))
        .clickable { noiseMode = (noiseMode + 1) % 3 }
        .padding(14.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "NOISE CONTROL",
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = theme.textSecondary
        )
        Text(
          text = when (noiseMode) {
            1 -> "ANC: HIGH"
            2 -> "TRANSPARENCY"
            else -> "OFF"
          },
          fontFamily = FontFamily.Monospace,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (noiseMode == 1) accentColor else theme.textPrimary
        )
        Text(
          text = "TAP TO SWITCH",
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          color = theme.textSecondary
        )
      }
    }
  }
}


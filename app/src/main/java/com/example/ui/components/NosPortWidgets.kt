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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.NosWidgetPortType
import com.example.model.WeatherData
import com.example.service.SystemPortHelper
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import com.example.util.VibrationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NosCalendarDigitalTimeWidget(
  currentTime: String,
  currentDate: String,
  accentColor: Color = NothingRed,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  val month = rememberFormattedDate("MMM").uppercase(Locale.ROOT)
  val dayOfWeek = rememberFormattedDate("EEEE").uppercase(Locale.ROOT)
  val dayNum = rememberFormattedDate("dd")

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable {
        VibrationHelper.vibrateTouch(context)
        if (onClick != null) onClick() else SystemPortHelper.launchClock(context)
      }
      .padding(horizontal = 20.dp, vertical = 18.dp)
      .testTag("nos_widget_calendar_time")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = "$month $dayOfWeek",
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textSecondary,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "$dayNum • $currentTime",
          fontFamily = FontFamily.Monospace,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary,
          letterSpacing = 1.sp
        )
      }

      // Dot Matrix Pill Clock Icon
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(theme.elevated)
          .border(1.dp, theme.border, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.Schedule,
          contentDescription = "Clock",
          tint = accentColor,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

@Composable
fun NosClockWidget(
  currentTime: String,
  accentColor: Color = NothingRed,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable {
        VibrationHelper.vibrateTouch(context)
        if (onClick != null) onClick() else SystemPortHelper.launchClock(context)
      }
      .padding(18.dp)
      .testTag("nos_widget_clock"),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "NOTHING (R) CLOCK",
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = currentTime,
          fontFamily = FontFamily.Monospace,
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary,
          letterSpacing = 2.sp
        )
      }

      Canvas(modifier = Modifier.size(54.dp)) {
        drawCircle(
          color = theme.border,
          radius = size.minDimension * 0.48f,
          style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
          color = accentColor,
          radius = 3.dp.toPx(),
          center = center
        )
        // Hour and minute hands
        val now = Date()
        val hours = now.hours % 12
        val minutes = now.minutes
        val hourAngle = (hours + minutes / 60f) * 30.0 * (Math.PI / 180.0)
        val minAngle = minutes * 6.0 * (Math.PI / 180.0)

        drawLine(
          color = if (theme.isDark) Color.White else Color.Black,
          start = center,
          end = Offset(
            (center.x + Math.sin(hourAngle) * (size.minDimension * 0.28f)).toFloat(),
            (center.y - Math.cos(hourAngle) * (size.minDimension * 0.28f)).toFloat()
          ),
          strokeWidth = 3.dp.toPx(),
          cap = StrokeCap.Round
        )

        drawLine(
          color = accentColor,
          start = center,
          end = Offset(
            (center.x + Math.sin(minAngle) * (size.minDimension * 0.40f)).toFloat(),
            (center.y - Math.cos(minAngle) * (size.minDimension * 0.40f)).toFloat()
          ),
          strokeWidth = 2.dp.toPx(),
          cap = StrokeCap.Round
        )
      }
    }
  }
}

@Composable
fun NosWeatherWidget(
  weather: WeatherData,
  accentColor: Color = NothingRed,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable {
        VibrationHelper.vibrateTouch(context)
        if (onClick != null) onClick() else SystemPortHelper.launchWeather(context, weather.city)
      }
      .padding(18.dp)
      .testTag("nos_widget_weather")
  ) {
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
            Icons.Default.WbSunny,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "${weather.city.uppercase()} • ${weather.condition.uppercase()}",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textSecondary,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "${weather.temperatureC}°",
            fontFamily = FontFamily.Monospace,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "H:${weather.highC}° L:${weather.lowC}°",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }
      }

      // Minimalist Dot Matrix Weather Icon
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(theme.elevated)
          .border(1.dp, theme.border, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.WbSunny,
          contentDescription = "Weather",
          tint = accentColor,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

// User Requirement: "اربط widget smart watch Casque بالتطبيقات النظام حتى تعمل صحيحة"
@Composable
fun NosEarBatteryWidget(
  audioState: AudioState,
  accentColor: Color = NothingRed,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable {
        VibrationHelper.vibrateTouch(context)
        if (onClick != null) onClick() else SystemPortHelper.launchAudioDeviceSettings(context)
      }
      .padding(18.dp)
      .testTag("nos_widget_ear_battery")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .border(1.dp, theme.border, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.Headphones,
            contentDescription = "Earbuds",
            tint = accentColor,
            modifier = Modifier.size(24.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (audioState.connected) Color(0xFF4CAF50) else Color(0xFF757575))
            )
            Text(
              text = audioState.deviceName.uppercase(),
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 1.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "L: ${audioState.batteryLeft}%  •  R: ${audioState.batteryRight}%  •  CASE: ${audioState.batteryCase}%",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = theme.textSecondary,
            letterSpacing = 0.5.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(theme.elevated)
          .border(1.dp, theme.border, RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "ANC",
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = accentColor
        )
      }
    }
  }
}

// User Requirement: "اربط widget smart watch Casque بالتطبيقات النظام حتى تعمل صحيحة"
@Composable
fun NosWatchStatsWidget(
  fitness: FitnessStats,
  accentColor: Color = NothingRed,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(26.dp))
      .clickable {
        VibrationHelper.vibrateTouch(context)
        if (onClick != null) onClick() else SystemPortHelper.launchWatchFitnessApp(context)
      }
      .padding(18.dp)
      .testTag("nos_widget_watch_stats")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(theme.elevated)
            .border(1.dp, theme.border, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.DirectionsRun,
            contentDescription = "Watch Activity",
            tint = accentColor,
            modifier = Modifier.size(24.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "CMF WATCH PRO 2",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 1.sp
            )
            Text(
              text = "• ${fitness.watchBattery}%",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = theme.textSecondary
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "${fitness.steps}",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
              )
              Text(
                text = "steps",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "${fitness.heartRate} bpm",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = theme.textSecondary
              )
            }
          }
        }
      }

      Canvas(modifier = Modifier.size(36.dp)) {
        val sweep = (fitness.steps / 10000f).coerceIn(0f, 1f) * 360f
        drawCircle(
          color = theme.border,
          radius = size.minDimension * 0.45f,
          style = Stroke(width = 3.dp.toPx())
        )
        drawArc(
          color = accentColor,
          startAngle = -90f,
          sweepAngle = sweep,
          useCenter = false,
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosWidgetPortSheet(
  activeWidgets: List<NosWidgetPortType>,
  hasSystemWidget: Boolean = false,
  onToggleWidget: (NosWidgetPortType) -> Unit,
  onAddSystemWidget: () -> Unit = {},
  onRemoveSystemWidget: () -> Unit = {},
  onDismiss: () -> Unit,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = theme.background,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "NOTHING OS 5 WIDGETS",
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 1.sp
          )
          Text(
            text = "Tap to add or remove signature widgets",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = theme.textSecondary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textPrimary)
        }
      }

      val allWidgetTypes = listOf(
        Pair(NosWidgetPortType.CALENDAR_DIGITAL_TIME, "Calendar & Digital Time (Matrix)"),
        Pair(NosWidgetPortType.CLOCK_MAIN, "Analog / Digital Clock"),
        Pair(NosWidgetPortType.WEATHER_MAIN, "Live Weather & High/Low"),
        Pair(NosWidgetPortType.EAR_BATTERY, "Nothing Ear / Casque Companion"),
        Pair(NosWidgetPortType.WATCH_STATS, "CMF Watch Pro 2 Fitness Stats")
      )

      allWidgetTypes.forEach { (type, label) ->
        val isActive = activeWidgets.contains(type)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(theme.surface)
            .border(1.dp, if (isActive) accentColor else theme.border, RoundedCornerShape(18.dp))
            .clickable { onToggleWidget(type) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = theme.textPrimary
          )

          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(if (isActive) accentColor else theme.elevated)
              .border(1.dp, if (isActive) accentColor else theme.border, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isActive) Icons.Default.Check else Icons.Default.Add,
              contentDescription = null,
              tint = if (isActive) Color.White else theme.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

private fun rememberFormattedDate(pattern: String): String {
  return try {
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
  } catch (_: Exception) {
    ""
  }
}

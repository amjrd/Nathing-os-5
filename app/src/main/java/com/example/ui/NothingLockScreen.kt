package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.ui.components.NothingWallpaperBackground
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.model.FitnessStats
import com.example.model.LauncherSettings
import com.example.model.LockClockStyle
import com.example.model.LockNotificationItem
import com.example.model.LockSecurityType
import com.example.model.LockShortcutType
import com.example.model.QuickToggleState
import com.example.model.WeatherInfo
import com.example.service.SystemPortHelper
import com.example.ui.components.ACCENT_COLORS
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingWhite
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun NothingLockScreen(
  currentTime: String,
  currentDate: String,
  weather: WeatherInfo,
  fitness: FitnessStats,
  toggles: QuickToggleState,
  notifications: List<LockNotificationItem>,
  settings: LauncherSettings,
  onUnlock: () -> Unit,
  onToggleTorch: () -> Unit,
  onLaunchShortcut: (LockShortcutType) -> Unit,
  onDismissNotification: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val accentColor = remember(settings.accentColorIndex) {
    ACCENT_COLORS.getOrElse(settings.accentColorIndex) { ACCENT_COLORS[0] }
  }

  val theme = LocalLauncherTheme.current
  val context = LocalContext.current
  val lockSettings = settings.lockScreen
  val scope = rememberCoroutineScope()

  // Drag offset for swipe up gesture
  var dragOffsetY by remember { mutableFloatStateOf(0f) }
  val animatedOffsetY = remember { Animatable(0f) }

  // PIN entry state
  var enteredPin by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }
  var showPinKeypad by remember { mutableStateOf(lockSettings.securityType == LockSecurityType.PIN) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_arrows")
  val arrowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "arrow_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(theme.background)
      .pointerInput(lockSettings.securityType) {
        if (lockSettings.securityType == LockSecurityType.SWIPE || !showPinKeypad) {
          detectVerticalDragGestures(
            onVerticalDrag = { _, dragAmount ->
              if (dragAmount < 0 || dragOffsetY < 0) {
                dragOffsetY += dragAmount
              }
            },
            onDragEnd = {
              if (dragOffsetY < -50f) {
                if (lockSettings.securityType == LockSecurityType.PIN) {
                  showPinKeypad = true
                  dragOffsetY = 0f
                } else {
                  onUnlock()
                }
              } else {
                dragOffsetY = 0f
              }
            }
          )
        }
      }
      .testTag("nothing_lock_screen")
  ) {
    // Dynamic Wallpaper for Lock Screen (Matching or custom lock photo)
    NothingWallpaperBackground(
      settings = settings,
      isLockScreen = true,
      accentColor = accentColor
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .offset {
          val safeY = if (dragOffsetY.isNaN()) 0 else dragOffsetY.coerceIn(-1200f, 0f).roundToInt()
          IntOffset(0, safeY)
        }
        .padding(horizontal = 24.dp)
        .padding(top = 16.dp, bottom = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Glance & Carrier Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = "NOTHING OS 5 • LOCKED",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 2.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Wifi,
            contentDescription = "WiFi",
            tint = theme.textPrimary,
            modifier = Modifier.size(14.dp)
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "${toggles.batteryLevel}%",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Battery",
              tint = if (toggles.isCharging) accentColor else theme.textPrimary,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Lock Screen Clock
      when (lockSettings.clockStyle) {
        LockClockStyle.DOT_MATRIX_BIG -> {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { SystemPortHelper.launchPixelClock(context) }
          ) {
            Text(
              text = currentTime,
              fontFamily = FontFamily.Monospace,
              fontSize = 72.sp,
              fontWeight = FontWeight.Black,
              color = theme.textPrimary,
              letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = currentDate,
              fontFamily = FontFamily.Monospace,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = accentColor,
              letterSpacing = 2.sp
            )
          }
        }

        LockClockStyle.VERTICAL_STACK -> {
          val parts = currentTime.split(":")
          val hour = parts.getOrNull(0) ?: "12"
          val min = parts.getOrNull(1) ?: "00"
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { SystemPortHelper.launchPixelClock(context) }
          ) {
            Text(
              text = hour,
              fontFamily = FontFamily.Monospace,
              fontSize = 76.sp,
              fontWeight = FontWeight.Black,
              color = theme.textPrimary,
              lineHeight = 72.sp
            )
            Text(
              text = min,
              fontFamily = FontFamily.Monospace,
              fontSize = 76.sp,
              fontWeight = FontWeight.Black,
              color = accentColor,
              lineHeight = 72.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = currentDate,
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = theme.textSecondary,
              letterSpacing = 2.sp
            )
          }
        }

        LockClockStyle.MINIMAL_ANALOG -> {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { SystemPortHelper.launchPixelClock(context) }
          ) {
            NothingAnalogLockClock(accentColor = accentColor, isDark = theme.isDark, modifier = Modifier.size(160.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "$currentTime  •  $currentDate",
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary,
              letterSpacing = 2.sp
            )
          }
        }

        LockClockStyle.CLASSIC_DIGITAL -> {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { SystemPortHelper.launchPixelClock(context) }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = accentColor,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = currentTime,
                fontFamily = FontFamily.Monospace,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
              )
            }
            Text(
              text = currentDate,
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              color = theme.textSecondary,
              letterSpacing = 2.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Lock Screen Widgets Pill Bar (if enabled)
      if (lockSettings.showWidgets && !showPinKeypad) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Weather Pill (Tap to open Pixel Weather Port)
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(theme.surface)
              .border(1.dp, theme.border, RoundedCornerShape(20.dp))
              .clickable { SystemPortHelper.launchPixelWeather(context) }
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.WbSunny,
              contentDescription = "Weather",
              tint = accentColor,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${weather.tempC}° ${weather.condition}",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Fitness Pill (Tap to open Health Connect / Google Fit Port)
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(theme.surface)
              .border(1.dp, theme.border, RoundedCornerShape(20.dp))
              .clickable { SystemPortHelper.launchHealthConnect(context) }
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
              contentDescription = "Steps",
              tint = theme.textPrimary,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${fitness.steps}",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = theme.textPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Middle Section: PIN Keypad OR Live Notifications List
      if (showPinKeypad) {
        // PIN Entry Mode
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (pinError) "INCORRECT PIN" else "ENTER PIN CODE",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (pinError) Color(0xFFD71921) else theme.textSecondary,
            letterSpacing = 2.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // 4 PIN Dots
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            (0 until 4).forEach { index ->
              val isFilled = index < enteredPin.length
              Box(
                modifier = Modifier
                  .size(14.dp)
                  .clip(CircleShape)
                  .background(if (isFilled) (if (pinError) Color(0xFFD71921) else accentColor) else Color.Transparent)
                  .border(
                    width = 1.5.dp,
                    color = if (pinError) Color(0xFFD71921) else NothingWhite,
                    shape = CircleShape
                  )
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Keypad Matrix (1 to 9, Back, 0, Check)
          val buttons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("DEL", "0", "OK")
          )

          Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            buttons.forEach { row ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                row.forEach { key ->
                  Box(
                    modifier = Modifier
                      .size(56.dp)
                      .clip(CircleShape)
                      .background(theme.surface)
                      .border(1.dp, theme.border, CircleShape)
                      .clickable {
                        pinError = false
                        when (key) {
                          "DEL" -> {
                            if (enteredPin.isNotEmpty()) {
                              enteredPin = enteredPin.dropLast(1)
                            }
                          }
                          "OK" -> {
                            if (enteredPin == lockSettings.pinCode) {
                              onUnlock()
                            } else {
                              pinError = true
                              enteredPin = ""
                            }
                          }
                          else -> {
                            if (enteredPin.length < 4) {
                              val newPin = enteredPin + key
                              enteredPin = newPin
                              if (newPin.length == 4) {
                                if (newPin == lockSettings.pinCode) {
                                  onUnlock()
                                } else {
                                  pinError = true
                                  enteredPin = ""
                                }
                              }
                            }
                          }
                        }
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    if (key == "DEL") {
                      Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Delete",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(18.dp)
                      )
                    } else if (key == "OK") {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Enter",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                      )
                    } else {
                      Text(
                        text = key,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CANCEL",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = NothingGrey,
              modifier = Modifier.clickable { showPinKeypad = false }
            )
            Text(
              text = "•",
              color = NothingGrey
            )
            Text(
              text = "BYPASS UNLOCK",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = accentColor,
              modifier = Modifier.clickable { onUnlock() }
            )
          }
        }
      } else {
        // Notifications list (stacked Nothing cards)
        if (lockSettings.showNotifications && notifications.isNotEmpty()) {
          val safeNotifications = remember(notifications) {
            notifications.distinctBy { it.id }
          }
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            itemsIndexed(
              items = safeNotifications,
              key = { index, item -> "${item.id}_${item.packageName}_$index" }
            ) { _, notif ->
              LockNotificationCard(
                notification = notif,
                accentColor = accentColor,
                onDismiss = { onDismissNotification(notif.id) }
              )
            }
          }
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }

        // Signature Glyph Tap & Swipe Unlock Sensor
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(theme.surface.copy(alpha = 0.9f))
            .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .clickable {
              if (lockSettings.securityType == LockSecurityType.PIN) {
                showPinKeypad = true
              } else {
                onUnlock()
              }
            }
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .testTag("lock_screen_unlock_sensor")
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Swipe up or tap to unlock",
            tint = accentColor.copy(alpha = arrowAlpha),
            modifier = Modifier.size(26.dp)
          )
          Text(
            text = if (lockSettings.securityType == LockSecurityType.PIN) "TAP OR SWIPE TO ENTER PIN" else "TAP OR SWIPE UP TO UNLOCK",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary.copy(alpha = arrowAlpha),
            letterSpacing = 1.5.sp
          )
        }
      }

      // 5. Bottom Shortcuts & Device Owner Info
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Shortcut (Torch)
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (toggles.isTorchOn) accentColor else theme.surface)
            .border(
              width = 1.dp,
              color = if (toggles.isTorchOn) accentColor else theme.border,
              shape = CircleShape
            )
            .clickable {
              if (lockSettings.leftShortcut == LockShortcutType.TORCH) {
                onToggleTorch()
              } else {
                onLaunchShortcut(lockSettings.leftShortcut)
              }
            },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (toggles.isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
            contentDescription = "Torch Shortcut",
            tint = if (toggles.isTorchOn) NothingBlack else theme.textPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Center Owner Info
        Text(
          text = lockSettings.customOwnerInfo,
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = theme.textSecondary,
          textAlign = TextAlign.Center,
          modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
          maxLines = 1
        )

        // Right Shortcut (Camera)
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(theme.surface)
            .border(1.dp, theme.border, CircleShape)
            .clickable {
              onLaunchShortcut(lockSettings.rightShortcut)
            },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Camera Shortcut",
            tint = theme.textPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun NothingAnalogLockClock(
  accentColor: Color,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  val calendar = Calendar.getInstance()
  val hour = calendar.get(Calendar.HOUR)
  val minute = calendar.get(Calendar.MINUTE)
  val second = calendar.get(Calendar.SECOND)

  Canvas(modifier = modifier) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2 - 8f

    // 12 dot hour markers
    for (i in 0 until 12) {
      val angle = Math.toRadians((i * 30 - 90).toDouble())
      val dotRadius = if (i % 3 == 0) 3.5f else 2f
      val x = center.x + (radius * cos(angle)).toFloat()
      val y = center.y + (radius * sin(angle)).toFloat()
      drawCircle(
        color = if (isDark) (if (i % 3 == 0) Color.White else Color.Gray) else (if (i % 3 == 0) Color(0xFF111111) else Color(0xFF888888)),
        radius = dotRadius,
        center = Offset(x, y)
      )
    }

    // Hour hand
    val hourAngle = Math.toRadians(((hour % 12 + minute / 60f) * 30 - 90).toDouble())
    val hourHandLength = radius * 0.5f
    drawLine(
      color = if (isDark) Color.White else Color(0xFF111111),
      start = center,
      end = Offset(
        center.x + (hourHandLength * cos(hourAngle)).toFloat(),
        center.y + (hourHandLength * sin(hourAngle)).toFloat()
      ),
      strokeWidth = 4f,
      cap = StrokeCap.Round
    )

    // Minute hand
    val minuteAngle = Math.toRadians(((minute + second / 60f) * 6 - 90).toDouble())
    val minuteHandLength = radius * 0.75f
    drawLine(
      color = if (isDark) Color.White else Color(0xFF111111),
      start = center,
      end = Offset(
        center.x + (minuteHandLength * cos(minuteAngle)).toFloat(),
        center.y + (minuteHandLength * sin(minuteAngle)).toFloat()
      ),
      strokeWidth = 2.5f,
      cap = StrokeCap.Round
    )

    // Center Red Nothing Dot
    drawCircle(
      color = accentColor,
      radius = 4f,
      center = center
    )

    // Outer second ticking indicator
    val secondAngle = Math.toRadians((second * 6 - 90).toDouble())
    val secondDotX = center.x + (radius * 0.9f * cos(secondAngle)).toFloat()
    val secondDotY = center.y + (radius * 0.9f * sin(secondAngle)).toFloat()
    drawCircle(
      color = accentColor,
      radius = 2.5f,
      center = Offset(secondDotX, secondDotY)
    )
  }
}

@Composable
private fun LockNotificationCard(
  notification: LockNotificationItem,
  accentColor: Color,
  onDismiss: () -> Unit
) {
  val theme = LocalLauncherTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(16.dp))
      .padding(14.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(theme.elevated),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = notification.appName,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
          Text(
            text = "• ${notification.timeFormatted}",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = theme.textSecondary
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = notification.title,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textPrimary
        )
        if (notification.text.isNotEmpty()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = notification.text,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = theme.textSecondary,
            lineHeight = 14.sp
          )
        }
      }
    }

    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "Dismiss",
      tint = theme.textSecondary,
      modifier = Modifier
        .size(16.dp)
        .clickable { onDismiss() }
    )
  }
}

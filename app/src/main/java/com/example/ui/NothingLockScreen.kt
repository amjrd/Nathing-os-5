package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioState
import com.example.model.FitnessStats
import com.example.model.LockScreenSettings
import com.example.model.LockSecurityType
import com.example.model.WeatherData
import com.example.service.SystemPortHelper
import com.example.ui.components.NothingWallpaperBackground
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import com.example.util.VibrationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NothingLockScreen(
  currentTime: String,
  currentDate: String,
  lockSettings: LockScreenSettings,
  audioState: AudioState,
  fitnessStats: FitnessStats,
  weatherData: WeatherData,
  wallpaperIndex: Int,
  wallpaperDimPct: Int,
  customWallpaperUri: String?,
  themeMode: com.example.model.LauncherThemeMode,
  onUnlock: () -> Unit,
  accentColor: Color = NothingRed,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  var showPinKeypad by remember { mutableStateOf(false) }
  var enteredPin by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }

  val formattedDay = remember {
    try { SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date()).uppercase(Locale.ROOT) }
    catch (_: Exception) { currentDate.uppercase(Locale.ROOT) }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(lockSettings.securityType) {
        detectVerticalDragGestures { _, dragAmount ->
          if (dragAmount < -50f) {
            VibrationHelper.vibrateTouch(context)
            if (lockSettings.securityType == LockSecurityType.PIN) {
              showPinKeypad = true
            } else {
              onUnlock()
            }
          }
        }
      }
      .testTag("nothing_lock_screen")
  ) {
    // Wallpaper & Ambient Dim
    NothingWallpaperBackground(
      wallpaperIndex = wallpaperIndex,
      themeMode = themeMode,
      wallpaperDimPct = (wallpaperDimPct + 25).coerceAtMost(80),
      customWallpaperUri = customWallpaperUri
    )

    // Main Lockscreen Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar: Lock Status Pill
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(theme.surface.copy(alpha = 0.85f))
          .border(1.dp, theme.border.copy(alpha = 0.75f), RoundedCornerShape(22.dp))
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            Icons.Default.Lock,
            contentDescription = "Locked",
            tint = accentColor,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = "NOTHING OS 5 • LOCKED",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }

      // Middle: Signature Nothing Large Clock & Glanceable Data
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 24.dp)
      ) {
        Text(
          text = currentTime,
          fontFamily = FontFamily.Monospace,
          fontSize = 72.sp,
          fontWeight = FontWeight.Bold,
          color = NothingWhite,
          letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = formattedDay,
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = NothingGrey,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Lockscreen Mini-Widgets Pill Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface.copy(alpha = 0.82f))
            .border(1.dp, theme.border.copy(alpha = 0.70f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Weather Glance
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable { SystemPortHelper.launchWeather(context, weatherData.city) }
          ) {
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            Text(
              text = "${weatherData.temperatureC}° ${weatherData.condition}",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = NothingWhite
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(20.dp)
              .background(theme.border)
          )

          // Ear battery glance
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable { SystemPortHelper.launchAudioDeviceSettings(context) }
          ) {
            Icon(Icons.Default.Headphones, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            Text(
              text = "${audioState.batteryLeft}%",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = NothingWhite
            )
          }
        }
      }

      // Bottom Area: Swipe Up Indicator & Shortcuts
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Swipe Up to Unlock Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(theme.surface.copy(alpha = 0.88f))
            .border(1.dp, accentColor.copy(alpha = 0.75f), RoundedCornerShape(24.dp))
            .clickable {
              VibrationHelper.vibrateTouch(context)
              if (lockSettings.securityType == LockSecurityType.PIN) {
                showPinKeypad = true
              } else {
                onUnlock()
              }
            }
            .padding(horizontal = 22.dp, vertical = 12.dp)
            .testTag("lock_swipe_up_btn"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "SWIPE UP TO UNLOCK",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NothingWhite,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Owner Info & Quick Shortcuts
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left Shortcut: Flashlight
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(theme.surface.copy(alpha = 0.85f))
              .border(1.dp, theme.border, CircleShape)
              .clickable { VibrationHelper.vibrateTouch(context) },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.FlashlightOn,
              contentDescription = "Flashlight",
              tint = NothingWhite,
              modifier = Modifier.size(20.dp)
            )
          }

          // Center Owner Tag
          Text(
            text = if (lockSettings.customOwnerInfo.isBlank()) "NOTHING (R) OS 5" else lockSettings.customOwnerInfo,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = NothingGrey,
            letterSpacing = 1.5.sp
          )

          // Right Shortcut: Camera
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(theme.surface.copy(alpha = 0.85f))
              .border(1.dp, theme.border, CircleShape)
              .clickable {
                VibrationHelper.vibrateTouch(context)
                try {
                  val camIntent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                  context.startActivity(camIntent)
                } catch (_: Exception) {}
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.CameraAlt,
              contentDescription = "Camera",
              tint = NothingWhite,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // PIN Keypad Overlay
    AnimatedVisibility(
      visible = showPinKeypad,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(NothingBlack.copy(alpha = 0.95f))
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = if (pinError) "INCORRECT PIN" else "ENTER PIN",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (pinError) NothingRed else NothingWhite,
            letterSpacing = 2.sp
          )

          // PIN Dots
          Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(vertical = 12.dp)
          ) {
            for (i in 0..3) {
              val isFilled = i < enteredPin.length
              Box(
                modifier = Modifier
                  .size(14.dp)
                  .clip(CircleShape)
                  .background(if (isFilled) accentColor else Color.Transparent)
                  .border(2.dp, if (isFilled) accentColor else NothingGrey, CircleShape)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 3x4 Keypad
          val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "DEL")
          )

          keys.forEach { row ->
            Row(
              horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
              row.forEach { key ->
                Box(
                  modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18181C))
                    .border(1.dp, Color(0xFF2C2C32), CircleShape)
                    .clickable {
                      VibrationHelper.vibrateTouch(context)
                      when (key) {
                        "C" -> {
                          showPinKeypad = false
                          enteredPin = ""
                          pinError = false
                        }
                        "DEL" -> {
                          if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                          pinError = false
                        }
                        else -> {
                          if (enteredPin.length < 4) {
                            enteredPin += key
                            if (enteredPin.length == 4) {
                              if (enteredPin == lockSettings.pinCode) {
                                showPinKeypad = false
                                enteredPin = ""
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
                    Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = NothingWhite, modifier = Modifier.size(20.dp))
                  } else {
                    Text(
                      text = key,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 20.sp,
                      fontWeight = FontWeight.Bold,
                      color = NothingWhite
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

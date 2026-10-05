package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.service.SystemPortHelper
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NothingAppInfoSheet(
  app: AppItem,
  onDismiss: () -> Unit,
  onLaunchApp: (AppItem) -> Unit,
  accentColor: Color = NothingRed
) {
  val context = LocalContext.current
  val theme = LocalLauncherTheme.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = theme.background,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = app.label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
          )
          Text(
            text = app.packageName,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = theme.textSecondary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = theme.textPrimary)
        }
      }

      // App Details Container
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(16.dp))
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("CATEGORY", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = theme.textSecondary)
          Text(app.category.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("UNREAD ALERTS", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = theme.textSecondary)
          Text("${app.notificationCount}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = {
            onDismiss()
            onLaunchApp(app)
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
          Text("  OPEN", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = {
            SystemPortHelper.openAppDetails(context, app.packageName)
            onDismiss()
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp), tint = theme.textPrimary)
          Text("  SYSTEM INFO", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

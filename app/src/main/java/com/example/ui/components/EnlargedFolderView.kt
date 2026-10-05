package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.FolderItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed

@Composable
fun EnlargedFolderView(
  folder: FolderItem,
  apps: List<AppItem>,
  onAppClick: (AppItem) -> Unit,
  onFolderClick: (FolderItem) -> Unit,
  modifier: Modifier = Modifier,
  iconSize: Dp = 48.dp,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current
  val folderApps = apps.filter { it.packageName in folder.appPackages }.take(4)

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(32.dp))
      .background(theme.surface)
      .border(1.dp, theme.border, RoundedCornerShape(32.dp))
      .clickable { onFolderClick(folder) }
      .padding(14.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = folder.name.uppercase(),
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = theme.textSecondary,
          letterSpacing = 1.sp
        )
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        folderApps.forEach { app ->
          AppIconItem(
            app = app,
            onClick = { onAppClick(app) },
            iconSize = iconSize,
            showLabel = false,
            iconPack = iconPack,
            accentColor = accentColor
          )
        }
      }
    }
  }
}

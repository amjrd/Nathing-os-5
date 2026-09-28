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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite

@Composable
fun NothingDock(
  dockApps: List<AppItem>,
  onAppClick: (AppItem) -> Unit,
  onOpenDrawer: () -> Unit,
  onOpenSearch: () -> Unit,
  modifier: Modifier = Modifier,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed,
  showSearchBar: Boolean = true,
  onToggleDockApp: ((AppItem) -> Unit)? = null,
  onOpenAppInfo: ((AppItem) -> Unit)? = null
) {
  val theme = LocalLauncherTheme.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Dock Icons Row - Fully Transparent, Sleek Floating Icons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent)
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 5 Pinned Dock Apps (Transparent Dock without extra drawer square)
      dockApps.take(5).forEach { app ->
        AppIconItem(
          app = app,
          onClick = { onAppClick(app) },
          onOpenAppInfo = onOpenAppInfo,
          onToggleDock = onToggleDockApp,
          iconSize = 52.dp,
          showLabel = false,
          iconPack = iconPack,
          accentColor = accentColor
        )
      }
    }

    if (showSearchBar) {
      Spacer(modifier = Modifier.height(10.dp))

      // Signature Nothing Search Pill (Matches Image 3 rounded search pill)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .clip(RoundedCornerShape(22.dp))
          .background(theme.searchPillBg)
          .border(1.dp, theme.border, RoundedCornerShape(22.dp))
          .clickable { onOpenSearch() }
          .padding(horizontal = 16.dp)
          .testTag("nothing_search_pill"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = theme.textSecondary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = if (!theme.isDark) "Search" else "SEARCH OR TYPE URL...",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = theme.textSecondary,
            letterSpacing = 1.sp
          )
        }

        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(if (!theme.isDark) Color(0xFF4CAF50) else accentColor)
        )
      }
    }
  }
}

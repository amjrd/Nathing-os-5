package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingRed
import java.util.Locale

@Composable
fun AppDrawerSheet(
  apps: List<AppItem>,
  onAppClick: (AppItem) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed,
  iconSizeLevel: Int = 1,
  drawerColoredIcons: Boolean = false,
  onToggleColoredIcons: (Boolean) -> Unit = {},
  onOpenAppInfo: ((AppItem) -> Unit)? = null
) {
  BackHandler { onDismiss() }

  val theme = LocalLauncherTheme.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }

  val currentIconSize = when (iconSizeLevel) {
    0 -> 44.dp
    2 -> 60.dp
    3 -> 68.dp
    else -> 52.dp
  }

  val categories = remember(apps) {
    val list = mutableListOf("ALL")
    val distinctCats = apps.map { it.category }.distinct().filter { it.isNotBlank() }
    list.addAll(distinctCats)
    list
  }

  val filteredApps = remember(apps, searchQuery, selectedCategory) {
    apps.filter { app ->
      val matchesSearch = searchQuery.isBlank() ||
        app.label.contains(searchQuery, ignoreCase = true) ||
        app.packageName.contains(searchQuery, ignoreCase = true)

      val matchesCategory = selectedCategory == "ALL" || app.category == selectedCategory
      matchesSearch && matchesCategory
    }.sortedBy { it.label.lowercase(Locale.ROOT) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(theme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp)
  ) {
    // Top Bar with Search & Color Toggle
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Search Box
      Row(
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(theme.surface)
          .border(1.dp, theme.border, RoundedCornerShape(24.dp))
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          Icons.Default.Search,
          contentDescription = "Search",
          tint = theme.textSecondary,
          modifier = Modifier.size(18.dp)
        )
        BasicTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          singleLine = true,
          textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            color = theme.textPrimary
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("drawer_search_input"),
          decorationBox = { innerTextField ->
            Box {
              if (searchQuery.isEmpty()) {
                Text(
                  text = "SEARCH APPS...",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  color = theme.textSecondary,
                  letterSpacing = 1.sp
                )
              }
              innerTextField()
            }
          }
        )
        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { searchQuery = "" },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              Icons.Default.Close,
              contentDescription = "Clear",
              tint = theme.textSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Color Icon Toggle Button (User feature: "1 app drawer coulor icond")
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(if (drawerColoredIcons) accentColor.copy(alpha = 0.2f) else theme.surface)
          .border(
            1.dp,
            if (drawerColoredIcons) accentColor else theme.border,
            CircleShape
          )
          .clickable { onToggleColoredIcons(!drawerColoredIcons) }
          .testTag("drawer_color_toggle"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.ColorLens,
          contentDescription = "Toggle Colorful Icons",
          tint = if (drawerColoredIcons) accentColor else theme.textSecondary,
          modifier = Modifier.size(22.dp)
        )
      }

      // Close Button
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .clickable { onDismiss() }
          .testTag("drawer_close_button"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.Close,
          contentDescription = "Close Drawer",
          tint = theme.textPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Categories Pill Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.take(5).forEach { cat ->
        val isSelected = selectedCategory == cat
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor else theme.surface)
            .border(
              1.dp,
              if (isSelected) accentColor else theme.border,
              RoundedCornerShape(14.dp)
            )
            .clickable { selectedCategory = cat }
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            text = cat.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else theme.textSecondary,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    // Apps Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(4),
      modifier = Modifier
        .fillMaxSize()
        .testTag("app_drawer_grid"),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      items(filteredApps, key = { it.packageName }) { app ->
        AppIconItem(
          app = app,
          onClick = { onAppClick(app) },
          onOpenAppInfo = onOpenAppInfo,
          iconSize = currentIconSize,
          showLabel = true,
          iconPack = if (drawerColoredIcons) IconPackStyle.COLORFUL else iconPack,
          accentColor = accentColor,
          isColoredOverride = drawerColoredIcons
        )
      }

      item {
        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }
}

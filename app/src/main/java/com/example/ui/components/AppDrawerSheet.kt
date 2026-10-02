package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.NothingRed

private data class DrawerCategory(
  val title: String,
  val apps: List<AppItem>
)

@Composable
fun AppDrawerSheet(
  apps: List<AppItem>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onAppClick: (AppItem) -> Unit,
  onTogglePin: (AppItem) -> Unit,
  onToggleDock: (AppItem) -> Unit,
  onOpenAppInfo: (AppItem) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed,
  iconSizeLevel: Int = 1,
  onToggleThemeMode: () -> Unit = {},
  onSelectIconPack: (IconPackStyle) -> Unit = {},
  onOpenSettings: () -> Unit = {}
) {
  val backgroundColor = Color(0xFF0B0B0C)
  val cardColor = Color(0xFF1C1C1E)
  val searchColor = Color(0xFF2C2C2E)
  val primaryText = Color(0xFFFFFFFF)
  val secondaryText = Color(0xFFA1A1A6)

  val quickLaunch = remember(apps) {
    listOf(
      listOf("Google Keep", "Keep"),
      listOf("Pocket Casts", "Pocket Cast"),
      listOf("Google Play", "Play Store", "Google Play Store"),
      listOf("Voice Recorder", "Recorder")
    ).mapNotNull { aliases -> resolveDrawerApp(apps, aliases) }
  }

  val categoryNames = remember { listOf("Media", "Social", "Tools", "Productivity", "Finance", "Lifestyle") }

  val categories = remember(apps, searchQuery) {
    categoryNames.mapNotNull { title ->
      val categoryApps = apps
        .filter { app -> classifyDrawerCategory(app) == title }
        .filter { app -> searchQuery.isBlank() || app.label.contains(searchQuery.trim(), ignoreCase = true) }
        .sortedBy { it.label.lowercase() }
      if (categoryApps.isNotEmpty()) DrawerCategory(title, categoryApps) else null
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(backgroundColor.copy(alpha = 0.94f))
      .testTag("app_drawer_container")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(68.dp)
          .testTag("drawer_quick_launch"),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
      ) {
        quickLaunch.take(4).forEach { app ->
          DrawerAppIcon(
            app = app, iconSize = 52.dp,
            onClick = { onAppClick(app) },
            onOpenAppInfo = onOpenAppInfo,
            onTogglePin = onTogglePin,
            onToggleDock = onToggleDock,
            iconPack = iconPack, accentColor = accentColor
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .testTag("drawer_category_scroll"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp)
      ) {
        items((categories.size + 1) / 2) { rowIndex ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            val left = categories[rowIndex * 2]
            DrawerCategoryCard(
              category = left, apps = apps, searchQuery = searchQuery,
              onAppClick = onAppClick, onOpenAppInfo = onOpenAppInfo,
              onTogglePin = onTogglePin, onToggleDock = onToggleDock,
              iconPack = iconPack, accentColor = accentColor,
              primaryText = primaryText, secondaryText = secondaryText
            )

            val rightIndex = rowIndex * 2 + 1
            if (rightIndex < categories.size) {
              val right = categories[rightIndex]
              DrawerCategoryCard(
                category = right, apps = apps, searchQuery = searchQuery,
                onAppClick = onAppClick, onOpenAppInfo = onOpenAppInfo,
                onTogglePin = onTogglePin, onToggleDock = onToggleDock,
                iconPack = iconPack, accentColor = accentColor,
                primaryText = primaryText, secondaryText = secondaryText
              )
            } else {
              Spacer(modifier = Modifier.size(158.dp, 180.dp))
            }
          }
        }


      }
    }

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .fillMaxWidth()
        .height(52.dp)
        .clip(RoundedCornerShape(26.dp))
        .background(searchColor.copy(alpha = 0.96f))
        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(26.dp))
        .padding(horizontal = 12.dp)
        .testTag("drawer_bottom_search"),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search apps",
        tint = secondaryText,
        modifier = Modifier.size(20.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.CenterStart
      ) {
        if (searchQuery.isEmpty()) {
          Text(
            text = "Search apps",
            color = secondaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }

        BasicTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          singleLine = true,
          textStyle = TextStyle(
            color = primaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          ),
          cursorBrush = SolidColor(accentColor),
          modifier = Modifier.fillMaxWidth().testTag("app_search_input")
        )
      }

      if (searchQuery.isNotEmpty()) {
        IconButton(
          onClick = { onSearchChange("") },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear search",
            tint = secondaryText,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun DrawerCategoryCard(
  category: DrawerCategory,
  apps: List<AppItem>,
  searchQuery: String,
  onAppClick: (AppItem) -> Unit,
  onOpenAppInfo: (AppItem) -> Unit,
  onTogglePin: (AppItem) -> Unit,
  onToggleDock: (AppItem) -> Unit,
  iconPack: IconPackStyle,
  accentColor: Color,
  primaryText: Color,
  secondaryText: Color
) {
  val categoryApps = category.apps

  val largeApps = categoryApps.take(2)
  val miniApps = categoryApps.drop(2).take(4)

  Column(
    modifier = Modifier
      .width(158.dp)
      .height(180.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(Color(0xFF1C1C1E).copy(alpha = 0.88f))
      .padding(12.dp)
      .testTag("drawer_category_" + category.title.lowercase())
  ) {
    Text(
      text = category.title,
      color = primaryText,
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 10.dp)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      largeApps.forEach { app ->
        DrawerAppIcon(
          app = app, iconSize = 48.dp,
          onClick = { onAppClick(app) },
          onOpenAppInfo = onOpenAppInfo,
          onTogglePin = onTogglePin,
          onToggleDock = onToggleDock,
          iconPack = iconPack, accentColor = accentColor
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      for (row in 0..1) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          for (column in 0..1) {
            val index = row * 2 + column
            if (index < miniApps.size) {
              val app = miniApps[index]
              DrawerAppIcon(
                app = app, iconSize = 24.dp,
                onClick = { onAppClick(app) },
                onOpenAppInfo = onOpenAppInfo,
                onTogglePin = onTogglePin,
                onToggleDock = onToggleDock,
                iconPack = iconPack, accentColor = accentColor
              )
            } else {
              Spacer(modifier = Modifier.size(24.dp))
            }
          }
        }
      }
    }

  }
}

@Composable
private fun DrawerAppIcon(
  app: AppItem,
  iconSize: Dp,
  onClick: () -> Unit,
  onOpenAppInfo: (AppItem) -> Unit,
  onTogglePin: (AppItem) -> Unit,
  onToggleDock: (AppItem) -> Unit,
  iconPack: IconPackStyle,
  accentColor: Color
) {
  AppIconItem(
    app = app,
    onClick = onClick,
    onOpenAppInfo = onOpenAppInfo,
    onTogglePin = onTogglePin,
    onToggleDock = onToggleDock,
    iconSize = iconSize,
    showLabel = false,
    iconPack = iconPack,
    accentColor = accentColor,
    modifier = Modifier.size(iconSize + 8.dp)
  )
}

private fun classifyDrawerCategory(app: AppItem): String {
  val label = app.label.lowercase()
  val pkg = app.packageName.lowercase()
  fun hasAny(vararg values: String) = values.any { label.contains(it) || pkg.contains(it) }
  return when {
    hasAny("youtube", "spotify", "netflix", "podcast", "pocket cast", "music", "video", "vlc", "anime", "gallery", "photos", "camera") -> "Media"
    hasAny("whatsapp", "instagram", "facebook", "messenger", "reddit", "telegram", "discord", "twitter", "tiktok", "snapchat", "social") -> "Social"
    hasAny("calculator", "clock", "calendar", "chrome", "brave", "recorder", "settings", "files", "file manager", "contacts", "phone", "maps", "google app", "tool", "utility") -> "Tools"
    hasAny("chatgpt", "gemini", "drive", "docs", "document", "scanner", "office", "notion", "keep", "gmail", "outlook", "tasks", "todo", "productivity") -> "Productivity"
    hasAny("gpay", "google pay", "paypal", "bank", "banking", "wallet", "finance", "money", "revolut", "wise", "slice") -> "Finance"
    hasAny("pinterest", "tracker", "health", "fitness", "shopping", "amazon", "ebay", "lifestyle", "weather") -> "Lifestyle"
    else -> "Tools"
  }
}

private fun resolveDrawerApp(
  apps: List<AppItem>,
  aliases: List<String>
): AppItem? {
  return aliases.asSequence()
    .mapNotNull { alias ->
      apps.firstOrNull { app ->
        app.label.equals(alias, ignoreCase = true)
      } ?: apps.firstOrNull { app ->
        app.label.contains(alias, ignoreCase = true)
      }
    }
    .firstOrNull()
}

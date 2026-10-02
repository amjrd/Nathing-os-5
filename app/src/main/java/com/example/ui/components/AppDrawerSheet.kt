package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.blur
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

  val categoryNames = remember { listOf("Media", "Social", "Tools", "Productivity", "Finance", "Lifestyle", "Other") }

  val categories = remember(apps, searchQuery) {
    val query = searchQuery.trim()
    categoryNames.mapNotNull { title ->
      val categoryApps = apps
        .distinctBy { it.packageName }
        .filter { app -> !isSystemSettingsApp(app) }
        .filter { app -> classifyDrawerCategory(app) == title }
        .filter { app -> query.isBlank() || app.label.contains(query, ignoreCase = true) }
        .sortedBy { it.label.lowercase() }
      if (categoryApps.isNotEmpty()) DrawerCategory(title, categoryApps) else null
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("app_drawer_container")
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .blur(24.dp)
        .background(backgroundColor.copy(alpha = 0.82f))
    )
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
              category = left,
              onAppClick = onAppClick, onOpenAppInfo = onOpenAppInfo,
              onTogglePin = onTogglePin, onToggleDock = onToggleDock,
              iconPack = iconPack, accentColor = accentColor,
              primaryText = primaryText, secondaryText = secondaryText
            )

            val rightIndex = rowIndex * 2 + 1
            if (rightIndex < categories.size) {
              val right = categories[rightIndex]
              DrawerCategoryCard(
                category = right,
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

      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier.size(36.dp).testTag("drawer_launcher_settings")
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = "Launcher Settings",
          tint = secondaryText,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
private fun DrawerCategoryCard(
  category: DrawerCategory,
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
  var expanded by remember(category.title) { mutableStateOf(false) }
  val previewApps = categoryApps.take(4)

  Box(
    modifier = Modifier
      .width(158.dp)
      .height(180.dp)
      .zIndex(if (expanded) 10f else 0f)
      .testTag("drawer_category_" + category.title.lowercase())
  ) {
    // Base card stays fixed in place. The expanded panel pops over it instead
    // of pushing the row downward.
    Column(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(24.dp))
        .background(Color(0xFF1C1C1E).copy(alpha = 0.72f))
        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
        .clickable { expanded = !expanded }
        .padding(12.dp)
    ) {
      Text(
        text = category.title,
        color = primaryText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        previewApps.take(2).forEach { app ->
          DrawerAppIcon(
            app = app,
            iconSize = 48.dp,
            onClick = { onAppClick(app) },
            onOpenAppInfo = onOpenAppInfo,
            onTogglePin = onTogglePin,
            onToggleDock = onToggleDock,
            iconPack = iconPack,
            accentColor = accentColor
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        previewApps.drop(2).take(2).forEach { app ->
          DrawerAppIcon(
            app = app,
            iconSize = 40.dp,
            onClick = { onAppClick(app) },
            onOpenAppInfo = onOpenAppInfo,
            onTogglePin = onTogglePin,
            onToggleDock = onToggleDock,
            iconPack = iconPack,
            accentColor = accentColor
          )
        }
      }
    }

    AnimatedVisibility(
      visible = expanded,
      enter = scaleIn(initialScale = 0.72f, animationSpec = tween(220)) ,
      exit = scaleOut(targetScale = 0.72f, animationSpec = tween(180)),
      modifier = Modifier.align(Alignment.Center)
    ) {
      Column(
        modifier = Modifier
          .width(138.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF101012).copy(alpha = 0.94f))
          .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
          .padding(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          categoryApps.take(4).forEach { app ->
            DrawerAppIcon(
              app = app,
              iconSize = 38.dp,
              onClick = { onAppClick(app) },
              onOpenAppInfo = onOpenAppInfo,
              onTogglePin = onTogglePin,
              onToggleDock = onToggleDock,
              iconPack = iconPack,
              accentColor = accentColor
            )
          }
        }

        categoryApps.drop(4).take(4).let { rowApps ->
          if (rowApps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              rowApps.forEach { app ->
                DrawerAppIcon(
                  app = app,
                  iconSize = 32.dp,
                  onClick = { onAppClick(app) },
                  onOpenAppInfo = onOpenAppInfo,
                  onTogglePin = onTogglePin,
                  onToggleDock = onToggleDock,
                  iconPack = iconPack,
                  accentColor = accentColor
                )
              }
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

private fun isSystemSettingsApp(app: AppItem): Boolean {
  val pkg = app.packageName.lowercase()
  return pkg == "com.android.settings" || pkg == "com.google.android.settings"
}

private fun classifyDrawerCategory(app: AppItem): String {
  val label = app.label.lowercase()
  val pkg = app.packageName.lowercase()
  val declared = app.category.lowercase()
  fun hasAny(vararg values: String) = values.any {
    label.contains(it) || pkg.contains(it) || declared.contains(it)
  }
  return when {
    hasAny("youtube", "spotify", "netflix", "podcast", "pocket cast", "music", "video", "vlc", "anime", "gallery", "photos", "camera") -> "Media"
    hasAny("whatsapp", "instagram", "facebook", "messenger", "reddit", "telegram", "discord", "twitter", "tiktok", "snapchat", "threads", "social") -> "Social"
    hasAny("calculator", "clock", "calendar", "chrome", "brave", "browser", "recorder", "files", "file manager", "contacts", "phone", "dialer", "maps", "google app", "tool", "utility", "security") -> "Tools"
    hasAny("chatgpt", "gemini", "drive", "docs", "document", "scanner", "office", "notion", "keep", "gmail", "outlook", "tasks", "todo", "productivity") -> "Productivity"
    hasAny("gpay", "google pay", "paypal", "bank", "banking", "wallet", "finance", "money", "revolut", "wise", "slice") -> "Finance"
    hasAny("pinterest", "tracker", "health", "fitness", "shopping", "amazon", "ebay", "lifestyle", "weather", "food", "travel") -> "Lifestyle"
    else -> "Other"
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

package com.example.ui.components


import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
  drawerCardSizeLevel: Int = 1,
  onToggleThemeMode: () -> Unit = {},
  onSelectIconPack: (IconPackStyle) -> Unit = {},
  onOpenSettings: () -> Unit = {},
) {
  val context = LocalContext.current
  val backgroundColor = Color(0xFF0B0B0C)
  val cardColor = Color(0xFF1C1C1E)
  val searchColor = Color(0xFF2C2C2E)
  val primaryText = Color(0xFFFFFFFF)
  val secondaryText = Color(0xFFA1A1A6)

  val categoryNames = remember { listOf("Social", "Tools", "Photography", "Entertainment", "Shopping", "Games", "Communication") }

  var expandedCategory by remember { mutableStateOf<String?>(null) }
  var drawerMode by remember { mutableStateOf("All") }
  val drawerListState = rememberLazyListState()
  val drawerIconSize = when (iconSizeLevel.coerceIn(0, 3)) {
    0 -> 52.dp
    2 -> 64.dp
    3 -> 70.dp
    else -> 58.dp
  }
  val showDrawerScrollbar by remember { derivedStateOf { drawerListState.layoutInfo.totalItemsCount > 0 && drawerListState.layoutInfo.visibleItemsInfo.size < drawerListState.layoutInfo.totalItemsCount } }

  val allApps = remember(apps, searchQuery) {
    apps.distinctBy { it.packageName }
      .filter { app -> !isSystemSettingsApp(app) }
      .filter { app -> searchQuery.isBlank() || app.label.contains(searchQuery.trim(), ignoreCase = true) }
      .sortedBy { it.label.lowercase() }
  }
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
      .background(backgroundColor)
      .testTag("app_drawer_container")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier.size(42.dp).testTag("drawer_launcher_settings")
        ) {
          Icon(Icons.Default.MoreVert, "Drawer options", tint = primaryText, modifier = Modifier.size(24.dp))
        }
      }

      Spacer(Modifier.height(2.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFF2C2C2E).copy(alpha = 0.92f))
          .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(26.dp))
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Search, "Search apps", tint = Color.White.copy(alpha = 0.78f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          singleLine = true,
          textStyle = TextStyle(color = primaryText, fontSize = 16.sp),
          cursorBrush = SolidColor(accentColor),
          modifier = Modifier.weight(1f).testTag("app_search_input"),
          decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
              if (searchQuery.isEmpty()) {
                Text("Search apps", color = Color.White.copy(alpha = 0.62f), fontSize = 16.sp)
              }
              innerTextField()
            }
          }
        )
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }, modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.Clear, "Clear search", tint = Color.White.copy(alpha = 0.82f), modifier = Modifier.size(20.dp))
          }
        }
      }
      Spacer(Modifier.height(8.dp))

      // Keep both drawer modes available. The original code had the state,
      // but no UI control could ever switch it away from "All".
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        listOf("All", "Categories").forEach { mode ->
          val selected = drawerMode == mode
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxSize()
              .clip(RoundedCornerShape(19.dp))
              .background(if (selected) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.06f))
              .border(1.dp, Color.White.copy(alpha = if (selected) 0.16f else 0.06f), RoundedCornerShape(19.dp))
              .clickable {
                drawerMode = mode
                expandedCategory = null
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = mode,
              color = if (selected) primaryText else secondaryText,
              fontSize = 12.sp,
              fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }

      Spacer(Modifier.height(8.dp))
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("drawer_category_scroll"),
          state = drawerListState,
          verticalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(top = 2.dp, bottom = 96.dp)
        ) {
          if (drawerMode == "Categories") {
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
                  primaryText = primaryText, secondaryText = secondaryText,
                  cardSizeLevel = drawerCardSizeLevel,
                  isExpanded = expandedCategory == left.title,
                  onExpand = { expandedCategory = left.title },
                  modifier = Modifier.weight(1f)
                )

                val rightIndex = rowIndex * 2 + 1
                if (rightIndex < categories.size) {
                  val right = categories[rightIndex]
                  DrawerCategoryCard(
                    category = right,
                    onAppClick = onAppClick, onOpenAppInfo = onOpenAppInfo,
                    onTogglePin = onTogglePin, onToggleDock = onToggleDock,
                    iconPack = iconPack, accentColor = accentColor,
                    primaryText = primaryText, secondaryText = secondaryText,
                    cardSizeLevel = drawerCardSizeLevel,
                    isExpanded = expandedCategory == right.title,
                    onExpand = { expandedCategory = right.title },
                    modifier = Modifier.weight(1f)
                  )
                } else {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          } else {
            items((allApps.size + 3) / 4) { rowIndex ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
              ) {
                val start = rowIndex * 4
                allApps.drop(start).take(4).forEach { app ->
                  DrawerAppIcon(
                    app = app,
                    iconSize = drawerIconSize,
                    onClick = { onAppClick(app) },
                    onOpenAppInfo = onOpenAppInfo,
                    onTogglePin = onTogglePin,
                    onToggleDock = onToggleDock,
                    iconPack = iconPack,
                    accentColor = accentColor,
                    primaryText = primaryText,
                    showLabel = true
                  )
                }
              }
            }
          }
        }

        if (showDrawerScrollbar) {
          DrawerScrollbarIndicator(
            state = drawerListState,
            modifier = Modifier.align(Alignment.CenterEnd)
          )
        }
      }
    }

    if (expandedCategory != null) {
      categories.firstOrNull { it.title == expandedCategory }?.let { expanded ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clickable { expandedCategory = null }
          )
          ExpandedDrawerCategoryPanel(
            category = expanded,
            cardSizeLevel = drawerCardSizeLevel,
            onAppClick = onAppClick,
            onOpenAppInfo = onOpenAppInfo,
            onTogglePin = onTogglePin,
            onToggleDock = onToggleDock,
            iconPack = iconPack,
            accentColor = accentColor,
            primaryText = primaryText,
            secondaryText = secondaryText,
            modifier = Modifier.align(Alignment.Center)
          )
        }
      }
    }
  }
}

@Composable
private fun GlyphMatrixAccent(modifier: Modifier = Modifier) {
  val pattern = arrayOf(
    "11110", "10000", "10000", "10110", "10001", "10001", "11110"
  )
  Canvas(modifier = modifier.size(width = 58.dp, height = 34.dp)) {
    val cols = 5
    val rows = 7
    val cell = minOf(size.width / cols, size.height / rows)
    val dot = cell * 0.30f
    for (y in 0 until rows) {
      for (x in 0 until cols) {
        if (pattern[y][x] == '1') {
          drawCircle(
            color = Color.White.copy(alpha = 0.82f),
            radius = dot / 2f,
            center = androidx.compose.ui.geometry.Offset(
              x * cell + cell / 2f,
              y * cell + cell / 2f
            )
          )
        }
      }
    }
  }
}

@Composable
private fun DrawerScrollbarIndicator(
  state: androidx.compose.foundation.lazy.LazyListState,
  modifier: Modifier = Modifier
) {
  val layout = state.layoutInfo
  val total = layout.totalItemsCount
  val visible = layout.visibleItemsInfo.size
  if (total <= visible || visible == 0) return

  val first = layout.visibleItemsInfo.firstOrNull()?.index ?: 0
  val maxFirst = (total - visible).coerceAtLeast(1)
  val progress = (first.toFloat() / maxFirst).coerceIn(0f, 1f)
  val trackHeight = 96.dp
  val thumbHeight = (trackHeight.value * (visible.toFloat() / total)).coerceIn(18f, trackHeight.value).dp

  Box(
    modifier = modifier
      .padding(end = 3.dp, top = 8.dp, bottom = 104.dp)
      .width(3.dp)
      .height(trackHeight)
      .clip(RoundedCornerShape(2.dp))
      .background(Color.White.copy(alpha = 0.10f))
  ) {
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = ((trackHeight.value - thumbHeight.value) * progress).dp)
        .width(3.dp)
        .height(thumbHeight)
        .clip(RoundedCornerShape(2.dp))
        .background(Color.White.copy(alpha = 0.72f))
    )
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
  secondaryText: Color,
  cardSizeLevel: Int,
  isExpanded: Boolean,
  onExpand: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Keep enough height for two complete rows of 58dp icons.
  // The old card was clipping the second row, making those icons look tiny.
  val cardHeight = when (cardSizeLevel) {
    0 -> 214.dp
    2 -> 238.dp
    else -> 224.dp
  }
  val categoryApps = category.apps.sortedWith(compareByDescending<AppItem> { it.isDock }.thenByDescending { it.isPinned }.thenBy { it.label.lowercase() })

  Box(
    modifier = modifier
      .height(cardHeight)
      .zIndex(if (isExpanded) 10f else 0f)
      .animateContentSize(animationSpec = tween(180))
      .testTag("drawer_category_" + category.title.lowercase())
  ) {
    // Base card stays fixed in place. The expanded panel pops over it instead
    // of pushing the row downward.
    Column(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(24.dp))
        .background(Color(0xFF202023).copy(alpha = 0.70f))
        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
        .clickable { onExpand() }
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Text(
        text = category.title,
        color = primaryText,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
      )
      Text(
        text = categoryApps.size.toString() + " apps",
        color = secondaryText,
        fontSize = 11.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      // Always fill the front of the card with four primary apps.
      // Extra apps stay available inside the expanded card.
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categoryApps.take(4).chunked(2).forEachIndexed { rowIndex, rowApps ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            rowApps.forEach { app ->
              DrawerAppIcon(
                app = app,
                iconSize = 58.dp,
                onClick = { onAppClick(app) },
                onOpenAppInfo = onOpenAppInfo,
                onTogglePin = onTogglePin,
                onToggleDock = onToggleDock,
                iconPack = iconPack,
                accentColor = accentColor
              )
            }
            repeat(2 - rowApps.size) { Spacer(Modifier.width(58.dp)) }
          }
        }
      }
    }

  }
}

@Composable
private fun ExpandedDrawerCategoryPanel(
  category: DrawerCategory,
  cardSizeLevel: Int,
  onAppClick: (AppItem) -> Unit,
  onOpenAppInfo: (AppItem) -> Unit,
  onTogglePin: (AppItem) -> Unit,
  onToggleDock: (AppItem) -> Unit,
  iconPack: IconPackStyle,
  accentColor: Color,
  primaryText: Color,
  secondaryText: Color,
  modifier: Modifier = Modifier
) {
  val innerScrollState = rememberScrollState()
  val panelWidth = if (cardSizeLevel == 2) 304.dp else 292.dp
  val panelHeight = if (cardSizeLevel == 2) 360.dp else 336.dp

  Box(
    modifier = modifier
      .width(panelWidth)
      .height(panelHeight)
      .clip(RoundedCornerShape(28.dp))
      .background(Color(0xFF121214).copy(alpha = 0.97f))
      .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(28.dp))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(innerScrollState)
        .padding(start = 14.dp, top = 14.dp, end = 24.dp, bottom = 14.dp)
    ) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(category.title, color = primaryText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(category.apps.size.toString(), color = secondaryText, fontSize = 13.sp)
      }
      Spacer(Modifier.height(12.dp))
      category.apps.chunked(4).forEach { rowApps ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Top) {
          rowApps.forEach { app ->
            DrawerAppIcon(
              app = app,
              iconSize = 54.dp,
              onClick = { onAppClick(app) },
              onOpenAppInfo = onOpenAppInfo,
              onTogglePin = onTogglePin,
              onToggleDock = onToggleDock,
              iconPack = iconPack,
              accentColor = accentColor,
              primaryText = primaryText,
              showLabel = true
            )
          }
        }
        Spacer(Modifier.height(10.dp))
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
  accentColor: Color,
  primaryText: Color = Color.White,
  showLabel: Boolean = false
) {
  Column(
    modifier = Modifier.width(if (showLabel) 68.dp else iconSize),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
  AppIconItem(
    app = app,
    onClick = onClick,
    onOpenAppInfo = onOpenAppInfo,
    onTogglePin = onTogglePin,
    onToggleDock = onToggleDock,
    iconSize = iconSize,
    showLabel = showLabel,
    iconPack = iconPack,
    accentColor = accentColor,
      modifier = Modifier.size(iconSize)
    )
    if (showLabel) {
      Text(
        text = app.label,
        color = primaryText,
        fontSize = 10.sp,
        maxLines = 1,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        modifier = Modifier.width(68.dp)
      )
    }
  }
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
    hasAny("whatsapp", "instagram", "facebook", "messenger", "reddit", "telegram", "discord", "twitter", "tiktok", "snapchat", "threads", "social") -> "Social"
    hasAny("calculator", "clock", "calendar", "chrome", "brave", "browser", "recorder", "files", "file manager", "contacts", "phone", "dialer", "maps", "google app", "tool", "utility", "security", "settings") -> "Tools"
    hasAny("camera", "photos", "gallery", "lightroom", "picsart", "pinterest", "nomo", "photography", "snapseed") -> "Photography"
    hasAny("youtube", "spotify", "netflix", "podcast", "music", "video", "vlc", "anime", "tiktok", "twitch", "capcut", "stream", "entertainment") -> "Entertainment"
    hasAny("amazon", "ebay", "shein", "temu", "aliexpress", "shopping", "store", "oppo", "market", "shop") -> "Shopping"
    hasAny("game", "gaming", "steam", "ea sports", "fc mobile", "pubg", "free fire", "minecraft", "roblox", "play games") -> "Games"
    hasAny("whatsapp", "messages", "messaging", "sms", "phone", "dialer", "contacts", "gmail", "email", "outlook", "telegram", "messenger", "communication", "mail") -> "Communication"
    else -> "Tools"
  }
}


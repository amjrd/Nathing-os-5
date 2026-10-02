package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite
import kotlinx.coroutines.launch
import java.util.Locale

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
  val context = androidx.compose.ui.platform.LocalContext.current
  val theme = LocalLauncherTheme.current
  val isDark = theme.isDark
  val currentIconSize = when (iconSizeLevel) {
    0 -> 44.dp
    2 -> 60.dp
    3 -> 68.dp
    else -> 56.dp
  }
  var showOverflowMenu by remember { mutableStateOf(false) }
  val gridState = rememberLazyGridState()
  val scope = rememberCoroutineScope()

  val filteredApps by remember(apps, searchQuery) {
    derivedStateOf {
      if (searchQuery.isBlank()) {
        apps
      } else {
        apps.filter { it.label.contains(searchQuery.trim(), ignoreCase = true) }
      }
    }
  }

  // Available Alphabet headers for fast scroll
  val alphabetLetters = remember(apps) {
    apps.mapNotNull { it.label.firstOrNull()?.uppercaseChar() }
      .filter { it in 'A'..'Z' }
      .distinct()
      .sorted()
  }

  val drawerBg = if (theme.isDark) Color(0xF20F0F11) else Color(0xF4F6F7F9)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(drawerBg)
      .testTag("app_drawer_container")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(start = 16.dp, end = 16.dp)
    ) {
      // Top Pull-Down Handle Pill (Authentic Nothing OS Bottom Drawer Handle)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp, bottom = 8.dp)
          .pointerInput(Unit) {
            var pullDownDist = 0f
            detectVerticalDragGestures(
              onDragStart = { pullDownDist = 0f },
              onDragEnd = { pullDownDist = 0f },
              onDragCancel = { pullDownDist = 0f },
              onVerticalDrag = { _, dragAmount ->
                pullDownDist += dragAmount
                // Reduced touch sensitivity (requires deliberate drag > 110f to close)
                if (pullDownDist > 110f) {
                  onClose()
                  pullDownDist = 0f
                }
              }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .width(42.dp)
            .height(4.5.dp)
            .clip(CircleShape)
            .background(theme.textSecondary.copy(alpha = 0.45f))
        )
      }

      // Top Bar: Back button + Search Box + 3-Dot Overflow Menu (Screenshot 1 & 2)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("close_drawer_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back to Home",
            tint = theme.textPrimary
          )
        }

        // Nothing OS Search Pill (Matches Screenshot 1 in Dark & Screenshot 2 in Light)
        Row(
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(theme.surface)
            .border(1.dp, theme.border, RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )

          Spacer(modifier = Modifier.width(10.dp))

          Box(modifier = Modifier.weight(1f)) {
            if (searchQuery.isEmpty()) {
              Text(
                text = "SEARCH APPS...",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = theme.textSecondary,
                letterSpacing = 1.sp
              )
            }
            BasicTextField(
              value = searchQuery,
              onValueChange = onSearchChange,
              textStyle = TextStyle(
                color = theme.textPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
              ),
              cursorBrush = SolidColor(accentColor),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_input")
            )
          }

          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { onSearchChange("") },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = theme.textSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        // 3-Dots Overflow Menu (Matches Screenshot 1 & 2 top-right)
        Box {
          IconButton(
            onClick = { showOverflowMenu = true },
            modifier = Modifier.testTag("drawer_overflow_button")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "More Options",
              tint = theme.textPrimary
            )
          }

          DropdownMenu(
            expanded = showOverflowMenu,
            onDismissRequest = { showOverflowMenu = false },
            modifier = Modifier
              .background(theme.surface)
              .border(1.dp, theme.border, RoundedCornerShape(8.dp))
          ) {
            // Theme Jour / Nuit Toggle
            DropdownMenuItem(
              text = {
                Text(
                  text = if (isDark) "SWITCH TO THEME JOUR (LIGHT)" else "SWITCH TO THEME NUIT (DARK)",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = theme.textPrimary
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                  contentDescription = null,
                  tint = accentColor,
                  modifier = Modifier.size(18.dp)
                )
              },
              onClick = {
                showOverflowMenu = false
                onToggleThemeMode()
              }
            )

            // Icon Pack: Nothing Monochrome
            DropdownMenuItem(
              text = {
                Text(
                  text = "ICON PACK: NOTHING (MONO)",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = if (iconPack == IconPackStyle.MONOCHROME) accentColor else theme.textPrimary
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = if (iconPack == IconPackStyle.MONOCHROME) accentColor else theme.textSecondary,
                  modifier = Modifier.size(18.dp)
                )
              },
              onClick = {
                showOverflowMenu = false
                onSelectIconPack(IconPackStyle.MONOCHROME)
              }
            )

            // Icon Pack: Colour (Scalloped)
            DropdownMenuItem(
              text = {
                Text(
                  text = "ICON PACK: COLOUR (SCALLOPED)",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = if (iconPack == IconPackStyle.COLOUR) accentColor else theme.textPrimary
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = if (iconPack == IconPackStyle.COLOUR) accentColor else theme.textSecondary,
                  modifier = Modifier.size(18.dp)
                )
              },
              onClick = {
                showOverflowMenu = false
                onSelectIconPack(IconPackStyle.COLOUR)
              }
            )

            // Icon Pack: Default (System)
            DropdownMenuItem(
              text = {
                Text(
                  text = "ICON PACK: DEFAULT (SYSTEM)",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = if (iconPack == IconPackStyle.SYSTEM_DEFAULT) accentColor else theme.textPrimary
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = if (iconPack == IconPackStyle.SYSTEM_DEFAULT) accentColor else theme.textSecondary,
                  modifier = Modifier.size(18.dp)
                )
              },
              onClick = {
                showOverflowMenu = false
                onSelectIconPack(IconPackStyle.SYSTEM_DEFAULT)
              }
            )

            // Settings
            DropdownMenuItem(
              text = {
                Text(
                  text = "LAUNCHER SETTINGS",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = theme.textPrimary
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  tint = accentColor,
                  modifier = Modifier.size(18.dp)
                )
              },
              onClick = {
                showOverflowMenu = false
                onOpenSettings()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Pixel-style app drawer: clean app grid with no legacy favorites tray.
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isBlank()) "ALL APPS" else "SEARCH RESULTS",
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.5.sp,
          color = theme.textSecondary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = "${filteredApps.size}",
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          color = theme.textSecondary
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      // Drawer Content: Grid + Fast-Scroll Alphabet Sidebar
      Row(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(5),
          state = gridState,
          verticalArrangement = Arrangement.spacedBy(20.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .testTag("app_drawer_grid")
        ) {
          items(filteredApps, key = { it.packageName }) { app ->
            AppIconItem(
              app = app,
              onClick = { onAppClick(app) },
              onOpenAppInfo = onOpenAppInfo,
              onTogglePin = onTogglePin,
              onToggleDock = onToggleDock,
              iconSize = currentIconSize,
              showLabel = true,
              iconPack = iconPack,
              drawerStyle = true,
              accentColor = accentColor
            )
          }
        }

        // Fast Alphabet Scroller Rail
        if (searchQuery.isEmpty() && alphabetLetters.isNotEmpty()) {
          Column(
            modifier = Modifier
              .width(20.dp)
              .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            alphabetLetters.forEach { letter ->
              Text(
                text = letter.toString(),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = theme.textSecondary,
                modifier = Modifier
                  .clickable {
                    com.example.util.VibrationHelper.vibrateTouch(context)
                    val targetIdx = filteredApps.indexOfFirst {
                      it.label.startsWith(letter, ignoreCase = true)
                    }
                    if (targetIdx >= 0) {
                      scope.launch { gridState.animateScrollToItem(targetIdx) }
                    }
                  }
                  .padding(vertical = 1.dp)
              )
            }
          }
        }
      }
    }
  }
}

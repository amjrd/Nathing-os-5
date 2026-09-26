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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.FolderItem
import com.example.model.IconPackStyle
import com.example.ui.theme.LocalLauncherTheme
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingMatteBlack
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite

/**
 * Signature Nothing OS Enlarged Folder (2x2).
 * Shows up to 4 apps inside directly tappable from the Home screen!
 */
@Composable
fun EnlargedFolderView(
  folder: FolderItem,
  onAppClick: (AppItem) -> Unit,
  onOpenFolderSheet: () -> Unit,
  onToggleEnlarged: () -> Unit,
  modifier: Modifier = Modifier,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed
) {
  val theme = LocalLauncherTheme.current

  if (folder.isEnlarged) {
    // 2x2 Enlarged Folder
    Box(
      modifier = modifier
        .clip(RoundedCornerShape(32.dp))
        .background(theme.surface)
        .border(1.dp, theme.border, RoundedCornerShape(32.dp))
        .padding(14.dp)
        .testTag("enlarged_folder_${folder.id}")
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Folder Header with toggle button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenFolderSheet() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = folder.name,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textSecondary,
            letterSpacing = 1.sp
          )

          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(CircleShape)
              .background(theme.elevated)
              .clickable { onToggleEnlarged() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FullscreenExit,
              contentDescription = "Minimize Folder",
              tint = theme.textPrimary,
              modifier = Modifier.size(12.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2x2 Apps Grid directly launchable
        val displayApps = folder.apps.take(4)
        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            displayApps.getOrNull(0)?.let { app ->
              AppIconItem(
                app = app,
                onClick = { onAppClick(app) },
                iconSize = 44.dp,
                showLabel = false,
                iconPack = iconPack,
                accentColor = accentColor
              )
            }
            displayApps.getOrNull(1)?.let { app ->
              AppIconItem(
                app = app,
                onClick = { onAppClick(app) },
                iconSize = 44.dp,
                showLabel = false,
                iconPack = iconPack,
                accentColor = accentColor
              )
            }
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            displayApps.getOrNull(2)?.let { app ->
              AppIconItem(
                app = app,
                onClick = { onAppClick(app) },
                iconSize = 44.dp,
                showLabel = false,
                iconPack = iconPack,
                accentColor = accentColor
              )
            }
            displayApps.getOrNull(3)?.let { app ->
              AppIconItem(
                app = app,
                onClick = { onAppClick(app) },
                iconSize = 44.dp,
                showLabel = false,
                iconPack = iconPack,
                accentColor = accentColor
              )
            }
          }
        }
      }
    }
  } else {
    // Normal 1x1 Compact Folder
    Column(
      modifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .clickable { onOpenFolderSheet() }
        .padding(6.dp)
        .testTag("compact_folder_${folder.id}"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(theme.surface)
          .border(1.dp, theme.border, CircleShape)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        // Mini 2x2 preview dots
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(theme.textPrimary))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(theme.textSecondary))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(theme.textSecondary))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentColor))
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = folder.name,
        color = theme.textPrimary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
      )
    }
  }
}

/**
 * Expanded Folder Modal Bottom Sheet displaying all apps inside folder.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedFolderSheet(
  folder: FolderItem,
  onDismiss: () -> Unit,
  onAppClick: (AppItem) -> Unit,
  onToggleEnlarged: () -> Unit,
  iconPack: IconPackStyle = IconPackStyle.MONOCHROME,
  accentColor: Color = NothingRed
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val theme = LocalLauncherTheme.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = theme.background,
    contentColor = theme.textPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .testTag("expanded_folder_sheet")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = folder.name,
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            letterSpacing = 2.sp
          )
          Text(
            text = "${folder.apps.size} APPS",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = theme.textSecondary
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(onClick = onToggleEnlarged) {
            Icon(
              imageVector = if (folder.isEnlarged) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
              contentDescription = "Toggle Enlarged",
              tint = theme.textPrimary
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Folder",
              tint = theme.textPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
      ) {
        items(folder.apps, key = { it.packageName }) { app ->
          AppIconItem(
            app = app,
            onClick = {
              onAppClick(app)
              onDismiss()
            },
            iconSize = 54.dp,
            showLabel = true,
            iconPack = iconPack,
            accentColor = accentColor
          )
        }
      }
    }
  }
}

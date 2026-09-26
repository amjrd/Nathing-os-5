package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NothingBorder
import com.example.ui.theme.NothingDarkSurface
import com.example.ui.theme.NothingElevated
import com.example.ui.theme.NothingGrey
import com.example.ui.theme.NothingMatteBlack
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(
  initialNote: String,
  onSave: (String) -> Unit,
  onDismiss: () -> Unit,
  accentColor: Color = NothingRed
) {
  var noteText by remember { mutableStateOf(initialNote) }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .clip(RoundedCornerShape(24.dp))
      .background(NothingMatteBlack)
      .border(1.dp, NothingBorder, RoundedCornerShape(24.dp))
      .padding(20.dp)
  ) {
    Column {
      Text(
        text = "EDIT QUICK MEMO",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = NothingWhite,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = noteText,
        onValueChange = { noteText = it },
        maxLines = 6,
        textStyle = TextStyle(
          color = NothingWhite,
          fontFamily = FontFamily.Monospace,
          fontSize = 13.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = accentColor,
          unfocusedBorderColor = NothingBorder,
          focusedContainerColor = NothingDarkSurface,
          unfocusedContainerColor = NothingDarkSurface
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().height(140.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onDismiss) {
          Text("CANCEL", color = NothingGrey, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        Button(
          onClick = {
            onSave(noteText)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = accentColor),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("SAVE", color = NothingWhite, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

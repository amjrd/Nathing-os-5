package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AppItem
import com.example.model.IconPackStyle
import com.example.model.LauncherSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nothing OS 5", appName)
  }

  @Test
  fun `test launcher settings defaults`() {
    val settings = LauncherSettings()
    assertEquals(IconPackStyle.MONOCHROME, settings.iconPack)
    assertEquals(4, settings.gridColumns)
    assertTrue(settings.showLabels)
  }

  @Test
  fun `test app search filter logic`() {
    val apps = listOf(
      AppItem("com.google.android.dialer", "", "Phone"),
      AppItem("com.google.android.apps.messaging", "", "Messages"),
      AppItem("com.android.chrome", "", "Chrome"),
      AppItem("com.google.android.GoogleCamera", "", "Camera")
    )
    val query = "cam"
    val filtered = apps.filter { it.label.contains(query, ignoreCase = true) }
    assertEquals(1, filtered.size)
    assertEquals("Camera", filtered[0].label)
  }
}

package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.components.NothingClockWidget
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GreetingScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun greeting_display_test() {
    composeTestRule.setContent {
      MyApplicationTheme {
        NothingClockWidget(
          hours = "12",
          minutes = "45",
          date = "WED 23 SEP"
        )
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("clock_widget").assertIsDisplayed()
  }
}

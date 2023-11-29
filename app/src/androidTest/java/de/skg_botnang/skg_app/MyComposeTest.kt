package de.skg_botnang.skg_app

import android.util.Log
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

class MyComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun myTest() {
        // Start the app
//        composeTestRule.setContent {
//            SKGAppTheme {
//                MainScreen(uiState = fakeUiState, /*...*/)
//            }
//        }

        composeTestRule.onNodeWithText("Debug Make Message").performClick()
        composeTestRule.waitForIdle()
        Log.d("SKG-TEST", "Warten ist vorbei")
        val children = composeTestRule.onNodeWithContentDescription("MessageCard").onChildren()
        children[0].assert(hasText("Debug", substring = true))
        children[1].assert(hasText("2023", substring = true))
        children[2].assert(hasText("This message was created by a button click."))
        composeTestRule.onRoot().printToLog("SKG-TEST")
    }
}
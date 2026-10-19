package ca.gbc.comp3074.onofua_phillip.lab4

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.AnnotatedString
import ca.gbc.comp3074.onofua_phillip.lab4.data.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomerAppTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test fun validateAddDeleteAndRecreate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        AppDatabase.getInstance(context).clearAllTables()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            compose.onNodeWithText("No customers in the database.").assertIsDisplayed()
            compose.onNodeWithText("Add Customer").performClick()
            compose.onNodeWithText("Enter a name and a valid age.").assertIsDisplayed()
            add("Alice", "21", true)
            add("Bob", "34", false)
            compose.onNodeWithContentDescription("Delete Alice").performClick()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Alice").fetchSemanticsNodes().isEmpty()
            }
            compose.onNodeWithText("Bob").assertIsDisplayed()
            scenario.recreate()
            compose.waitUntil(10_000) {
                compose.onAllNodesWithText("Bob").fetchSemanticsNodes().size == 1
            }
            compose.onNodeWithText("Inactive customer").assertIsDisplayed()
            add("Charlie", "28", true)
            compose.onNodeWithText("Saved customers (2)").assertIsDisplayed()
        }
    }

    private fun add(name: String, age: String, active: Boolean) {
        compose.onNodeWithTag("name").performSemanticsAction(SemanticsActions.SetText) {
            it(AnnotatedString(name))
        }
        compose.onNodeWithTag("age").performSemanticsAction(SemanticsActions.SetText) {
            it(AnnotatedString(age))
        }
        if (active) compose.onNodeWithTag("active").performClick()
        compose.onNodeWithText("Add Customer").performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText(name, useUnmergedTree = true).fetchSemanticsNodes().size == 1 &&
                compose.onAllNodesWithText("Add Customer").fetchSemanticsNodes().size == 1
        }
    }
}

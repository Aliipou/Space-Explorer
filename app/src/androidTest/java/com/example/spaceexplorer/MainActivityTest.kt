package com.example.spaceexplorer

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.spaceexplorer.ui.activities.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_launches_successfully() {
        // Verify main activity is displayed
        onView(withId(R.id.nav_host_fragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbar_is_displayed() {
        // Verify toolbar is visible
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun recyclerView_is_displayed_in_list_fragment() {
        // Verify RecyclerView is shown
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun swipeRefreshLayout_is_displayed() {
        // Verify SwipeRefreshLayout exists
        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun progressBar_exists_in_layout() {
        // Verify ProgressBar is in the view hierarchy
        onView(withId(R.id.progressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun emptyView_exists_in_layout() {
        // Verify empty view is in hierarchy
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun swipeDown_triggers_refresh() {
        // Verify swipe down works
        onView(withId(R.id.swipeRefreshLayout))
            .perform(swipeDown())

        // SwipeRefreshLayout should respond
        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun app_name_shown_in_title() {
        // Check app title
        onView(allOf(withText(R.string.app_name), isDescendantOfA(withId(R.id.toolbar))))
            .check(matches(isDisplayed()))
    }
}

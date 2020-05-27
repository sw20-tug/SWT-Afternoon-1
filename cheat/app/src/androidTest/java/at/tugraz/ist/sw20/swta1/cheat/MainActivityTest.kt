package at.tugraz.ist.sw20.swta1.cheat

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Test
    fun testComponentsPresent() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { a ->
            assertEquals("Cheat", a.supportActionBar?.title)
        }

        onView(withId(R.id.main)).check(matches(isDisplayed()))
        onView(withId(R.id.title_paired_devices)).check(matches(isDisplayed()))
        onView(withId(R.id.title_nearby_devices)).check(matches(isDisplayed()))
    }


    @Test
    fun testMainToAboutTransition() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.main)).check(matches(isDisplayed()))

        onView(withId(R.id.about_page)).perform(click())

        onView(withId(R.id.about_page_icon)).check(matches(isDisplayed()))

        Espresso.pressBack()
        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }

    @Test
    fun testMainToAboutTransitionWithSupportActionBarBackButton() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.main)).check(matches(isDisplayed()))

        onView(withId(R.id.about_page)).perform(click())

        onView(withId(R.id.about_page_icon)).check(matches(isDisplayed()))

        onView(withContentDescription("Navigate up")).perform(click())

        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }
}
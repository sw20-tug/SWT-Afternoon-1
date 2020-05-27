package at.tugraz.ist.sw20.swta1.cheat

import android.content.res.Resources
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AboutPageActivityTest {

    @Test
    fun testComponentsPresent() {
        var credit1: String = ""
        var credit2: String = ""
        var credit3: String = ""
        var credit4: String = ""

        ActivityScenario.launch(AboutPageActivity::class.java).onActivity { a ->
            credit1 = a.resources.getString(R.string.reference1)
            credit2 = a.resources.getString(R.string.reference2)
            credit3 = a.resources.getString(R.string.reference3)
            credit4 = a.resources.getString(R.string.reference4)
            assertEquals("Cheat", a.supportActionBar?.title);
        }

        onView(withId(R.id.about_page_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.about_version)).check(matches(isDisplayed()))
        onView(withId(R.id.about_credit_1)).check(matches(isDisplayed()))
        onView(withId(R.id.about_credit_2)).check(matches(isDisplayed()))
        onView(withId(R.id.about_credit_3)).check(matches(isDisplayed()))
        onView(withId(R.id.about_credit_4)).check(matches(isDisplayed()))

        onView(withId(R.id.about_version)).check(matches(withText("Version 1.0")))
        onView(withId(R.id.about_credit_1)).check(matches(withText(credit1)))
        onView(withId(R.id.about_credit_2)).check(matches(withText(credit2)))
        onView(withId(R.id.about_credit_3)).check(matches(withText(credit3)))
        onView(withId(R.id.about_credit_4)).check(matches(withText(credit4)))
    }

}
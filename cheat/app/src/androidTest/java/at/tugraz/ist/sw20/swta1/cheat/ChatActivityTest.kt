package at.tugraz.ist.sw20.swta1.cheat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothServiceProvider
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ChatActivityTest {

    @Before
    fun setup() {
        BluetoothServiceProvider.useMock = true
        BluetoothServiceProvider.getBluetoothService().setOnMessageReceive {  }
        BluetoothServiceProvider.getBluetoothService().setOnStateChangeListener { _, _ ->  }
    }



    @Test
    fun testComponentsPresent() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())


        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.connection_status)).check(matches(isDisplayed()))
        onView(withId(R.id.text_entry)).check(matches(isDisplayed()))
        onView(withId(R.id.image_select)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_send)).check(matches(isDisplayed()))

        onView(withId(R.id.title)).check(matches(withText("TestDevice")))
        onView(withId(R.id.connection_status)).check(matches(withText("Connected")))
    }

    @Test
    fun testSendMessage() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))));
    }



    fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder =
                    view.findViewHolderForAdapterPosition(position)
                        ?: // has no item on such position
                        return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

}
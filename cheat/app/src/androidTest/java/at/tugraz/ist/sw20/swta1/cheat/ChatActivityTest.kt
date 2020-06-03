package at.tugraz.ist.sw20.swta1.cheat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothServiceProvider
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
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

        inChatActivity()
    }

    @Test
    fun testSendMessage() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))
    }

    @Test
    fun testEditMessage() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        onView(withId(R.id.chat_history))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withText("Edit")).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("This is a test message!")))
        onView(withId(R.id.text_entry)).perform(replaceText("Is this an edited test message?"))
        onView(withId(R.id.btn_send)).perform(click())

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("Is this an edited test message?")))))
        onView(withId(R.id.text_entry)).check(matches(withText("")))
    }

    @Test
    fun testCancelEditMessage() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        onView(withId(R.id.chat_history))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withText("Edit")).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("This is a test message!")))

        onView(withId(R.id.text_entry)).perform(replaceText("Is this an edited test message?"))
        onView(withId(R.id.btn_cancel_edit)).perform(click())

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))
        onView(withId(R.id.text_entry)).check(matches(withText("")))
    }

    @Test
    fun testDeleteMessage() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        onView(withId(R.id.chat_history))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withText("Delete")).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("Deleted")))))
        onView(withId(R.id.text_entry)).check(matches(withText("")))
    }

    @Test
    fun testDisconnectUIPositive() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        onView(withId(R.id.connection_status)).perform(click())
        onView(withText("YES")).check(matches(isDisplayed())).perform(click())

        inMainActivity()
    }

    @Test
    fun testDisconnectUINegative() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        onView(withId(R.id.connection_status)).perform(click())
        onView(withText("NO")).check(matches(isDisplayed())).perform(click())

        inChatActivity()
    }

    @Test
    fun testDisconnectButtonPositive() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        Espresso.pressBack()
        Espresso.pressBack()
        onView(withText("YES")).check(matches(isDisplayed())).perform(click())

        inMainActivity()
    }

    @Test
    fun testDisconnectButtonNegative() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.list_paired_devices)).perform(click())

        onView(withId(R.id.text_entry)).perform(typeText("This is a test message!"))
        onView(withId(R.id.btn_send)).perform(click())
        onView(withId(R.id.text_entry)).check(matches(withText("")))

        onView(withId(R.id.chat_history))
            .check(matches(atPosition(0, hasDescendant(withText("This is a test message!")))))

        Espresso.pressBack()
        Espresso.pressBack()
        onView(withText("NO")).check(matches(isDisplayed())).perform(click())

        inChatActivity()
    }



    private fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
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

    private fun inMainActivity() {
        onView(withId(R.id.main)).check(matches(isDisplayed()))
        onView(withId(R.id.title_paired_devices)).check(matches(isDisplayed()))
        onView(withId(R.id.title_nearby_devices)).check(matches(isDisplayed()))
    }

    private fun inChatActivity() {
        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.connection_status)).check(matches(isDisplayed()))
        onView(withId(R.id.text_entry)).check(matches(isDisplayed()))
        onView(withId(R.id.image_select)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_send)).check(matches(isDisplayed()))

        onView(withId(R.id.title)).check(matches(withText("TestDevice")))
        onView(withId(R.id.connection_status)).check(matches(withText("Connected")))
    }
}
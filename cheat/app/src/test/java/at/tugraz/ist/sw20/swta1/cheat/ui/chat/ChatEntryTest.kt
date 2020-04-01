package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


internal class ChatEntryTest {

    private val entry: ChatEntry = ChatEntry("Hi this is a message", true, Date())

    @Test
    fun getFormattedTimestamp() {
        var noException = true

        try {
            SimpleDateFormat("HH:mm").parse(entry.getFormattedTimestamp())
        }
        catch (ex: Exception) {
            noException = false
        }

        assertTrue(noException)
    }

    @Test
    fun getMessage() {
        assertEquals("Hi this is a message", entry.getMessage())
    }

    @Test
    fun isWrittenByMe() {
        assertTrue(entry.isWrittenByMe())
    }
}
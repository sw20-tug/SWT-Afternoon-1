package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.*

internal class ChatViewModelTest {

    @Test
    fun testInsertChatEntry() {
        val entry = ChatEntry("Hi, this is a test msg",true, false, Date())

        val viewModel = ChatViewModel()

        val scrollPosition = viewModel.insertMessage(entry)

        assertEquals(0, scrollPosition)
        assertEquals(entry, viewModel.getChatEntries()[0])
    }

    @Test
    fun testEditChatEntry() {
        val entry = ChatEntry("Hi, this is a test msg",true, false, Date())

        val entry2 = ChatEntry("Hi, this is a test msg",true, false, Date(),
            entry.getId())

        val viewModel = ChatViewModel()

        val scrollPosition1 = viewModel.insertMessage(entry)
        val scrollPosition2 = viewModel.insertMessage(entry2)

        assertEquals(0, scrollPosition1)
        assertEquals(-1, scrollPosition2)
        assertEquals(entry2, viewModel.getChatEntries()[0])
        assertEquals(1, viewModel.getChatEntries().size)
    }
}
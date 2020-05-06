package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import androidx.lifecycle.ViewModel
import java.util.ArrayList

class ChatViewModel : ViewModel() {
    private val chatEntries = mutableListOf<ChatEntry>() as ArrayList

    public fun insertMessage(chatEntry: ChatEntry): Int {
        val oldMessageIndex = chatEntries.indexOfFirst { entry -> entry.getId() == chatEntry.getId()}

        return if(oldMessageIndex == -1) {
            chatEntries.add(chatEntry)
            chatEntries.size -1
        } else {
            chatEntries.set(oldMessageIndex, chatEntry)
            -1
        }
    }

    public fun getChatEntries() = chatEntries
}
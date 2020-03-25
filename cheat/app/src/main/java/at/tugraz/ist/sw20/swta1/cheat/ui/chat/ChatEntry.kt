package at.tugraz.ist.sw20.swta1.cheat.ui.chat

class ChatEntry(private val message: String, private val isByMe: Boolean) {
    fun getMessage(): String {
        return message
    }

    fun isWrittenByMe(): Boolean {
        return isByMe
    }
}
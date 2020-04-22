package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ChatEntry(private val message: String, var isByMe: Boolean,
                private val timestamp: Date) : Serializable {

    fun getFormattedTimestamp(): String {
        val df = SimpleDateFormat("HH:mm", Locale.US)
        return df.format(timestamp)
    }

    fun getMessage(): String {
        return message
    }

    fun isWrittenByMe(): Boolean {
        return isByMe
    }
}
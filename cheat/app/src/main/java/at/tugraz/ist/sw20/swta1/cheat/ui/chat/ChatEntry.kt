package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ChatEntry(private val message: String, private val image: ByteArray, var isByMe: Boolean, private var isBySystem: Boolean,
                private val timestamp: Date) : Serializable {

    constructor(message: String, isByMe: Boolean, isBySystem: Boolean, timestamp: Date) :
            this(message, byteArrayOf(), isByMe, isBySystem, timestamp)
    
    fun getFormattedTimestamp(): String {
        val df = SimpleDateFormat("HH:mm", Locale.US)
        return df.format(timestamp)
    }

    fun getMessage(): String {
        return message
    }
    
    fun getImage(): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
    
    fun isImage(): Boolean {
        return image.isNotEmpty()
    }

    fun isWrittenByMe(): Boolean {
        return isByMe
    }

    fun isSystemMessage(): Boolean {
        return isBySystem
    }
}
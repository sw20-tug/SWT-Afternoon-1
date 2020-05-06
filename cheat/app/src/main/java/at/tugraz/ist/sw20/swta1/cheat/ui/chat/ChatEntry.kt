package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import at.tugraz.ist.sw20.swta1.cheat.R
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ChatEntry(private var message: String, private val image: ByteArray, var isByMe: Boolean, var isBySystem: Boolean,
                private val timestamp: Date, private val id : UUID = UUID.randomUUID())
    : Serializable, Cloneable {

    private var deleted = false
    private var editTimestamp: Date? = null

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

    fun getMessageShortened(): String {
        if(message.length > R.dimen.max_edit_message_length + 3) {
            return message.replace("\n", " ").substring(0, R.dimen.max_edit_message_length) + "..."
        }
        return message.replace("\n", " ")
    }

    fun getId(): UUID {
        return id
    }

    fun isWrittenByMe(): Boolean {
        return isByMe
    }

    fun isSystemMessage(): Boolean {
        return isBySystem
    }

    fun setDeleted() {
        deleted = true
        message = "Deleted"
    }

    fun edit(message: String) {
        this.message = message
        editTimestamp = Date()
    }

    fun isDeleted() = deleted

    fun isEdited() = editTimestamp != null

    fun getEditTimestamp() = editTimestamp

    public override fun clone(): Any {
        return super.clone()
    }
}
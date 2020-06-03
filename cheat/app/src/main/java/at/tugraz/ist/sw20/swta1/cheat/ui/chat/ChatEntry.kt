package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import at.tugraz.ist.sw20.swta1.cheat.R
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ChatEntry(private var message: String, private val image: ByteArray, var isByMe: Boolean, var isBySystem: Boolean,
                private val timestamp: Date, private val id : UUID = UUID.randomUUID())
    : Serializable, Cloneable {

    private var deleted = false
    private var editTimestamp: Date? = null

    constructor(message: String, isByMe: Boolean, isBySystem: Boolean, timestamp: Date, id : UUID = UUID.randomUUID()) :
            this(message, byteArrayOf(), isByMe, isBySystem, timestamp, id)
    
    fun getFormattedTimestamp(): String {
        val df = SimpleDateFormat("HH:mm", Locale.US)
        return df.format(timestamp)
    }

    fun getFormattedEditTimestamp(): String {
        val df = SimpleDateFormat("HH:mm", Locale.US)
        return df.format(editTimestamp!!)
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

    fun getMessageShortened(context: Context): String {
        val maxLength = context.resources.getInteger(R.integer.max_edit_message_length)
        if(message.length > maxLength + 3) {
            return message.replace("\n", " ").substring(0, maxLength) + "..."
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

    fun cloneWithNewId(id: UUID = UUID.randomUUID()): ChatEntry {
        return ChatEntry(message, image, isByMe, isBySystem, timestamp, id)
    }
}
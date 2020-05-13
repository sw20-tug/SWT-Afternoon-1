package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.content.Context
import android.graphics.Typeface
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.R

class ChatHistoryAdapter(private val dataSource: ArrayList<ChatEntry>, private val context: Context)
    : RecyclerView.Adapter<ChatHistoryAdapter.ChatViewHolder>() {

    class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatEntry = dataSource[position]
        val tvMessage = holder.view.findViewById<TextView>(R.id.chat_message)
        tvMessage.text = chatEntry.getMessage()
        
        if(chatEntry.isImage()) {
            holder.view.findViewById<ImageView>(R.id.chat_image).setImageBitmap(chatEntry.getImage())
        } else {
            val tvMessage = holder.view.findViewById<TextView>(R.id.chat_message)
            tvMessage.text = chatEntry.getMessage()
            
            if (chatEntry.isDeleted()) {
                tvMessage.setTypeface(null, Typeface.ITALIC)
            } else {
                tvMessage.setTypeface(null, Typeface.NORMAL)
            }
        }
        
        holder.view.findViewById<TextView>(R.id.chat_timestamp).text = chatEntry.getFormattedTimestamp()

        if (!chatEntry.isSystemMessage()) {
            val etEditTimestamp = holder.view.findViewById<TextView>(R.id.chat_edit_timestamp)
            if (chatEntry.isEdited()) {
                etEditTimestamp.text = context.getString(R.string.edit_timestamp,
                    chatEntry.getFormattedEditTimestamp())
                etEditTimestamp.visibility = View.VISIBLE
            }
            else {
                etEditTimestamp.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = dataSource[position]
        var layout = R.layout.item_chat_message_by_other
        if(item.isSystemMessage()) {
            layout = R.layout.item_chat_message_by_system
        } else if(item.isWrittenByMe()) {
            if(item.isImage() && !item.isDeleted())
                layout = R.layout.item_chat_message_by_me_image
            else
                layout = R.layout.item_chat_message_by_me
        } else if(item.isImage() && !item.isDeleted()) {
            layout = R.layout.item_chat_message_by_other_image
        }

        return layout
    }

    fun getItemAt(position: Int) : ChatEntry {
        return dataSource[position]
    }

    override fun getItemCount() = dataSource.size
}
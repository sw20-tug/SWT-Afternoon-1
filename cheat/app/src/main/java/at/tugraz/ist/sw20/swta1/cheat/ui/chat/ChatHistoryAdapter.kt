package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.R

class ChatHistoryAdapter(private val dataSource: ArrayList<ChatEntry>) : RecyclerView.Adapter<ChatHistoryAdapter.ChatViewHolder>() {

    class ChatViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatEntry = dataSource[position]
        val tvMessage = holder.view.findViewById<TextView>(R.id.chat_message)
        tvMessage.text = chatEntry.getMessage()
        if (chatEntry.isDeleted()) {
            tvMessage.setTypeface(null, Typeface.ITALIC);
        }
        holder.view.findViewById<TextView>(R.id.chat_timestamp).text = chatEntry.getFormattedTimestamp()
    }

    override fun getItemViewType(position: Int): Int {
        var layout = R.layout.item_chat_message_by_other
        if(dataSource[position].isSystemMessage())
        {
            layout = R.layout.item_chat_message_by_system
        }
        else if(dataSource[position].isWrittenByMe()) {
            layout = R.layout.item_chat_message_by_me
        }

        return layout
    }

    fun getItemAt(position: Int) : ChatEntry {
        return dataSource[position]
    }

    override fun getItemCount() = dataSource.size
}
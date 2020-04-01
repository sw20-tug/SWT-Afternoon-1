package at.tugraz.ist.sw20.swta1.cheat.ui.chat

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
        holder.view.findViewById<TextView>(R.id.chat_message).text = dataSource[position].getMessage()
        holder.view.findViewById<TextView>(R.id.chat_timestamp).text = dataSource[position].getFormattedTimestamp()
    }

    override fun getItemViewType(position: Int): Int {
        var layout = R.layout.item_chat_message_by_me
        if(!dataSource[position].isWrittenByMe()) {
            layout = R.layout.item_chat_message_by_other
        }
        return layout
    }

    override fun getItemCount() = dataSource.size
}
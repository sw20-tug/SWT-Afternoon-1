package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import at.tugraz.ist.sw20.swta1.cheat.R

class ChatHistoryAdapter(private val context: Context,
                         private val dataSource: ArrayList<ChatEntry>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val entry = getItem(position) as ChatEntry
        val layout = if(entry.isWrittenByMe()) R.layout.item_chat_message_by_me else R.layout.item_chat_message_by_other
        val rowView = inflater.inflate(layout, parent, false)
        val textViewMessage = rowView.findViewById<TextView>(R.id.chat_message)
        textViewMessage.text = entry.getMessage()
        rowView.findViewById<TextView>(R.id.chat_timestamp).text = entry.getFormattedTimestamp()
        return rowView
    }
}
package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.R
import kotlinx.android.synthetic.main.chat_fragment.view.*
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import kotlinx.android.synthetic.main.item_text.*
import java.util.Date

class ChatFragment : Fragment() {
    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel
    private var chatEntries = mutableListOf<ChatEntry>() as ArrayList

    private lateinit var root: View
    private lateinit var chatAdapter: ChatHistoryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        root =  inflater.inflate(R.layout.chat_fragment, container, false)

        val header = root.item_header_text.findViewById<TextView>(R.id.title)
        header.text = BluetoothService.getConnectedDevice()!!.name

        BluetoothService.setOnMessageReceive { chatEntry ->
            chatEntry.isByMe = false
            Log.i("Message", "Message received: ${chatEntry.getMessage()}")
            chatEntries.add(chatEntry)
            activity!!.runOnUiThread {
                chatAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(chatEntries.size - 1)
            }
        }

        chatAdapter = ChatHistoryAdapter(
            chatEntries
        )

        recyclerView = root.findViewById<RecyclerView>(R.id.chat_history).apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = chatAdapter
        }

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        initSendButton()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun initSendButton() {
        val btnSend = root.item_text_entry_field.findViewById<Button>(R.id.btn_send)
        val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)

        btnSend.setOnClickListener {
            val text = etMsg.text.toString().trim()
            if (text.isNotBlank()) {
                val chatEntry = ChatEntry(text, true, Date())
                chatEntries.add(chatEntry)
                BluetoothService.sendMessage(chatEntry)
                chatAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(chatEntries.size - 1)
                etMsg.text.clear()
            }
        }
    }
}
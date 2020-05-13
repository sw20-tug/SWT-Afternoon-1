package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.ChatActivity
import at.tugraz.ist.sw20.swta1.cheat.R
import at.tugraz.ist.sw20.swta1.cheat.RecyclerItemClickListener
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothState
import kotlinx.android.synthetic.main.chat_fragment.view.*
import java.util.*

class ChatFragment : Fragment() {
    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    private lateinit var root: View
    private lateinit var chatAdapter: ChatHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentEditMessage: ChatEntry? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        root =  inflater.inflate(R.layout.chat_fragment, container, false)

        // viewModel.insertMessage(ChatEntry("Hi", true, false, Date()))

        val header = root.item_header_text.findViewById<TextView>(R.id.title)
        header.text = BluetoothService.getConnectedDevice()?.name

        BluetoothService.setOnMessageReceive { chatEntry ->
            chatEntry.isByMe = false
            Log.i("Message", "Message received: ${chatEntry.getMessage()}")
            val scrollPosition = viewModel.insertMessage(chatEntry)
            activity!!.runOnUiThread {
                chatAdapter.notifyDataSetChanged()
                if (scrollPosition > -1) {
                    recyclerView.smoothScrollToPosition(scrollPosition)
                }
            }
        }

        BluetoothService.setOnStateChangeListener { _, newState ->
            val connection_status = root.findViewById<TextView>(R.id.connection_status)

            activity!!.runOnUiThread {
                when (newState) {
                    BluetoothState.CONNECTED -> connection_status.text =
                        getString(R.string.connected_status)
                    BluetoothState.READY -> connection_status.text =
                        getString(R.string.disconnected_status)
                    else -> {
                    }
                }
            }
        }

        chatAdapter = ChatHistoryAdapter(viewModel.getChatEntries(), context!!)

        recyclerView = root.findViewById<RecyclerView>(R.id.chat_history).apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = chatAdapter
        }

        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView,
            object : RecyclerItemClickListener.OnItemClickListener{

            override fun onItemClick(view: View?, position: Int) {
            }

            override fun onLongItemClick(view: View?, position: Int) {
                val message = chatAdapter.getItemAt(position)
                if (message.isByMe && !message.isDeleted()) {
                    val builder = AlertDialog.Builder(activity!!)
                    builder.setTitle("Options")
                        .setItems(R.array.message_options) { _, which ->
                            if (which == 0) {
                                currentEditMessage = message
                                root.item_edit_hint.visibility = View.VISIBLE
                                root.item_edit_hint.findViewById<TextView>(R.id.tv_edit_text).text =
                                    message.getMessageShortened(context!!)
                                val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)
                                etMsg.setText(message.getMessage())
                            } else {
                                deleteChatEntry(message)
                            }
                        }

                    builder.setNegativeButton("Cancel") { _, _ -> }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }))

        root.item_edit_hint.findViewById<Button>(R.id.btn_cancel_edit).setOnClickListener {
            currentEditMessage = null
            root.item_edit_hint.visibility = View.GONE
            val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)
            etMsg.text.clear()
        }

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        initSendButton()
        initConnectionButton()

        return root
    }

    private fun deleteChatEntry(chatEntry: ChatEntry) {
        chatEntry.setDeleted()
        chatAdapter.notifyDataSetChanged()
        BluetoothService.sendMessage(chatEntry)

    }

    private fun initConnectionButton() {
        val connection_status = root.findViewById<TextView>(R.id.connection_status)
        connection_status.setOnClickListener {
            if(BluetoothService.state == BluetoothState.CONNECTED) {
                (activity as ChatActivity).disconnect()
            }
        }
    }

    private fun initSendButton() {
        val btnSend = root.item_text_entry_field.findViewById<Button>(R.id.btn_send)
        val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)

        btnSend.setOnClickListener {
            val text = etMsg.text.toString().trim()
            if (BluetoothService.state != BluetoothState.CONNECTED)
            {
                Toast.makeText(context, "Can't sent message while disconnected.", Toast.LENGTH_SHORT).show()
            }
            else if (text.isNotBlank()) {
                var chatEntry = currentEditMessage
                if(chatEntry != null) {
                    chatEntry.edit(text)
                    currentEditMessage = null
                    root.item_edit_hint.visibility = View.GONE
                } else {
                    chatEntry = ChatEntry(text, true, false, Date())
                }
                val scrollPosition = viewModel.insertMessage(chatEntry)
                BluetoothService.sendMessage(chatEntry)
                chatAdapter.notifyDataSetChanged()
                if(scrollPosition != -1) {
                    recyclerView.smoothScrollToPosition(scrollPosition)
                }
                etMsg.text.clear()
            }
        }
    }
}
package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.R
import kotlinx.android.synthetic.main.chat_fragment.view.*
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

        // TODO Delete these entries once we get real entries from Backend
        chatEntries = arrayListOf(
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date()),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date()),
            ChatEntry(
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                true, Date()
            ),
            ChatEntry(
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                false, Date()
            ),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date()),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date()),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date()),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", true, Date()),
            ChatEntry("Hey", true, Date()),
            ChatEntry("Yo", false, Date())
        )

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
                chatEntries.add(ChatEntry(text, true, Date()))
                chatAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(chatEntries.size - 1)
                etMsg.text.clear()
            }
        }
    }
}
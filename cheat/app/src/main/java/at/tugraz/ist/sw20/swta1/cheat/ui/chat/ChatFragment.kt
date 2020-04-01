package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import at.tugraz.ist.sw20.swta1.cheat.R
import java.util.Date

class ChatFragment : Fragment() {
    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view =  inflater.inflate(R.layout.chat_fragment, container, false)
        val listView = view.findViewById<ListView>(R.id.chat_history)

        // TODO Delete these entries once we get real entries from Backend
        val chatEntries = arrayListOf(
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
        val adapter = ChatHistoryAdapter(
            view.context,
            chatEntries
        )
        listView.adapter = adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
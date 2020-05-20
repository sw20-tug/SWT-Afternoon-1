package at.tugraz.ist.sw20.swta1.cheat

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatEntry
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatFragment
import java.util.*

class ChatActivity : AppCompatActivity() {
    var reconnect = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChatFragment.newInstance())
                .commitNow()
        }

        supportActionBar?.hide()

        val chatEntry = ChatEntry(getString(R.string.partner_connected), true, true, Date())
        BluetoothService.sendMessage(chatEntry)
    }

    override fun onBackPressed() {
        val imageView = (supportFragmentManager.fragments[0] as ChatFragment).view!!
            .findViewById<ImageView>(R.id.chat_history_full_image)
        if (imageView.visibility == View.VISIBLE) {
            (supportFragmentManager.fragments[0] as ChatFragment).hideFullImage()
        } else {
            disconnect()
        }
    }

    fun disconnect()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.disconnect_message_title))
        builder.setMessage(getString(R.string.disconnect_message_message))

        builder.setPositiveButton(getString(R.string.dialog_option_yes)){ dialog, which ->
            val chatEntry = ChatEntry(getString(R.string.partner_disconnected), true, true, Date())
            BluetoothService.sendMessage(chatEntry)
            reconnect = false
            BluetoothService.disconnect()
            goBackToMainActivity()
        }

        builder.setNegativeButton(getString(R.string.dialog_option_no)){ _, _ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun goBackToMainActivity() {
        super.onBackPressed()
    }
}
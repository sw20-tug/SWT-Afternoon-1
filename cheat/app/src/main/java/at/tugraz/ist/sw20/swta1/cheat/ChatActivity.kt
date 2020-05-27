package at.tugraz.ist.sw20.swta1.cheat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothServiceProvider
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
            supportActionBar?.hide()

            val chatEntry = ChatEntry(getString(R.string.partner_connected), true, true, Date())
            BluetoothServiceProvider.getBluetoothService().sendMessage(chatEntry)
        }
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
        val layout = layoutInflater.inflate(R.layout.dialog_exit_conversation, null) as View
        val builder = AlertDialog.Builder(this)
        builder.setView(layout)
        val dialog: AlertDialog = builder.create()
        layout.findViewById<Button>(R.id.disconnect_dialog_yes).setOnClickListener {
            val chatEntry = ChatEntry(getString(R.string.partner_disconnected), true, true, Date())
            BluetoothServiceProvider.getBluetoothService().sendMessage(chatEntry)
            reconnect = false
            BluetoothServiceProvider.getBluetoothService().disconnect()
            goBackToMainActivity()
            dialog.dismiss()
        }
        layout.findViewById<Button>(R.id.disconnect_dialog_no).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun goBackToMainActivity() {
        // do nothing when we receive additional messages
        BluetoothServiceProvider.getBluetoothService().setOnMessageReceive {}
        super.onBackPressed()
    }
}
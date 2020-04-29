package at.tugraz.ist.sw20.swta1.cheat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatEntry
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatFragment
import java.util.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChatFragment.newInstance())
                .commitNow()
        }

        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        disconnect()
    }

    private fun disconnect()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Disconnect")
        builder.setMessage("Do you want to disconnect?")

        builder.setPositiveButton("YES"){dialog, which ->
            val chatEntry = ChatEntry("Chat partner disconnected.", true, true, Date())
            BluetoothService.sendMessage(chatEntry)
            BluetoothService.disconnect()
            super.onBackPressed()
        }

        builder.setNegativeButton("No"){_,_ -> }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
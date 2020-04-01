package at.tugraz.ist.sw20.swta1.cheat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatFragment

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
}
package at.tugraz.ist.sw20.swta1.cheat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import at.tugraz.ist.sw20.swta1.cheat.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        //val intent = Intent(this, ChatActivity::class.java)
        //startActivity(intent)
    }
}

package at.tugraz.ist.sw20.swta1.cheat

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import at.tugraz.ist.sw20.swta1.cheat.ui.about.AboutPageFragment

class AboutPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AboutPageFragment.newInstance())
                .commitNow()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package at.tugraz.ist.sw20.swta1.cheat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
//import at.tugraz.ist.sw20.swta1.cheat.AboutPage
import android.widget.Toast
import at.tugraz.ist.sw20.swta1.cheat.ui.main.MainFragment
import mehdi.sakout.aboutpage.AboutPage

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1 /* <- is for identifying this request, random number */
            )
        }
        
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        
        //val intent = Intent(this, ChatActivity::class.java)
        //startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about_page -> {
                Toast.makeText(this, "Enter about page", Toast.LENGTH_SHORT).show()
                val aboutPage = AboutPage(this).create()
                setContentView(aboutPage)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

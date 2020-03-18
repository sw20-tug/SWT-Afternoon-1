package at.tugraz.ist.sw20.swta1.cheat

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import at.tugraz.ist.sw20.swta1.cheat.ui.main.MainFragment
import android.widget.Toast

class MainActivity : AppCompatActivity() {
  
  private val REQUEST_ENABLE_BLUETOOTH: Int = 1
  private var bluetoothAdapter: BluetoothAdapter? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.container, MainFragment.newInstance())
        .commitNow()
    }
  
    bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
      // Device doesn't support bluetooth
      Toast.makeText(this, "No Bluetooth available", Toast.LENGTH_SHORT).show()
    } else {
      if (!bluetoothAdapter!!.isEnabled) {
        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
      }
    }
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if(requestCode == REQUEST_ENABLE_BLUETOOTH) {
      if (resultCode == Activity.RESULT_OK) {
        if(bluetoothAdapter!!.isEnabled) {
          Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
        } else {
          Toast.makeText(this, "Bluetooth disabled", Toast.LENGTH_SHORT).show()
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Toast.makeText(this, "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show()
      }
    }
  }
}

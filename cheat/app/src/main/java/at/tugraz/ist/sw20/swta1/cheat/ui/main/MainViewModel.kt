package at.tugraz.ist.sw20.swta1.cheat.ui.main

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService

class MainViewModel : ViewModel() {

    lateinit var bluetoothService: BluetoothService

    lateinit var nearbyDevices: MutableLiveData<MutableList<BluetoothDevice>>

    fun getPairedDevices(): List<BluetoothDevice> {
        bluetoothService.getPairedDevices().forEach { device ->
            Log.println(Log.INFO, "Paired device", device.name)
        }
        return bluetoothService.getPairedDevices()
    }

}

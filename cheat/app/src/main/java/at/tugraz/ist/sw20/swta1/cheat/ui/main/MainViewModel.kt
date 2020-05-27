package at.tugraz.ist.sw20.swta1.cheat.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothServiceProvider
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.IBluetoothDevice
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.RealBluetoothDevice

class MainViewModel : ViewModel() {

    lateinit var nearbyDevices: MutableLiveData<MutableList<IBluetoothDevice>>

    fun getPairedDevices(): List<IBluetoothDevice> {
        BluetoothServiceProvider.getBluetoothService().getPairedDevices().forEach { device ->
            Log.println(Log.INFO, "Paired device", device.name)
        }
        return BluetoothServiceProvider.getBluetoothService().getPairedDevices()
    }

}

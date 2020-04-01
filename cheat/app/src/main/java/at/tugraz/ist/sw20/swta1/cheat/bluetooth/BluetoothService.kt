package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BluetoothService(private val adapter: BluetoothAdapter) {
    
    private var receiver : BroadcastReceiver? = null
    
    fun getPairedDevices() : List<RealBluetoothDevice> {
        return adapter.bondedDevices.map { device -> RealBluetoothDevice(device) }.toList()
    }
    
    fun discoverDevices(activity: Activity, onDeviceFound: (BluetoothDevice) -> Unit, onDiscoveryStopped: () -> Unit) {
        if(receiver != null) {
            activity.unregisterReceiver(receiver)
        }
        if(adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }
        
        // Create a BroadcastReceiver for ACTION_FOUND.
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when(action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if(device?.name != null) {
                            Log.println(Log.DEBUG, "Found device", device.name)
                            onDeviceFound(device)
                        }
                    }
                }
            }
        }

        val finishedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.println(Log.DEBUG, "Bluetooth", "Discovery stopped")
                onDiscoveryStopped()
            }
        }
        
        activity.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        activity.registerReceiver(finishedReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        if(!adapter.startDiscovery()) {
            Log.println(Log.ERROR, "Bluetooth", "Error starting discovery")
        }
        Log.println(Log.DEBUG, "Bluetooth", "Start discovery")
    }
    
    fun stopDiscovery(activity: Activity) {
        adapter.cancelDiscovery()
        Log.println(Log.DEBUG, "Bluetooth", "Stop discovery")
        if(receiver != null) {
            activity.unregisterReceiver(receiver)
        }
    }
}
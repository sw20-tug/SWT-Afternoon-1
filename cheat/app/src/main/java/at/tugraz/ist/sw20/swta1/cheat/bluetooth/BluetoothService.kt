package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothService(private val adapter: BluetoothAdapter) {
    private val tag = "BluetoothService"
    private val connectionTag = "$tag/Connection"
    private var receiver : BroadcastReceiver? = null

    val uuid = UUID.randomUUID()
    var state : BluetoothState = BluetoothState.UNAVAILABLE

    fun getPairedDevices() : List<BluetoothDevice> {
        return adapter.bondedDevices.toList()
    }
    
    fun discoverDevices(activity: Activity, onDeviceFound: (BluetoothDevice) -> Unit) {
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
                        val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        Log.println(Log.DEBUG, tag, device.name)
                        onDeviceFound(device)
                    }
                }
            }
        }
        
        activity.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        adapter.startDiscovery()
        state = BluetoothState.DISCOVERING
        Log.println(Log.DEBUG, tag, "Start discovery")
    }
    
    fun stopDiscovery(activity: Activity) {
        adapter.cancelDiscovery()
        state = BluetoothState.READY
        Log.println(Log.DEBUG, tag, "Stop discovery")
        if(receiver != null) {
            activity.unregisterReceiver(receiver)
        }
    }

    fun connectToDevice(activity: Activity, device : BluetoothDevice) {
        stopDiscovery(activity)
        state = BluetoothState.CONNECTING
        val connection = InitConnection(device)
        connection.run()
    }

    inner class InitConnection(private val target: BluetoothDevice) : Thread() {
        private var targetSocket : BluetoothSocket?

        init {
            try {
                targetSocket = target.createRfcommSocketToServiceRecord(uuid)
            } catch (e : IOException) {
                targetSocket = null
                this@BluetoothService.state = BluetoothState.CONNECTING_FAILED
                Log.println(Log.ERROR, connectionTag, "Failed to create socket to " + target.name)
            }
        }

        override fun run() {
            Log.println(Log.INFO, connectionTag, "Beginning connection")
            if(this@BluetoothService.state != BluetoothState.CONNECTING) {
                Log.println(Log.ERROR, connectionTag, "Unexpected state: " + this@BluetoothService.state)
                return
            }
            try {
                targetSocket!!.connect()
            } catch (e: IOException) { // Close the socket
                try {
                    targetSocket!!.close()
                } catch (e2: IOException) {
                    Log.println(Log.ERROR, connectionTag, "Unable to close connection during connection failure")
                }
                this@BluetoothService.state = BluetoothState.CONNECTING_FAILED
                return
            }

        }
    }
}

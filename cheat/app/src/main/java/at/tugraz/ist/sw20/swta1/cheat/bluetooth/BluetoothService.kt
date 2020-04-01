package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService(private val adapter: BluetoothAdapter) {
    private val tag = "BluetoothService"
    private val connectionTag = "$tag/Connection"
    private var receiver: BroadcastReceiver? = null
    private var initConnection: InitConnection? = null
    private var acceptConnection: AcceptConnection? = null
    private var currentConnection: CurrentConnection? = null
    private var onStateChange: (BluetoothState) -> Unit = {}
    private var onMessageReceive: (ByteArray, Int) -> Any = { _: ByteArray, _: Int -> }
    
    val uuid = UUID.randomUUID()
    var state: BluetoothState = BluetoothState.DISABLED
    
    fun getPairedDevices(): List<BluetoothDevice> {
        return adapter.bondedDevices.toList()
    }
    
    fun setOnStateChangeListener(onStateChange: (BluetoothState) -> Unit) {
        this.onStateChange = onStateChange
    }
    
    fun setOnMessageReceive(onMessageReceive: (ByteArray, Int) -> Any) {
        this.onMessageReceive = onMessageReceive
    }
    
    private fun updateState(state: BluetoothState) {
        this.state = state
        onStateChange(state)
    }
    
    fun discoverDevices(activity: Activity, onDeviceFound: (BluetoothDevice) -> Unit): Boolean {
        if (state != BluetoothState.READY) {
            return false
        }
        if (receiver != null) {
            activity.unregisterReceiver(receiver)
        }
        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }
        
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1 /* <- is for identifying this request, random number */
            )
            
            return false
        }
        
        // Create a BroadcastReceiver for ACTION_FOUND.
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        Log.println(Log.DEBUG, tag, device.name)
                        onDeviceFound(device)
                    }
                }
            }
        }
        
        activity.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        adapter.startDiscovery()
        updateState(BluetoothState.READY)
        Log.println(Log.DEBUG, tag, "Start discovery")
        return true
    }
    
    fun stopDiscovery(activity: Activity) {
        adapter.cancelDiscovery()
        updateState(BluetoothState.READY)
        Log.println(Log.DEBUG, tag, "Stop discovery")
        if (receiver != null) {
            activity.unregisterReceiver(receiver)
        }
    }
    
    fun connectToDevice(activity: Activity, device: BluetoothDevice): Boolean {
        stopDiscovery(activity)
        if (state != BluetoothState.READY) {
            return false
        }
        updateState(BluetoothState.CONNECTING)
        synchronized(this) {
            initConnection?.cancel()
            initConnection = InitConnection(device)
            initConnection!!.run()
        }
        return true
    }
    
    fun sendMessage(message: ByteArray) {
        if (state != BluetoothState.CONNECTED || currentConnection == null) {
            return
        }
        
        try {
            currentConnection?.outStream?.write(message)
        } catch (e: IOException) {
            Log.e(connectionTag, "Error sending message", e)
            // TODO disconnect
        }
    }
    
    @Synchronized
    private fun connect(device: BluetoothDevice, socket: BluetoothSocket) {
        if (state != BluetoothState.CONNECTING) {
            return
        }
        if (initConnection != null) {
            initConnection?.cancel();
            initConnection = null;
        }
        
        if (acceptConnection != null) {
            acceptConnection?.cancel();
            acceptConnection = null;
        }
        
        if (currentConnection != null) {
            currentConnection?.cancel();
            currentConnection = null;
        }
        Log.i(connectionTag, "Connecting to " + device.name + "...")
        currentConnection = CurrentConnection(device, socket)
        currentConnection?.run()
    }
    
    inner class InitConnection(private val target: BluetoothDevice) : Thread() {
        private var targetSocket: BluetoothSocket?
        
        init {
            try {
                targetSocket = target.createRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                targetSocket = null
                updateState(BluetoothState.READY)
                Log.e(connectionTag, "Failed to create socket to " + target.name)
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Beginning connection")
            if (this@BluetoothService.state != BluetoothState.READY) {
                Log.e(connectionTag, "Unexpected state: " + this@BluetoothService.state)
                return
            }
            if (targetSocket == null) {
                Log.e(connectionTag, "Failed to create socket to " + target.name)
                return
            }
            try {
                targetSocket!!.connect()
            } catch (e: IOException) { // Close the socket
                try {
                    targetSocket!!.close()
                } catch (e2: IOException) {
                    Log.e(connectionTag, "Unable to close connection during connection failure")
                }
                return
            }
            synchronized(this@BluetoothService) {
                initConnection = null
            }
            updateState(BluetoothState.CONNECTING)
            connect(target, targetSocket!!)
        }
        
        fun cancel() {
            try {
                targetSocket?.close()
            } catch (e: IOException) {
            }
        }
    }
    
    inner class AcceptConnection() : Thread() {
        private var serverSocket: BluetoothServerSocket?
        
        init {
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(adapter.name, uuid)
            } catch (e: IOException) {
                serverSocket = null
                updateState(BluetoothState.READY)
                Log.println(Log.ERROR, connectionTag, "Failed to create server socket")
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Listening for incoming connections")
            if (this@BluetoothService.state != BluetoothState.READY) {
                Log.e(connectionTag, "Unexpected state: " + this@BluetoothService.state)
                return
            }
            if (serverSocket == null) {
                Log.e(connectionTag, "Failed to create server socket")
                return
            }
            var clientSocket: BluetoothSocket? = null
            
            while (this@BluetoothService.state != BluetoothState.CONNECTED) {
                try {
                    clientSocket = serverSocket!!.accept()
                } catch (e: IOException) { // Close the socket
                    Log.e(connectionTag, "AcceptConnection server socket", e)
                    clientSocket = null
                }
                if (clientSocket != null) {
                    var ready = true
                    synchronized(this@BluetoothService) {
                        if (this@BluetoothService.state != BluetoothState.READY) {
                            Log.e(
                                connectionTag,
                                "State was not READY. (State: " + this@BluetoothService.state + ")"
                            )
                            ready = false
                        } else {
                            acceptConnection = null
                            updateState(BluetoothState.CONNECTING)
                        }
                    }
                    if (ready) {
                        connect(clientSocket.remoteDevice, clientSocket)
                    } else {
                        try {
                            clientSocket.close()
                        } catch (e: IOException) {
                        }
                    }
                }
            }
            cancel()
        }
        
        fun cancel() {
            try {
                serverSocket?.close()
            } catch (e: IOException) {
            }
        }
    }
    
    inner class CurrentConnection(
        private val device: BluetoothDevice,
        private val socket: BluetoothSocket
    ) : Thread() {
        var inStream: InputStream?
        var outStream: OutputStream?
        
        init {
            try {
                inStream = socket.inputStream
                outStream = socket.outputStream
                updateState(BluetoothState.CONNECTED)
            } catch (e: IOException) {
                inStream = null
                outStream = null
                Log.e(connectionTag, "Error during steam creation", e)
                currentConnection = null
                cancel()
                updateState(BluetoothState.READY)
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Connection to ${device.name} established")
            val buffer = ByteArray(1024)
            var bytesRead = 0
            while (this@BluetoothService.state == BluetoothState.CONNECTED) {
                try {
                    bytesRead = inStream!!.read(buffer)
                    Log.d(connectionTag, "Message: $buffer")
                    onMessageReceive(buffer, bytesRead)
                } catch (e: IOException) { // Close the socket
                    Log.e(connectionTag, "CurrentConnection error reading message", e)
                    //TODO disconnect
                }
            }
            
            cancel()
        }
        
        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
            }
            try {
                inStream?.close()
            } catch (e: IOException) {
            }
            try {
                outStream?.close()
            } catch (e: IOException) {
            }
        }
    }
    
}

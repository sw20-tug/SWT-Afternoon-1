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
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatEntry
import java.io.*
import java.util.*

object BluetoothService {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val tag = "BluetoothService"
    private val connectionTag = "$tag/Connection"
    private var receiver: BroadcastReceiver? = null
    private var initConnection: InitConnection? = null
    private var acceptConnection: AcceptConnection? = null
    private var currentConnection: CurrentConnection? = null
    private var onStateChange: (BluetoothState, BluetoothState) -> Unit = {_, _ -> }
    private var onMessageReceive: (ChatEntry) -> Any = { chatEntry: ChatEntry -> Log.i(connectionTag, "Received message without listener: ${chatEntry.getMessage()}") }
    
    val uuid = UUID.fromString("871dc78d-b4c1-4bf4-81f1-52af98e32350")
    @Volatile
    var state: BluetoothState = BluetoothState.DISABLED
    
    fun getPairedDevices() : List<RealBluetoothDevice> {
        return adapter.bondedDevices.map { device -> RealBluetoothDevice(device) }.toList()
    }
    
    fun setOnStateChangeListener(onStateChange: (odlState: BluetoothState, newState: BluetoothState) -> Unit) {
        this.onStateChange = onStateChange
    }
    
    fun setOnMessageReceive(onMessageReceive: (ChatEntry) -> Any) {
        this.onMessageReceive = onMessageReceive
    }
    
    private fun updateState(state: BluetoothState) {
        var oldState: BluetoothState
        synchronized(this) {
            if(this.state == state)
                return
            
            oldState = this.state
            this.state = state
        }
        
        Log.i(tag, "State changed from '$oldState' to '$state'")
        onStateChange(oldState, state)
    }
    
    fun setup() {
        updateState(BluetoothState.READY)
        acceptConnection = AcceptConnection()
        acceptConnection!!.start()
    }
    
    fun discoverDevices(activity: Activity, onDeviceFound: (BluetoothDevice) -> Unit, onDiscoveryStopped: () -> Unit) {
        if(state != BluetoothState.READY) {
            return
        }
        if(receiver != null) {
            activity.unregisterReceiver(receiver)
            receiver = null
        }
        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }
        
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1 /* <- is for identifying this request, random number */
            )
            
            return
        }
        
        // Create a BroadcastReceiver for ACTION_FOUND.
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action: String? = intent.action
                when (action) {
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
            Log.println(Log.ERROR, tag, "Error starting discovery")
        }
        Log.println(Log.DEBUG, tag, "Start discovery")
    }
    
    fun stopDiscovery(activity: Activity) {
        adapter.cancelDiscovery()
        updateState(BluetoothState.READY)
        Log.println(Log.DEBUG, tag, "Stop discovery")
        if (receiver != null) {
            activity.unregisterReceiver(receiver)
            receiver = null
        }
    }
    
    fun connectToDevice(activity: Activity, device: IBluetoothDevice): Boolean {
        stopDiscovery(activity)
        synchronized(this) {
            if (state != BluetoothState.READY) {
                return false
            }
        }
        
        updateState(BluetoothState.ATTEMPT_CONNECTION)
        initConnection?.cancel()
        initConnection = InitConnection(device)
        initConnection!!.start()
        return true
    }
    
    fun sendMessage(message: ChatEntry) : Boolean {
        synchronized(this) {
            if (state != BluetoothState.CONNECTED || currentConnection == null) {
                return false
            }
        }
        
        try {
            currentConnection?.objectOutStream?.writeObject(message)
        } catch (e: IOException) {
            Log.e(connectionTag, "Error sending message", e)
            return false
        }
        return true
    }
    
    @Synchronized
    private fun connect(device: IBluetoothDevice, socket: BluetoothSocket) {
        if (state != BluetoothState.CONNECTING) {
            return
        }
        if (initConnection != null) {
            initConnection?.cancel()
            initConnection = null
        }
        
        if (acceptConnection != null) {
            acceptConnection?.cancel()
            acceptConnection = null
        }
        
        if (currentConnection != null) {
            currentConnection?.cancel()
            currentConnection = null
        }
        Log.i(connectionTag, "Connecting to " + device.name + "...")
        currentConnection = CurrentConnection(device, socket)
        currentConnection?.start()
    }
    
    class InitConnection(private val target: IBluetoothDevice) : Thread() {
        private var targetSocket: BluetoothSocket?
        
        init {
            try {
                targetSocket = target.createSocket(uuid)
            } catch (e: IOException) {
                targetSocket = null
                updateState(BluetoothState.READY)
                Log.e(connectionTag, "Failed to create socket to " + target.name)
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Beginning connection")
            synchronized(BluetoothService) {
                if (BluetoothService.state != BluetoothState.ATTEMPT_CONNECTION) {
                    Log.e(connectionTag, "Unexpected state: " + BluetoothService.state)
                    return
                }
            }
            if (targetSocket == null) {
                Log.e(connectionTag, "Failed to create socket to " + target.name)
                return
            }
            try {
                targetSocket!!.connect()
            } catch (e: IOException) { // Close the socket
                updateState(BluetoothState.READY)
                Log.e(connectionTag, "Failed to connect, closing socket")
                try {
                    targetSocket!!.close()
                } catch (e2: IOException) {
                    Log.e(connectionTag, "Unable to close connection during connection failure")
                }
                return
            }
            Log.i(connectionTag, "Socket connection established")
            synchronized(BluetoothService) {
                initConnection = null
            }
            updateState(BluetoothState.CONNECTING)
            connect(target, targetSocket!!)
        }
        
        fun cancel() {
            Log.i(connectionTag, "Cancelling InitConnection")
            try {
                targetSocket?.close()
            } catch (e: IOException) {
            }
        }
    }
    
    class AcceptConnection() : Thread() {
        private var serverSocket: BluetoothServerSocket?
        
        init {
            Log.d(connectionTag, "Starting accepting thread")
            try {
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(adapter.name, uuid)
                Log.d(connectionTag, "Server socket opened")
            } catch (e: IOException) {
                serverSocket = null
                updateState(BluetoothState.READY)
                Log.println(Log.ERROR, connectionTag, "Failed to create server socket")
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Listening for incoming connections")
            synchronized(BluetoothService) {
                if (BluetoothService.state != BluetoothState.READY) {
                    Log.e(connectionTag, "Unexpected state: " + BluetoothService.state)
                    return
                }
            }
            if (serverSocket == null) {
                Log.e(connectionTag, "Failed to create server socket")
                return
            }
            var clientSocket: BluetoothSocket? = null
            
            while (BluetoothService.state == BluetoothState.READY) {
                try {
                    clientSocket = serverSocket!!.accept()
                } catch (e: IOException) { // Close the socket
                    Log.e(connectionTag, "AcceptConnection server socket failed")
                    clientSocket = null
                }
                if (clientSocket != null) {
                    Log.i(connectionTag, "Incoming socket connection established")
                    var ready = true
                    synchronized(BluetoothService) {
                        if (BluetoothService.state != BluetoothState.READY) {
                            ready = false
                        } else {
                            acceptConnection = null
                        }
                    }
                    if (ready) {
                        updateState(BluetoothState.CONNECTING)
                        connect(RealBluetoothDevice(clientSocket.remoteDevice), clientSocket)
                    } else {
                        Log.e(
                            connectionTag,
                            "State was not READY. (State: " + BluetoothService.state + ")"
                        )
                        try {
                            clientSocket.close()
                        } catch (e: IOException) {
                        }
                    }
                } else {
                    Log.e(connectionTag, "Client socket was null")
                }
            }
            cancel()
        }
        
        fun cancel() {
            Log.i(connectionTag, "Cancelling AcceptConnection")
            try {
                serverSocket?.close()
            } catch (e: IOException) {
            }
        }
    }
    
    class CurrentConnection(
        private val device: IBluetoothDevice,
        private val socket: BluetoothSocket
    ) : Thread() {
        var inStream: InputStream?
        var outStream: OutputStream?
        var objectOutStream: ObjectOutputStream?
        
        init {
            try {
                inStream = socket.inputStream
                outStream = socket.outputStream
                objectOutStream = ObjectOutputStream(outStream)
                updateState(BluetoothState.CONNECTED)
            } catch (e: IOException) {
                inStream = null
                outStream = null
                objectOutStream = null
                Log.e(connectionTag, "Error during stream creation", e)
                currentConnection = null
                cancel()
                updateState(BluetoothState.READY)
            }
        }
        
        override fun run() {
            Log.i(connectionTag, "Connection to ${device.name} established, ready to send/receive")
            val buffer = ByteArray(1024)
            var bytesRead = 0
            val inputStream = ObjectInputStream(inStream)
            while (BluetoothService.state == BluetoothState.CONNECTED) {
                try {
                    val chatEntry = inputStream.readObject() as ChatEntry
                    // Log.d(connectionTag, "Message: $buffer")
                    onMessageReceive(chatEntry)
                } catch (e: IOException) { // Close the socket
                    Log.e(connectionTag, "CurrentConnection error reading message", e)
                    updateState(BluetoothState.READY)
                }
            }
            
            cancel()
        }
        
        fun cancel() {
            Log.i(connectionTag, "Cancelling CurrentConnection")
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

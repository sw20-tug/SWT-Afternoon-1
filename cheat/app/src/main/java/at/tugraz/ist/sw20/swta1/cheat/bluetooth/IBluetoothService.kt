package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import at.tugraz.ist.sw20.swta1.cheat.MainActivity
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatEntry
import java.util.*

interface IBluetoothService {
    var activity : MainActivity?
    val uuid: UUID?

    var state: BluetoothState
    fun getPairedDevices() : List<IBluetoothDevice>
    fun setOnStateChangeListener(onStateChange: (odlState: BluetoothState, newState: BluetoothState) -> Unit)
    fun setOnMessageReceive(onMessageReceive: (ChatEntry) -> Any)
    fun isBluetoothEnabled() : Boolean
    fun setDiscoverable(context: Context, timeInSeconds : Int = 300)
    fun setup()
    fun discoverDevices(onDeviceFound: (BluetoothDevice) -> Unit, onDiscoveryStopped: () -> Unit)
    fun stopDiscovery()
    fun connectToDevice(device: IBluetoothDevice): Boolean
    fun sendMessage(message: ChatEntry) : Boolean
    fun disconnect()
    fun getConnectedDevice () : IBluetoothDevice?
}
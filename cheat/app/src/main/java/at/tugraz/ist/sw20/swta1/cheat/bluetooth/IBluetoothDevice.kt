package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.util.*

interface IBluetoothDevice {
    val name : String
    val address: String
    fun createSocket(uuid: UUID) : BluetoothSocket
}

class MockBluetoothDevice(override val name: String, override val address: String) : IBluetoothDevice {
    override fun createSocket(uuid: UUID) : BluetoothSocket {
        throw NotImplementedError("Cannot create socket to a MockDevice - use MockBluetoothService instead of the real one")
    }
}

class RealBluetoothDevice(val device: BluetoothDevice): IBluetoothDevice {
    override val name: String
        get() = device.name
    override val address: String
        get() = device.address
    
    override fun createSocket(uuid: UUID) : BluetoothSocket {
        return device.createRfcommSocketToServiceRecord(uuid)
    }
}
package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.bluetooth.BluetoothDevice

interface InterfaceBluetoothDevice {
    val name : String
    val address: String
}

class MockBluetoothDevice(override val name: String, override val address: String) : InterfaceBluetoothDevice {
}

class RealBluetoothDevice(val device: BluetoothDevice): InterfaceBluetoothDevice {
    override val name: String
        get() = device.name
    override val address: String
        get() = device.address
}
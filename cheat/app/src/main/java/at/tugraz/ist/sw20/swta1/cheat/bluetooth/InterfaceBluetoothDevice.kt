package at.tugraz.ist.sw20.swta1.cheat.bluetooth

interface InterfaceBluetoothDevice {
    var name : String
    var address: String
}

class MockBluetoothDevice(override var name: String, override var address: String) : InterfaceBluetoothDevice {
}
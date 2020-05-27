package at.tugraz.ist.sw20.swta1.cheat.bluetooth

object BluetoothServiceProvider {
    var useMock = false

    fun getBluetoothService(): IBluetoothService {
        return if (useMock) MockBluetoothService else BluetoothService
    }
}
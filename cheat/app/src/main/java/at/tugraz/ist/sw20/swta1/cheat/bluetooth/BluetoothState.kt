package at.tugraz.ist.sw20.swta1.cheat.bluetooth

enum class BluetoothState {
    UNAVAILABLE,
    DISABLED,
    READY,
    DISCOVERING,
    CONNECTING,
    CONNECTING_FAILED,
    CONNECTED
}
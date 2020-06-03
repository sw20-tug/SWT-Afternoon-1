package at.tugraz.ist.sw20.swta1.cheat.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import at.tugraz.ist.sw20.swta1.cheat.MainActivity
import at.tugraz.ist.sw20.swta1.cheat.ui.chat.ChatEntry
import java.util.*

object MockBluetoothService : IBluetoothService {
    override var activity : MainActivity? = null
    override val uuid = UUID.fromString("871dc78d-b4c1-4bf4-81f1-52af98e32350")
    override var state: BluetoothState = BluetoothState.DISABLED


    private var onStateChange: (BluetoothState, BluetoothState) -> Unit = {_, _ -> }
    private var onMessageReceive: (ChatEntry) -> Any = {_ -> }

    private fun updateState(state: BluetoothState) {
        var oldState: BluetoothState
        synchronized(this) {
            if(this.state == state)
                return

            oldState = this.state
            this.state = state
        }

        Log.i("MockBluetooth", "State changed from '$oldState' to '$state'")
        onStateChange(oldState, state)
    }


    override fun getPairedDevices(): List<IBluetoothDevice> {
        return listOf(MockBluetoothDevice("TestDevice", "TestAddress"))
    }

    override fun setOnStateChangeListener(onStateChange: (odlState: BluetoothState, newState: BluetoothState) -> Unit) {
        this.onStateChange = onStateChange
    }

    override fun setOnMessageReceive(onMessageReceive: (ChatEntry) -> Any) {
        this.onMessageReceive = onMessageReceive
    }

    override fun isBluetoothEnabled(): Boolean {
        return true
    }

    override fun setDiscoverable(context: Context, timeInSeconds: Int) {

    }

    override fun setup() {
        updateState(BluetoothState.READY)
    }

    override fun discoverDevices(
        onDeviceFound: (BluetoothDevice) -> Unit,
        onDiscoveryStopped: () -> Unit
    ) {
        // No nearby devices
        onDiscoveryStopped()
    }

    override fun stopDiscovery() {
    }

    override fun connectToDevice(device: IBluetoothDevice): Boolean {
        updateState(BluetoothState.ATTEMPT_CONNECTION)
        updateState(BluetoothState.CONNECTING)
        updateState(BluetoothState.CONNECTED)
        return true
    }

    override fun sendMessage(message: ChatEntry): Boolean {
        if (state != BluetoothState.CONNECTED) {
            Log.w(null, "Tried to send message while not connected")
            return false
        }
        if (!message.isDeleted() && !message.isEdited())
            onMessageReceive(message.cloneWithNewId())
        return true
    }

    override fun disconnect() {
        setup()
    }

    override fun getConnectedDevice(): IBluetoothDevice? {
        return MockBluetoothDevice("TestDevice", "TestAddress")
    }
}
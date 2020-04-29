package at.tugraz.ist.sw20.swta1.cheat.ui.main

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import at.tugraz.ist.sw20.swta1.cheat.ChatActivity
import at.tugraz.ist.sw20.swta1.cheat.MainActivity
import at.tugraz.ist.sw20.swta1.cheat.R
import at.tugraz.ist.sw20.swta1.cheat.RecyclerItemClickListener
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothState
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.IBluetoothDevice
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.RealBluetoothDevice
import at.tugraz.ist.sw20.swta1.cheat.ui.main.adapters.BluetoothDeviceAdapter
import kotlinx.android.synthetic.main.item_title_cell.view.*
import java.util.*
import kotlin.concurrent.schedule


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    lateinit var lvPairedDevices: RecyclerView
    lateinit var lvNearbyDevices: RecyclerView
    lateinit var pullToRefreshContainer: SwipeRefreshLayout

    private val REQUEST_ENABLE_BLUETOOTH: Int = 1
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support bluetooth
            Toast.makeText(activity, "No Bluetooth available", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter!!.isEnabled) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
            }
        }

        val root = inflater.inflate(R.layout.main_fragment, container, false)
        lvPairedDevices = root.findViewById(R.id.list_paired_devices)
        lvNearbyDevices = root.findViewById(R.id.list_nearby_devices)

        val titlePaired: View = root.findViewById(R.id.title_paired_devices)
        val titleNearby: View = root.findViewById(R.id.title_nearby_devices)

        titlePaired.title.text = getString(R.string.paired_devices)
        titleNearby.title.text = getString(R.string.nearby_devices)

        lvPairedDevices.layoutManager = LinearLayoutManager(this.context)
        lvPairedDevices.isNestedScrollingEnabled = false
        lvPairedDevices.setHasFixedSize(true)
        lvNearbyDevices.layoutManager = LinearLayoutManager(this.context)
        lvNearbyDevices.isNestedScrollingEnabled = false
        lvNearbyDevices.setHasFixedSize(true)

        pullToRefreshContainer = root.findViewById<SwipeRefreshLayout>(R.id.pull_to_refresh_container)
        pullToRefreshContainer.setOnRefreshListener {
            viewModel.nearbyDevices.observe(viewLifecycleOwner, Observer { deviceList ->
                viewModel.bluetoothService.discoverDevices(activity!!, { device ->
                    if (deviceList.find { d -> d.address == device.address } == null) {
                        Log.println(Log.INFO, "Found Nearby Device: ", device.name)
                        deviceList.add(RealBluetoothDevice(device))
                        val adapterNearby = BluetoothDeviceAdapter(this.context!!, deviceList)
                        lvNearbyDevices.adapter = adapterNearby
                        lvNearbyDevices.addOnItemTouchListener(RecyclerItemClickListener(context, lvNearbyDevices, object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View?, position: Int) {
                                connectToSelectedDevice(activity!!, adapterNearby.getDeviceAt(position))
                            }
        
                            override fun onLongItemClick(view: View?, position: Int) {}
                        }))
                    }
                }, {
                    pullToRefreshContainer.isRefreshing = false
                })
            })
            
            Timer().schedule(10000) {
                pullToRefreshContainer.isRefreshing = false
            }
        }
        
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
        if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
            showBluetoothDevices()
        }
        viewModel.bluetoothService.setOnStateChangeListener { oldState, newState ->
            if (newState == BluetoothState.CONNECTED) {
                val intent = Intent(activity, ChatActivity::class.java)
                context!!.startActivity(intent)
            } else if(oldState == BluetoothState.CONNECTED && newState == BluetoothState.READY) {
                val intent = Intent(activity, MainActivity::class.java)
                context!!.startActivity(intent)
            }
        }
    }

    private fun showBluetoothDevices() {
        viewModel.bluetoothService = BluetoothService
        viewModel.bluetoothService.setup()
        viewModel.nearbyDevices = MutableLiveData()
        viewModel.nearbyDevices.value = mutableListOf()

        if (this.context!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        viewModel.nearbyDevices.observe(viewLifecycleOwner, Observer { deviceList ->
            viewModel.bluetoothService.discoverDevices(activity!!, { device ->
                if (deviceList.find { d -> d.address == device.address } == null) {
                    Log.println(Log.INFO, "Found Nearby Device: ", device.name)
                    deviceList.add(RealBluetoothDevice(device))
                    val adapterNearby = BluetoothDeviceAdapter(this.context!!, deviceList)
                    lvNearbyDevices.adapter = adapterNearby
                    lvNearbyDevices.addOnItemTouchListener(RecyclerItemClickListener(context, lvNearbyDevices, object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View?, position: Int) {
                            connectToSelectedDevice(activity!!, adapterNearby.getDeviceAt(position))
                        }
        
                        override fun onLongItemClick(view: View?, position: Int) {}
                    }))
                }
            }, {})
        })

        val adapterPaired = BluetoothDeviceAdapter(this.context!!, viewModel.getPairedDevices())
        lvPairedDevices.adapter = adapterPaired
        lvPairedDevices.addOnItemTouchListener(RecyclerItemClickListener(context, lvPairedDevices, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                connectToSelectedDevice(activity!!, adapterPaired.getDeviceAt(position))
            }
        
            override fun onLongItemClick(view: View?, position: Int) {}
        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(activity, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
                    showBluetoothDevices()
                } else {
                    Toast.makeText(activity, "Bluetooth disabled", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity, "Bluetooth enabling cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun connectToSelectedDevice(activity: Activity, device: IBluetoothDevice) {
        pullToRefreshContainer.isRefreshing = false
        
        Log.d("Connecting", "Clicked on device '${device.name}'")
        if(!viewModel.bluetoothService.connectToDevice(activity, device)) {
            Toast.makeText(context, "Connecting to device '${device.name}' failed!",
                Toast.LENGTH_LONG).show()
            Log.d("Connecting", "Connecting to device '${device.name}' failed")
        } else {
            Toast.makeText(context, "Connecting to device '${device.name}' succeeded!",
                Toast.LENGTH_LONG).show()
            Log.d("Connecting", "Connecting to device '${device.name}' succeeded")
        }
    }
}

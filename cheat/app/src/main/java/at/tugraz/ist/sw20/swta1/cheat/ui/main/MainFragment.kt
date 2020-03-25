package at.tugraz.ist.sw20.swta1.cheat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import at.tugraz.ist.sw20.swta1.cheat.R
import at.tugraz.ist.sw20.swta1.cheat.ui.main.adapters.BluetoothDeviceAdapter
import kotlinx.android.synthetic.main.item_title_cell.view.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        val lvPairedDevices: ListView = root.findViewById(R.id.list_paired_devices)
        val lvNearbyDevices: ListView = root.findViewById(R.id.list_nearby_devices)
        // TODO lvPairedDevices.onItemClickListener
        val titlePaired: View = root.findViewById(R.id.title_paired_devices)
        val titleNearby: View = root.findViewById(R.id.title_nearby_devices)

        titlePaired.title.text = getString(R.string.paired_devices)
        titleNearby.title.text = getString(R.string.nearby_devices)

        val devicesPaired = ArrayList<Any>()
        val adapterPaired = BluetoothDeviceAdapter(this.context!!, devicesPaired)
        lvPairedDevices.adapter = adapterPaired

        val devicesNearby = ArrayList<Any>()
        val adapterNearby = BluetoothDeviceAdapter(this.context!!, devicesNearby)
        lvPairedDevices.adapter = adapterNearby

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

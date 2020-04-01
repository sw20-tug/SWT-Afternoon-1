package at.tugraz.ist.sw20.swta1.cheat.ui.main.adapters

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.R
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.InterfaceBluetoothDevice
import kotlinx.android.synthetic.main.item_text.view.*

class BluetoothDeviceAdapter(private val ctx: Context, private val devices: List<InterfaceBluetoothDevice>) :
        RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.device_name
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_text, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = devices.get(position).name
    }
}
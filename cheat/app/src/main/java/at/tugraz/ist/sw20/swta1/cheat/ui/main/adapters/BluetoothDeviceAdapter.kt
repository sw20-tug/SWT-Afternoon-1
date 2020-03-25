package at.tugraz.ist.sw20.swta1.cheat.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import at.tugraz.ist.sw20.swta1.cheat.R

// TODO change to device list
class BluetoothDeviceAdapter(val ctx: Context, val devices: List<Any>) :
        BaseAdapter() {

    private val inflater: LayoutInflater
            = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder {
        lateinit var txtName: TextView
    }

    override fun getItem(position: Int): Any {
        return devices[position]
    }

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_text, parent, false)

            viewHolder = ViewHolder()
            viewHolder.txtName = view.findViewById(R.id.device_name) as TextView

            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        val titleTextView = viewHolder.txtName

        val device = getItem(position) as Any
        titleTextView.text = device.toString() // TODO change to name
        return view
    }
}
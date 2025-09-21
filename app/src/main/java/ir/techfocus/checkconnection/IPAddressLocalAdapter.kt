package ir.techfocus.checkconnection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class IPAddressLocalAdapter(
    context: Context,
    val ipAddressList: List<IPAddressLocal>,
    val onImageClick: (IPAddressLocal) -> Unit
) :
    ArrayAdapter<IPAddressLocal?>(context, 0, ipAddressList) {

    override fun getCount(): Int = ipAddressList.size

    override fun getItem(position: Int): IPAddressLocal = ipAddressList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_selected, parent, false)

        val textView = view.findViewById<TextView>(R.id.text1)

        val currentItem: IPAddressLocal = getItem(position)

        textView.setText("${currentItem.name}  ${currentItem.ipAddress}  ${currentItem.port}")

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item2, parent, false)

        val textView = view.findViewById<TextView>(R.id.text1)
        val imageView = view.findViewById<ImageView>(R.id.image_view)

        val currentItem: IPAddressLocal = getItem(position)

        imageView.setOnClickListener {
            onImageClick(currentItem)
        }
        textView.setText("${currentItem.name}  ${currentItem.ipAddress}  ${currentItem.port}")

        return view
    }
}
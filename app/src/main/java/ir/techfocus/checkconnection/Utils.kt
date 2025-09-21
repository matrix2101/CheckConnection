package ir.techfocus.checkconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import java.io.IOException

class Utils {

    companion object {
        const val CONNECTIVITY_DB: String = "connectivity_db"
    }

    fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting()
    }

    //==============================================================================================

    fun hasNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    //==============================================================================================

    fun splitAddressAndPort(input: String): Array<String> {
        val parts = input.split("  ")
        var partsArray = emptyArray<String>()
        for (i: Int in 0..parts.size - 1) {
            partsArray += parts[i]
        }
        return partsArray
    }

    //==============================================================================================

    fun joinAddressAndPort(array: Array<String>): String {
        var joinString = ""
        for (i: Int in 0..array.size - 1) {
            joinString = "${array[i]}  "
        }
        return joinString.trim()
    }

    //==============================================================================================

    fun remoteSpinnerArrayMaker(items: Items): Array<String> {
        var spinnerArray = emptyArray<String>()
        for (i: Int in 0..(items.items.size - 1)) {
            for (j: Int in 0..(items.items.get(i).ipAddresses.size - 1)) {
                spinnerArray += "${items.items[i].name}  ${items.items[i].ipAddresses[j].ipAddress}  ${items.items[i].ipAddresses[j].port}"
            }
        }
        return spinnerArray
    }

    //==============================================================================================

    fun localSpinnerArrayMaker(items: List<IPAddressLocal>): Array<String> {
        var spinnerArray = emptyArray<String>()
        for (i: Int in 0..(items.size - 1)) {
            spinnerArray += "${items[i].name}  ${items[i].ipAddress}  ${items[i].port}"
        }

        return spinnerArray
    }

    //==============================================================================================

    fun remoteDatabaseSpinnerArrayMaker(ipAddressRemoteList: List<IPAddressRemote>): Array<String> {
        var array = emptyArray<String>()
        for (i: Int in 0..(ipAddressRemoteList.size - 1)) {
            array += "${ipAddressRemoteList[i].name}  ${ipAddressRemoteList[i].ipAddress}  ${ipAddressRemoteList[i].port}"
        }
        return array
    }

    //==============================================================================================

    fun validation(edtIP: EditText, edtPort: EditText): Boolean {
        if (edtIP.text.toString().isNotEmpty() && edtPort.text.toString().isNotEmpty()) {
            return true
        }
        return false
    }

    //==============================================================================================

    fun validateIntInput(editText: EditText): Boolean {
        return if (editText.text.toString().toIntOrNull() != null) true
        else false
    }

    //==============================================================================================

    fun validateLongInput(editText: EditText): Boolean {
        return if (editText.text.toString().toLongOrNull() != null) true
        else false
    }

    //==============================================================================================

    fun getInstanceDB(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, CONNECTIVITY_DB
        ).build()
    }

    //==============================================================================================

    fun convertPersianToEnglishNumbers(input: String): String {
        val persianNumbers = listOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val arabicNumbers = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

        val builder = StringBuilder()

        for (char in input) {
            when {
                char in persianNumbers -> builder.append(persianNumbers.indexOf(char))
                char in arabicNumbers -> builder.append(arabicNumbers.indexOf(char))
                else -> builder.append(char)
            }
        }

        return builder.toString()
    }

    //==============================================================================================

    fun getPredefinedFromAssets(context: Context, fileName: String): Items? {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().readText()
            val gson = Gson()
            return jsonString.let {
                gson.fromJson(it, Items::class.java)
            }

        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }

    //==============================================================================================

    fun alertDialogWarningWithListener(
        context: Context,
        activity: AppCompatActivity,
        title: String?,
        content: String?,
        confirmClickListener: AlertDialogFragment.AlertDialogListener?,
        cancelClickListener: AlertDialogFragment.AlertDialogListener?
    ) {
        try {
            if (!activity.isFinishing && !activity.isDestroyed) {
                val manager = activity.getSupportFragmentManager()
                val alertDialogFragment = AlertDialogFragment(context, Constants.WARNING_TYPE, true)
                alertDialogFragment.setTitleText(title!!)
                alertDialogFragment.setContentText(content!!)
                alertDialogFragment.setConfirmText(context.getResources().getString(R.string.confirm))
                alertDialogFragment.setConfirmClickListener(confirmClickListener)
                alertDialogFragment.setCancelText(context.getResources().getString(R.string.deny))
                alertDialogFragment.setCancelClickListener(cancelClickListener)
                alertDialogFragment.show(manager, alertDialogFragment.getTag())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
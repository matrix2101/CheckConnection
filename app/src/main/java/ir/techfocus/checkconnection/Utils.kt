package ir.techfocus.checkconnection

import android.content.Context
import android.widget.EditText
import androidx.room.Room

class Utils {

    companion object {
        const val CONNECTIVITY_DB: String = "connectivity_db"
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

    fun getInstanceDB (context : Context): AppDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, CONNECTIVITY_DB
        ).build()
    }

}
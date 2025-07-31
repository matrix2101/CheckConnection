package ir.techfocus.checkconnection

import android.content.Context
import com.google.gson.Gson
import java.io.File

class SaveAndLoadRemoteObject {

    companion object {
        const val JSON_FILENAME_REMOTE: String = "predefined_remote.json"
    }

    fun saveObjectToFile(context: Context, obj: Any?) {
        try {
            val gson = Gson()
            val jsonString = gson.toJson(obj)
            val file = File(context.filesDir, JSON_FILENAME_REMOTE)
            file.writeText(jsonString, Charsets.UTF_8)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //==============================================================================================

    inline fun <reified T> loadObjectFromFile(context: Context): T? {
        return try {
            val file = File(context.filesDir, JSON_FILENAME_REMOTE)
            if (!file.exists()) return null
            val jsonString = file.readText(Charsets.UTF_8)
            Gson().fromJson(jsonString, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //==============================================================================================

    suspend inline fun <reified T> getPredefined(context: Context): T? {
        try {
            val response = ApiClient.apiInterface.getPredefined()
            saveObjectToFile(context, response)
            return loadObjectFromFile(context)
        } catch (e: Exception) {
            return loadObjectFromFile(context)
        }
    }
}
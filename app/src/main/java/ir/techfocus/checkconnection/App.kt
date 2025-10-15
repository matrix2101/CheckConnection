package ir.techfocus.checkconnection

import android.app.Application
import kotlinx.coroutines.runBlocking

class App : Application() {
    companion object {
        var appLanguage: String = "fa" // default
    }

    override fun onCreate() {
        super.onCreate()

        val db = Utils().getInstanceDB(this)

        val lang = runBlocking {
            db.dao().getSettingByName(Constants.LANGUAGE)
        }
        appLanguage = lang.value
    }
}
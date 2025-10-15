package ir.techfocus.checkconnection

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = Utils().getSettings(newBase, Constants.LANGUAGE, Constants.LANGUAGE_FA)
        val context = Utils().setLocale(newBase, language)
        super.attachBaseContext(context)
    }
}
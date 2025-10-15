package ir.techfocus.checkconnection

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import ir.techfocus.checkconnection.databinding.ActivitySplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val context: Context = this
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.root)
        db = Utils().getInstanceDB(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            splashScreen.setKeepOnScreenCondition { true }

        } else {
            splashScreen.setKeepOnScreenCondition { false }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            downloadAndSavePredefined {
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }
        }
    }

    //==============================================================================================

    suspend fun downloadAndSavePredefined(onComplete: () -> Unit) {
        if (Utils().checkInternetConnection(context)) {
            try {
                val items: Items? = ApiClient.apiInterface.getPredefined()
                if (items != null) {
                    insertIPAddressRemote(items)
                } else {
                    withContext(Dispatchers.Main) {
                        delay(1000)
                        onComplete()
                    }
                }

                withContext(Dispatchers.Main) {
                    delay(1000)
                    onComplete()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (db.dao().getAllIPAddressesRemote()?.size == 0) {
                    val items: Items? = Utils().getPredefinedFromAssets(context, "predefined.json")
                    if (items != null) {
                        insertIPAddressRemote(items)
                    }
                }
                withContext(Dispatchers.Main) {
                    delay(1000)
                    onComplete()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                delay(1000)
                onComplete()
            }
        }
    }

    //==============================================================================================

    suspend fun insertIPAddressRemote(items: Items) {
        db.dao().deleteAllIPAddressRemote()
        for (i: Int in 0..(items.items.size - 1)) {
            for (j: Int in 0..(items.items.get(i).ipAddresses.size - 1)) {
                val ipAddress = IPAddressRemote(
                    items.items[i].name,
                    items.items[i].ipAddresses[j].ipAddress,
                    items.items[i].ipAddresses[j].port
                )
                db.dao().insertIPAddressRemote(ipAddress)
            }
        }
    }
}
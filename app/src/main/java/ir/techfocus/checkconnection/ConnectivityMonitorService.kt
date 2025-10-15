package ir.techfocus.checkconnection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit


class ConnectivityMonitorService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private val client = OkHttpClient.Builder()
    }

    private lateinit var addressTestArray: Array<AddressTest>
    private var delay: Long = Constants.DEFAULT_DELAY
    private var timeOut: Int = Constants.DEFAULT_TIMEOUT
    private var notifText: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(Constants.NOTIFICATION_ID, buildNotification(resources.getString(R.string.testing)))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
        scope.cancel()
    }

    //==============================================================================================

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        addressTestArray = intent?.getSerializableExtra(Constants.ADDRESS_TEST_KEY) as Array<AddressTest>
        delay = intent.getLongExtra(Constants.DELAY, Constants.DEFAULT_DELAY)
        timeOut = intent.getIntExtra(Constants.TIME_OUT, Constants.DEFAULT_TIMEOUT)

        client.connectTimeout(timeOut.toLong(), TimeUnit.MILLISECONDS)
        client.readTimeout(timeOut.toLong(), TimeUnit.MILLISECONDS)
        client.writeTimeout(timeOut.toLong(), TimeUnit.MILLISECONDS)
        client.build()

        startMonitoring()
        return START_STICKY
    }

    //==============================================================================================

    override fun onBind(intent: Intent?): IBinder? = null

    //==============================================================================================

    private fun startMonitoring() {
        scope.launch {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            while (isActive) {
                notifText = ""
                val httpOk = hasUsableInternet()
                if (addressTestArray[0].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[0]) && httpOk) {
                        notifText += "1✅"
                        addressTestArray[0].reachable = true
                        addressTestArray[0].testSuccess = addressTestArray[0].testSuccess + 1
                    } else {
                        notifText += "1❌"
                        addressTestArray[0].reachable = false
                        addressTestArray[0].testFailed = addressTestArray[0].testFailed + 1
                    }
                }
                if (addressTestArray[1].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[1]) && httpOk) {
                        notifText += "  2✅"
                        addressTestArray[1].reachable = true
                        addressTestArray[1].testSuccess = addressTestArray[1].testSuccess + 1
                    } else {
                        notifText += "  2❌"
                        addressTestArray[1].reachable = false
                        addressTestArray[1].testFailed = addressTestArray[1].testFailed + 1
                    }
                }
                if (addressTestArray[2].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[2]) && httpOk) {
                        notifText += "  3✅"
                        addressTestArray[2].reachable = true
                        addressTestArray[2].testSuccess = addressTestArray[2].testSuccess + 1
                    } else {
                        notifText += "  3❌"
                        addressTestArray[2].reachable = false
                        addressTestArray[2].testFailed = addressTestArray[2].testFailed + 1
                    }
                }

                val updatedNotification = buildNotification(notifText)
                notificationManager.notify(Constants.NOTIFICATION_ID, updatedNotification)

                sendUpdate()
                delay(delay)
            }
        }
    }

    //==============================================================================================

    private fun buildNotification(status: String): Notification {
        return NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    //==============================================================================================

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                resources.getString(R.string.connectivityStatus),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    //==============================================================================================

    fun isIpReachableViaTcp(addressTest: AddressTest): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(addressTest.ip, addressTest.port), timeOut)
                socket.soTimeout = timeOut
                socket.getOutputStream().write(0xFF)
                true
            }

        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    //==============================================================================================

    fun hasUsableInternet(): Boolean {
        return try {
            val request = Request.Builder()
                .url(Constants.clients3Url2)
                .build()

            val response = client.build().newCall(request).execute()
            response.use { it.code == 204 }
        } catch (e: Exception) {
            false
        }
    }

    //==============================================================================================

    private fun sendUpdate() {
        val intent = Intent(Constants.CONNECTIVITY_UPDATE)
        intent.putExtra(Constants.ADDRESS_TEST_KEY, addressTestArray)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
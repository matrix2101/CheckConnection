package ir.techfocus.checkconnection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.techfocus.checkconnection.MainActivity.Companion.ADDRESS_TEST_KEY
import ir.techfocus.checkconnection.MainActivity.Companion.CONNECTIVITY_UPDATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


class ConnectivityMonitorService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val CHANNEL_ID = "connectivity_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var addressTestArray: Array<AddressTest>
    private var delay: Long = 1500
    private var timeOut: Int = 1300
    private var notifText: String = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("⏳ Checking..."))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        scope.cancel()
    }

    //==============================================================================================

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        addressTestArray = intent?.getSerializableExtra(ADDRESS_TEST_KEY) as Array<AddressTest>
        delay = intent.getLongExtra(MainActivity.DELAY, 1500)
        timeOut = intent.getIntExtra(MainActivity.TIME_OUT, 1300)
        startMonitoring()
        return START_STICKY
    }

    //==============================================================================================

    override fun onBind(intent: Intent?): IBinder? = null

    //==============================================================================================

    private fun startMonitoring() {
        scope.launch {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            while (isActive) {

                notifText = ""
                if (addressTestArray[0].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[0])) {
                        notifText += "1✅"
                        addressTestArray[0].reachable = true
                    } else {
                        notifText += "1❌"
                        addressTestArray[0].reachable = false
                    }
                }
                if (addressTestArray[1].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[1])) {
                        notifText += "  2✅"
                        addressTestArray[1].reachable = true
                    } else {
                        notifText += "  2❌"
                        addressTestArray[1].reachable = false
                    }
                }
                if (addressTestArray[2].shouldBeTested == true) {
                    if (isIpReachableViaTcp(addressTestArray[2])) {
                        notifText += "  3✅"
                        addressTestArray[2].reachable = true
                    } else {
                        notifText += "  3❌"
                        addressTestArray[2].reachable = false
                    }
                }

                val updatedNotification = buildNotification(notifText)
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)

                sendUpdate()
                delay(delay)
            }
        }
    }

    //==============================================================================================

    private fun buildNotification(status: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("IP Connectivity Monitor")
            .setContentText(status)
            .setSmallIcon(R.drawable.icon_check)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    //==============================================================================================

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "IP Connectivity Status",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    //==============================================================================================

    fun isIpReachableViaTcp(addressTest: AddressTest): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(addressTest.ip, addressTest.port), timeOut)
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

    private fun sendUpdate() {
        val intent = Intent(CONNECTIVITY_UPDATE)
        intent.putExtra(ADDRESS_TEST_KEY, addressTestArray)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}
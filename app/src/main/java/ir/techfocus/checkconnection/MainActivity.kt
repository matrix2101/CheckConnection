package ir.techfocus.checkconnection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.techfocus.checkconnection.databinding.ActivityMainBinding


class MainActivity : ComponentActivity() {

    lateinit var binding: ActivityMainBinding

    companion object {
        const val CONNECTIVITY_UPDATE: String = "ir.techfocus.CONNECTIVITY_UPDATE"
        const val ADDRESS_TEST_KEY: String = "addressTest"
        const val DELAY: String = "delay"
        const val TIME_OUT: String = "timeOut"
        const val DEFAULT_IP: String = "127.0.0.1"
        const val DEFAULT_PORT: Int = 443
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot())

        binding.btnStart.setOnClickListener {

            var addressTestArray = emptyArray<AddressTest>()

            var delay: Long = 1500
            if (validateLongInput(binding.edtDelay)) {
                delay = binding.edtDelay.text.toString().toLong()
            }
            val timeOut = delay - 200

            if (validation2(binding.edtIP1, binding.edtPort1) && validateIntInput(binding.edtPort1)) {
                val addressTest1 = AddressTest(
                    binding.edtIP1.text.toString(),
                    binding.edtPort1.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest1
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            if (validation2(binding.edtIP2, binding.edtPort2) && validateIntInput(binding.edtPort2)) {
                val addressTest2 = AddressTest(
                    binding.edtIP2.text.toString(),
                    binding.edtPort2.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest2
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            if (validation2(binding.edtIP3, binding.edtPort3) && validateIntInput(binding.edtPort3)) {
                val addressTest3 = AddressTest(
                    binding.edtIP3.text.toString(),
                    binding.edtPort3.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest3
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            binding.btnStart.isActivated = false
            binding.btnStop.isActivated = true
            startMyService(addressTestArray, delay, timeOut.toInt())
        }

        binding.btnStop.setOnClickListener {
            binding.btnStart.isActivated = true
            binding.btnStop.isActivated = false
            stopMyService()
        }
    }

    //==============================================================================================

    fun validation2(edtIP: EditText, edtPort: EditText): Boolean {
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

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CONNECTIVITY_UPDATE) {
                //val addressTestArray = intent.getSerializableExtra(ADDRESS_TEST_KEY) as Array<AddressTest>

            }
        }
    }

    //==============================================================================================

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(CONNECTIVITY_UPDATE)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMyService()
    }

    //==============================================================================================

    fun startMyService(addressTestArray: Array<AddressTest>, delay: Long, timeOut: Int) {

        val intent = Intent(this, ConnectivityMonitorService::class.java)
        val bundle = Bundle()
        bundle.putSerializable(ADDRESS_TEST_KEY, addressTestArray)
        bundle.putLong(DELAY, delay)
        bundle.putInt(TIME_OUT, timeOut)
        intent.putExtras(bundle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }


        if (!Settings.canDrawOverlays(this)) {
            val intent2 = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivityForResult(intent2, 1234)
        } else {
            startService(Intent(this, OverlayService::class.java))
        }
    }

    //==============================================================================================

    fun stopMyService() {
        val stopIntent = Intent(this, ConnectivityMonitorService::class.java)
        stopService(stopIntent)

        val stopIntent2 = Intent(this, OverlayService::class.java)
        stopService(stopIntent2)
    }
}



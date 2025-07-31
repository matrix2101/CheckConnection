package ir.techfocus.checkconnection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.techfocus.checkconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0


class MainActivity : ComponentActivity() {

    lateinit var binding: ActivityMainBinding
    val context: Context = this
    lateinit var db: AppDatabase
    var isRemoteSpinnerInitialized1: Boolean = false
    var isRemoteSpinnerInitialized2: Boolean = false
    var isRemoteSpinnerInitialized3: Boolean = false
    var isLocalSpinnerInitialized1: Boolean = false
    var isLocalSpinnerInitialized2: Boolean = false
    var isLocalSpinnerInitialized3: Boolean = false

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
        db = Utils().getInstanceDB(context)

        lifecycleScope.launch {
            initButtons()
            initRemoteSpinners()
            initLocalSpinners()
            initEditTextListener()
        }
    }

    //==============================================================================================

    suspend fun initRemoteSpinners() {
        val items = downloadAndSavePredefined()

        if (items != null) {
            val spinnerArray = Utils().remoteSpinnerArrayMaker(items)

            val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(context, android.R.layout.simple_spinner_item, spinnerArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnPreIP1.adapter = adapter
            binding.spnPreIP2.adapter = adapter
            binding.spnPreIP3.adapter = adapter
        }
    }

    //==============================================================================================

    suspend fun downloadAndSavePredefined(): Items? {
        val items: Items? = ApiClient.apiInterface.getPredefined()
        if (items != null) {
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
        return items
    }

    //==============================================================================================

    suspend fun initLocalSpinners() {
        val items = db.dao().getAllIPAddressesLocal()

        if (items != null) {
            var spinnerArray = Utils().localSpinnerArrayMaker(items)
            spinnerArray = spinnerArray.reversedArray()

            val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(context, android.R.layout.simple_spinner_item, spinnerArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spnIP1.adapter = adapter
            binding.spnIP2.adapter = adapter
            binding.spnIP3.adapter = adapter
        }
    }

    //==============================================================================================

    suspend fun addItemLocal(ipAddress: String, port: String) {
        val remote = db.dao().getIPAddressRemoteByIpAddress(ipAddress)
        val local = db.dao().getIPAddressLocalByIpAddress(ipAddress)

        val max = db.dao().getGreatestIdNameLocal().toInt()

        if ((remote == null || remote.ipAddress == "") && (local == null || local.ipAddress == "")) {
            db.dao().insertIPAddressLocal(IPAddressLocal((max + 1).toString(), ipAddress, port.toInt()))
            initLocalSpinners()
        }
    }

    //==============================================================================================

    /*fun showSnackbarMessage() {
        Snackbar.make(binding.layout, resources.getString(R.string.jsonDownloadFailed), Snackbar.LENGTH_LONG)
            .setAction(resources.getString(R.string.tryAgain)) {
                lifecycleScope.launch {
                    initRemoteSpinners()
                }
            }.show()
    }*/

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

    //==============================================================================================

    fun initButtons() {
        binding.btnStart.setOnClickListener {
            var addressTestArray = emptyArray<AddressTest>()

            var delay: Long = 1500
            if (Utils().validateLongInput(binding.edtDelay)) {
                delay = binding.edtDelay.text.toString().toLong()
            }
            val timeOut = delay - 200

            //================================================================

            if (Utils().validation(binding.edtIP1, binding.edtPort1) && Utils().validateIntInput(binding.edtPort1)) {
                val addressTest1 = AddressTest(
                    binding.edtIP1.text.toString(),
                    binding.edtPort1.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest1
                lifecycleScope.launch {
                    addItemLocal(binding.edtIP1.text.toString(), binding.edtPort1.text.toString())
                }
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            //================================================================

            if (Utils().validation(binding.edtIP2, binding.edtPort2) && Utils().validateIntInput(binding.edtPort2)) {
                val addressTest2 = AddressTest(
                    binding.edtIP2.text.toString(),
                    binding.edtPort2.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest2
                lifecycleScope.launch {
                    addItemLocal(binding.edtIP2.text.toString(), binding.edtPort2.text.toString())
                }
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            //================================================================

            if (Utils().validation(binding.edtIP3, binding.edtPort3) && Utils().validateIntInput(binding.edtPort3)) {
                val addressTest3 = AddressTest(
                    binding.edtIP3.text.toString(),
                    binding.edtPort3.text.toString().toInt(),
                    true,
                    false
                )
                addressTestArray += addressTest3
                lifecycleScope.launch {
                    addItemLocal(binding.edtIP3.text.toString(), binding.edtPort3.text.toString())
                }
            } else {
                val addressTest = AddressTest(
                    DEFAULT_IP,
                    DEFAULT_PORT,
                    false,
                    false
                )
                addressTestArray += addressTest
            }

            //================================================================

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

    fun initEditTextListener() {
        binding.spnPreIP1.onItemSelectedListener =
            createSpinnerListener(::isRemoteSpinnerInitialized1, binding.edtIP1, binding.edtPort1)

        binding.spnPreIP2.onItemSelectedListener =
            createSpinnerListener(::isRemoteSpinnerInitialized2, binding.edtIP2, binding.edtPort2)

        binding.spnPreIP3.onItemSelectedListener =
            createSpinnerListener(::isRemoteSpinnerInitialized3, binding.edtIP3, binding.edtPort3)

        binding.spnIP1.onItemSelectedListener =
            createSpinnerListener(::isLocalSpinnerInitialized1, binding.edtIP1, binding.edtPort1)

        binding.spnIP2.onItemSelectedListener =
            createSpinnerListener(::isLocalSpinnerInitialized2, binding.edtIP2, binding.edtPort2)

        binding.spnIP3.onItemSelectedListener =
            createSpinnerListener(::isLocalSpinnerInitialized3, binding.edtIP3, binding.edtPort3)
    }

    //==============================================================================================

    fun createSpinnerListener(
        isInitializedRef: KMutableProperty0<Boolean>,
        editTextIP: EditText,
        editTextPort: EditText
    ): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isInitializedRef.get()) {
                    isInitializedRef.set(true)
                    return
                }

                val text = parent?.getItemAtPosition(position).toString()
                val split = Utils().splitAddressAndPort(text)
                editTextIP.setText(split[1])
                editTextPort.setText(split[2])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}



package ir.techfocus.checkconnection

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import ir.techfocus.checkconnection.CustomSpinner.OnSpinnerEventsListener
import ir.techfocus.checkconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val context: Context = this
    private val activity: Activity = this
    private lateinit var db: AppDatabase
    private var permissionHelper: PermissionHelper? = null
    private var startFlag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Utils().getInstanceDB(context)

        lifecycleScope.launch {
            initButtons()
            initRemoteSpinners()
            initLocalSpinners()
            initCharts()
            initEnglishNumberWatcher()
        }
    }

    //==============================================================================================

    fun initEnglishNumberWatcher() {
        setEnglishNumberWatcher(binding.edtIP1)
        setEnglishNumberWatcher(binding.edtPort1)
        setEnglishNumberWatcher(binding.edtIP2)
        setEnglishNumberWatcher(binding.edtPort2)
        setEnglishNumberWatcher(binding.edtIP3)
        setEnglishNumberWatcher(binding.edtPort3)
        setEnglishNumberWatcher(binding.edtDelay)
    }

    //==============================================================================================

    fun setEnglishNumberWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                val converted = Utils().convertPersianToEnglishNumbers(input)

                if (input != converted) {
                    editText.removeTextChangedListener(this)
                    editText.setText(converted)
                    editText.setSelection(converted.length)
                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    //==============================================================================================

    suspend fun initRemoteSpinners() {
        binding.spnPreIP1.closeDropdown()
        binding.spnPreIP2.closeDropdown()
        binding.spnPreIP3.closeDropdown()

        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(context, R.layout.spinner_item_selected, downloadAndSavePredefined())
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnPreIP1.setSpinnerEventsListener(spinnerEvents(binding.spnPreIP1, binding.edtIP1, binding.edtPort1))
        binding.spnPreIP2.setSpinnerEventsListener(spinnerEvents(binding.spnPreIP2, binding.edtIP2, binding.edtPort2))
        binding.spnPreIP3.setSpinnerEventsListener(spinnerEvents(binding.spnPreIP3, binding.edtIP3, binding.edtPort3))

        binding.spnPreIP1.adapter = adapter
        binding.spnPreIP2.adapter = adapter
        binding.spnPreIP3.adapter = adapter
    }

    //==============================================================================================

    suspend fun initLocalSpinners() {
        val items = db.dao().getAllIPAddressesLocal()

        binding.spnIP1.closeDropdown()
        binding.spnIP2.closeDropdown()
        binding.spnIP3.closeDropdown()

        if (items != null) {
            var items = items.reversed()

            val adapter = IPAddressLocalAdapter(context, items) { clickedItem ->
                lifecycleScope.launch {
                    db.dao().deleteIPAddressLocalByName(clickedItem.name)
                    initLocalSpinners()
                }
            }

            binding.spnIP1.setSpinnerEventsListener(spinnerEvents(binding.spnIP1, binding.edtIP1, binding.edtPort1))
            binding.spnIP2.setSpinnerEventsListener(spinnerEvents(binding.spnIP2, binding.edtIP2, binding.edtPort2))
            binding.spnIP3.setSpinnerEventsListener(spinnerEvents(binding.spnIP3, binding.edtIP3, binding.edtPort3))

            binding.spnIP1.adapter = adapter
            binding.spnIP2.adapter = adapter
            binding.spnIP3.adapter = adapter
        }
    }

    //==============================================================================================

    fun spinnerEvents(spinner: CustomSpinner, edtIP: EditText, edtPort: EditText): OnSpinnerEventsListener {
        return object : OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                spinner.setSelected(true)
            }

            override fun onSpinnerClosed() {
                spinner.setSelected(false)
            }

            override fun onSpinnerItemClick(position: Int) {
                spinner.setSelected(false)
                if (spinner.selectedItem != null) {
                    if (spinner.selectedItem is String) {
                        val selectedItem: String = spinner.selectedItem.toString()
                        val split = Utils().splitAddressAndPort(selectedItem)
                        edtIP.setText(split[1])
                        edtPort.setText(split[2])

                    } else if (spinner.selectedItem is IPAddressLocal) {
                        val selectedItem: IPAddressLocal = spinner.selectedItem as IPAddressLocal
                        edtIP.setText(selectedItem.ipAddress)
                        edtPort.setText(selectedItem.port.toString())
                    }
                }
            }
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

    suspend fun downloadAndSavePredefined(): Array<String> {
        val ipAddressList: List<IPAddressRemote>? = db.dao().getAllIPAddressesRemote()
        if (ipAddressList != null) {
            return Utils().remoteDatabaseSpinnerArrayMaker(ipAddressList)
        }
        return emptyArray<String>()
    }

    //==============================================================================================

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.CONNECTIVITY_UPDATE) {
                val addressTestArray = intent.getSerializableExtra(Constants.ADDRESS_TEST_KEY) as Array<AddressTest>

                setData(binding.chartIP1, addressTestArray[0])
                setData(binding.chartIP2, addressTestArray[1])
                setData(binding.chartIP3, addressTestArray[2])
            }
        }
    }

    //==============================================================================================

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            initEditTexts()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Constants.CONNECTIVITY_UPDATE)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        GlobalScope.launch(Dispatchers.IO) {
            addLastEditTextInfoToDB()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        stopMyService()
    }

    //==============================================================================================

    suspend fun addLastEditTextInfoToDB() {
        db.dao().deleteAllIPAddressLast()

        db.dao().insertIPAddressLast(
            IPAddressLast(
                Constants.IP_ADDRESS_NAME_1,
                binding.edtIP1.text.toString(),
                binding.edtPort1.text.toString()
            )
        )
        db.dao().insertIPAddressLast(
            IPAddressLast(
                Constants.IP_ADDRESS_NAME_2,
                binding.edtIP2.text.toString(),
                binding.edtPort2.text.toString()
            )
        )
        db.dao().insertIPAddressLast(
            IPAddressLast(
                Constants.IP_ADDRESS_NAME_3,
                binding.edtIP3.text.toString(),
                binding.edtPort3.text.toString()
            )
        )
        db.dao().insertIPAddressLast(
            IPAddressLast(
                Constants.DELAY,
                binding.edtDelay.text.toString(),
                binding.edtDelay.text.toString()
            )
        )
    }

    //==============================================================================================

    suspend fun initEditTexts() {
        binding.edtIP1.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_1)?.ipAddress)
        binding.edtIP2.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_2)?.ipAddress)
        binding.edtIP3.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_3)?.ipAddress)
        binding.edtPort1.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_1)?.port)
        binding.edtPort2.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_2)?.port)
        binding.edtPort3.setText(db.dao().getIPAddressLastByName(Constants.IP_ADDRESS_NAME_3)?.port)
        binding.edtDelay.setText(db.dao().getIPAddressLastByName(Constants.DELAY)?.ipAddress)
    }

    //==============================================================================================

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults[0] == -1) {
            showMessage(resources.getString(R.string.notifPermissionDenied))
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
        val startStopClickListener = View.OnClickListener {
            if (startFlag) {

                if (!Settings.canDrawOverlays(context)) {

                    val confirmListener = object : AlertDialogFragment.AlertDialogListener {
                        override fun onClick(alertDialog: AlertDialogFragment?) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${activity.packageName}")
                            )
                            startActivity(intent)
                            alertDialog?.dismissWithAnimation()
                        }
                    }
                    val cancelListener = object : AlertDialogFragment.AlertDialogListener {
                        override fun onClick(alertDialog: AlertDialogFragment?) {
                            alertDialog?.dismissWithAnimation()
                        }
                    }
                    Utils().alertDialogWarningWithListener(
                        context,
                        this,
                        resources.getString(R.string.canDrawOverlaysPermissionTitle),
                        resources.getString(R.string.canDrawOverlaysPermissionContent),
                        confirmListener,
                        cancelListener
                    )

                } else {
                    var addressTestArray = emptyArray<AddressTest>()

                    var delay: Long = Constants.DEFAULT_DELAY
                    if (Utils().validateLongInput(binding.edtDelay)) {
                        delay = binding.edtDelay.text.toString().toLong()
                    }
                    val timeOut = Constants.DEFAULT_TIMEOUT

                    //================================================================

                    if (Utils().validation(binding.edtIP1, binding.edtPort1) && Utils().validateIntInput(binding.edtPort1)) {
                        val addressTest1 = AddressTest(
                            binding.edtIP1.text.toString(),
                            binding.edtPort1.text.toString().toInt(),
                            true,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest1
                        lifecycleScope.launch {
                            addItemLocal(binding.edtIP1.text.toString(), binding.edtPort1.text.toString())
                        }
                    } else {
                        val addressTest = AddressTest(
                            Constants.DEFAULT_IP,
                            Constants.DEFAULT_PORT,
                            false,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest
                    }

                    //================================================================

                    if (Utils().validation(binding.edtIP2, binding.edtPort2) && Utils().validateIntInput(binding.edtPort2)) {
                        val addressTest2 = AddressTest(
                            binding.edtIP2.text.toString(),
                            binding.edtPort2.text.toString().toInt(),
                            true,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest2
                        lifecycleScope.launch {
                            addItemLocal(binding.edtIP2.text.toString(), binding.edtPort2.text.toString())
                        }
                    } else {
                        val addressTest = AddressTest(
                            Constants.DEFAULT_IP,
                            Constants.DEFAULT_PORT,
                            false,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest
                    }

                    //================================================================

                    if (Utils().validation(binding.edtIP3, binding.edtPort3) && Utils().validateIntInput(binding.edtPort3)) {
                        val addressTest3 = AddressTest(
                            binding.edtIP3.text.toString(),
                            binding.edtPort3.text.toString().toInt(),
                            true,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest3
                        lifecycleScope.launch {
                            addItemLocal(binding.edtIP3.text.toString(), binding.edtPort3.text.toString())
                        }
                    } else {
                        val addressTest = AddressTest(
                            Constants.DEFAULT_IP,
                            Constants.DEFAULT_PORT,
                            false,
                            false,
                            0,
                            0
                        )
                        addressTestArray += addressTest
                    }

                    startService(Intent(activity, OverlayService::class.java))


                    permissionHelper = PermissionHelper(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
                    permissionHelper?.requestAll { }

                    val intent = Intent(context, ConnectivityMonitorService::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.ADDRESS_TEST_KEY, addressTestArray)
                    bundle.putLong(Constants.DELAY, delay)
                    bundle.putInt(Constants.TIME_OUT, timeOut)
                    intent.putExtras(bundle)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)

                    } else {
                        startService(intent)
                    }

                    binding.btnStartStop.setImageResource(R.drawable.button_start)
                    binding.btnStartStopIcon.setImageResource(R.drawable.ic_stop)
                    startFlag = false
                }

            } else {
                binding.btnStartStop.setImageResource(R.drawable.button_stop)
                binding.btnStartStopIcon.setImageResource(R.drawable.ic_start)
                stopMyService()

                startFlag = true
            }
        }

        binding.btnStartStop.setOnClickListener(startStopClickListener)
        binding.btnStartStopIcon.setOnClickListener(startStopClickListener)

        initDeleteButton(binding.edtIP1, binding.btnClearIP1)
        initDeleteButton(binding.edtPort1, binding.btnClearPort1)
        initDeleteButton(binding.edtIP2, binding.btnClearIP2)
        initDeleteButton(binding.edtPort2, binding.btnClearPort2)
        initDeleteButton(binding.edtIP3, binding.btnClearIP3)
        initDeleteButton(binding.edtPort3, binding.btnClearPort3)
    }

    //==============================================================================================

    fun initDeleteButton(editText: EditText, imageButton: ImageButton) {
        imageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                editText.setText("")
            }
        })
    }

    //==============================================================================================

    fun initCharts() {
        initChart(binding.chartIP1)
        initChart(binding.chartIP2)
        initChart(binding.chartIP3)
    }

    //==============================================================================================

    fun initChart(pieChart: PieChart) {
        val regularFont = ResourcesCompat.getFont(context, R.font.iran_yekan_regular)

        pieChart.setNoDataTextTypeface(regularFont)
        pieChart.setNoDataText(resources.getString(R.string.noDataText))
        pieChart.setNoDataTextColor(resources.getColor(R.color.c303030, null))

        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(1f, 1f, 1f, 1f)

        pieChart.setDragDecelerationFrictionCoef(0.9f)

        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(100)

        pieChart.setHoleRadius(60f)
        pieChart.setTransparentCircleRadius(50f)

        pieChart.setDrawCenterText(false)

        pieChart.setRotationAngle(0f)

        pieChart.animateY(1500, Easing.EaseInOutCubic)
        pieChart.animateX(1500, Easing.EaseInOutCubic)


        val l: Legend = pieChart.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        l.setOrientation(Legend.LegendOrientation.VERTICAL)
        l.setDrawInside(false)
        l.setXEntrySpace(0f)
        l.setYEntrySpace(4f)
        l.setXOffset(0f)
        l.setYOffset(0f)

        pieChart.setDrawEntryLabels(false)
        pieChart.setEntryLabelTypeface(regularFont)
    }

    //==============================================================================================

    private fun setData(pieChart: PieChart, addressTest: AddressTest) {
        val entries = ArrayList<PieEntry?>()

        entries.add(
            PieEntry(
                addressTest.testSuccess.toFloat(),
                resources.getString(R.string.connectionSuccess),
                R.drawable.icon_check
            )
        )
        entries.add(
            PieEntry(
                addressTest.testFailed.toFloat(),
                resources.getString(R.string.connectionFailed),
                R.drawable.icon_cross
            )
        )

        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(true)

        dataSet.setSliceSpace(3f)
        dataSet.setIconsOffset(MPPointF(0f, 40f))
        dataSet.setSelectionShift(5f)

        val colors = ArrayList<Int?>()
        colors.add(resources.getColor(R.color.success, null))
        colors.add(resources.getColor(R.color.failed, null))

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.setColors(colors)

        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(DefaultValueFormatter(0))
        data.setValueTextSize(12f)
        data.setValueTextColor(resources.getColor(R.color.cE0E0E0, null))
        data.setValueTypeface(ResourcesCompat.getFont(context, R.font.iran_yekan_regular))
        pieChart.setData(data)

        pieChart.highlightValues(null)

        pieChart.invalidate()
    }

    //==============================================================================================

    fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}



package ir.techfocus.checkconnection

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var cardView1: CardView
    private lateinit var cardView2: CardView
    private lateinit var cardView3: CardView
    private lateinit var linearLayout1: LinearLayout
    private lateinit var linearLayout2: LinearLayout
    private lateinit var linearLayout3: LinearLayout
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var overlayParams1: WindowManager.LayoutParams
    private lateinit var overlayParams2: WindowManager.LayoutParams
    private lateinit var overlayParams3: WindowManager.LayoutParams

    private lateinit var localBroadcastManager: LocalBroadcastManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        val filter = IntentFilter(Constants.CONNECTIVITY_UPDATE)
        localBroadcastManager.registerReceiver(receiver, filter)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        //overlayView1 =========================================================================

        cardView1 = initCardView()
        linearLayout1 = initLinearLayout()

        textView1 = initTextView()
        textView1.text = "IP1"

        imageView1 = ImageView(this)
        imageView1.setImageResource(R.drawable.icon_cross) // default

        overlayParams1 = initLayoutParams()

        overlayParams1.gravity = Gravity.TOP or Gravity.START
        overlayParams1.x = 50
        overlayParams1.y = 100

        cardView1.setOnTouchListener(initOnTouchListener(overlayParams1, cardView1))
        cardView1.setCardBackgroundColor(resources.getColor(R.color.IP1BackgroundColor, null))

        linearLayout1.addView(textView1)
        linearLayout1.addView(imageView1)
        cardView1.addView(linearLayout1)

        windowManager.addView(cardView1, overlayParams1)

        //overlayView2 =========================================================================

        cardView2 = initCardView()
        linearLayout2 = initLinearLayout()

        textView2 = initTextView()
        textView2.text = "IP2"

        imageView2 = ImageView(this)
        imageView2.setImageResource(R.drawable.icon_cross) // default

        overlayParams2 = initLayoutParams()

        overlayParams2.gravity = Gravity.TOP or Gravity.START
        overlayParams2.x = 50
        overlayParams2.y = 225

        cardView2.setOnTouchListener(initOnTouchListener(overlayParams2, cardView2))
        cardView2.setCardBackgroundColor(resources.getColor(R.color.IP2BackgroundColor, null))

        linearLayout2.addView(textView2)
        linearLayout2.addView(imageView2)
        cardView2.addView(linearLayout2)

        windowManager.addView(cardView2, overlayParams2)

        //overlayView3 =========================================================================

        cardView3 = initCardView()
        linearLayout3 = initLinearLayout()

        textView3 = initTextView()
        textView3.text = "IP3"

        imageView3 = ImageView(this)
        imageView3.setImageResource(R.drawable.icon_cross) // default

        overlayParams3 = initLayoutParams()

        overlayParams3.gravity = Gravity.TOP or Gravity.START
        overlayParams3.x = 50
        overlayParams3.y = 350

        cardView3.setOnTouchListener(initOnTouchListener(overlayParams3, cardView3))
        cardView3.setCardBackgroundColor(resources.getColor(R.color.IP3BackgroundColor, null))

        linearLayout3.addView(textView3)
        linearLayout3.addView(imageView3)
        cardView3.addView(linearLayout3)

        //======================================================================================

        windowManager.addView(cardView3, overlayParams3)
    }

    //==============================================================================================

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.CONNECTIVITY_UPDATE) {
                val addressTestArray = intent.getSerializableExtra(Constants.ADDRESS_TEST_KEY) as Array<AddressTest>

                if (addressTestArray[0].shouldBeTested == true) {
                    cardView1.isVisible = true
                    imageView1.setImageResource(
                        if (addressTestArray[0].reachable) R.drawable.icon_check else R.drawable.icon_cross
                    )
                } else {
                    cardView1.isVisible = false
                }

                if (addressTestArray[1].shouldBeTested == true) {
                    cardView2.isVisible = true
                    imageView2.setImageResource(
                        if (addressTestArray[1].reachable) R.drawable.icon_check else R.drawable.icon_cross
                    )
                } else {
                    cardView2.isVisible = false
                }

                if (addressTestArray[2].shouldBeTested == true) {
                    cardView3.isVisible = true
                    imageView3.setImageResource(
                        if (addressTestArray[2].reachable) R.drawable.icon_check else R.drawable.icon_cross
                    )
                } else {
                    cardView3.isVisible = false
                }

            }
        }
    }

    //==============================================================================================

    override fun onDestroy() {
        super.onDestroy()

        if (::windowManager.isInitialized && ::cardView1.isInitialized) {
            windowManager.removeView(cardView1)
        }
        if (::windowManager.isInitialized && ::cardView2.isInitialized) {
            windowManager.removeView(cardView2)
        }
        if (::windowManager.isInitialized && ::cardView3.isInitialized) {
            windowManager.removeView(cardView3)
        }

        // Unregister receiver
        if (::localBroadcastManager.isInitialized) {
            localBroadcastManager.unregisterReceiver(receiver)
        }
    }

    //==============================================================================================

    fun initCardView(): CardView {
        return CardView(this).apply {
            radius = 16f
            setCardBackgroundColor("#EEFFFFFF".toColorInt()) // semi-transparent black
            cardElevation = 8f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    //==============================================================================================

    fun initLinearLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 0, 4, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        }
    }

    //==============================================================================================

    fun initOnTouchListener(overlayParams: WindowManager.LayoutParams, cardView: CardView): View.OnTouchListener {
        return object : View.OnTouchListener {
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = overlayParams.x
                        initialY = overlayParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        overlayParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        overlayParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(cardView, overlayParams)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        v.performClick()
                        return true
                    }
                }
                return false
            }
        }
    }

    //==============================================================================================

    fun initLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
    }

    //==============================================================================================

    fun initTextView(): TextView {
        return TextView(this).apply {
            text = "IP"
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.NORMAL)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    //==============================================================================================

    override fun onBind(intent: Intent?): IBinder? = null
}
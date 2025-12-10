package ir.techfocus.checkconnection

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ir.techfocus.checkconnection.CustomSpinner.OnSpinnerEventsListener
import ir.techfocus.checkconnection.databinding.FragmentSettingsBinding


class SettingsFragment(private val myContext: Context) : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        val view: View? = binding.getRoot()
        db = Utils().getInstanceDB(myContext)

        initLangSpinner()
        initOverlaySpinner()

        binding.imgClose.setOnClickListener { view -> parentFragmentManager.popBackStack() }

        return view
    }

    //==============================================================================================

    fun initLangSpinner() {
        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(myContext, R.layout.spinner_item_selected, resources.getStringArray(R.array.language_array))
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnLanguage.setSpinnerEventsListener(spinnerEventsLanguage(binding.spnLanguage))
        binding.spnLanguage.adapter = adapter
    }

    //==============================================================================================

    fun spinnerEventsLanguage(spinner: CustomSpinner): OnSpinnerEventsListener {
        return object : OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                spinner.setSelected(true)
            }

            override fun onSpinnerClosed() {
                spinner.setSelected(false)
            }

            override fun onSpinnerItemClick(position: Int) {
                spinner.setSelected(false)
                val selectedItem: String = spinner.selectedItem.toString()

                val languageValueFa: String = resources.getStringArray(R.array.language_array).get(0).toString()
                val languageValueEn: String = resources.getStringArray(R.array.language_array).get(1).toString()
                val languageValueSystemDefault: String = resources.getStringArray(R.array.language_array).get(2).toString()

                if (selectedItem == languageValueFa) {
                    Utils().saveSettings(myContext, Constants.LANGUAGE, Constants.LANGUAGE_FA)
                    (getActivity() as MainActivity).changeLocale(myContext, Constants.LANGUAGE_FA)


                } else if (selectedItem == languageValueEn) {
                    Utils().saveSettings(myContext, Constants.LANGUAGE, Constants.LANGUAGE_EN)
                    (getActivity() as MainActivity).changeLocale(myContext, Constants.LANGUAGE_EN)


                } else if (selectedItem == languageValueSystemDefault) {
                    Utils().saveSettings(myContext, Constants.LANGUAGE, Utils().getSystemLanguage(myContext))
                    (getActivity() as MainActivity).changeLocale(myContext, Utils().getSystemLanguage(myContext))
                }
            }
        }
    }

    //==============================================================================================

    fun initOverlaySpinner() {
        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(myContext, R.layout.spinner_item_selected, resources.getStringArray(R.array.overlay_array))
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnOverlay.setSpinnerEventsListener(spinnerEventsOverlay(binding.spnOverlay))
        binding.spnOverlay.adapter = adapter

        binding.spnOverlay.setSelection(Utils().getSettings(myContext, Constants.OVERLAY_KEY, "0").toInt())
    }

    //==============================================================================================

    fun spinnerEventsOverlay(spinner: CustomSpinner): OnSpinnerEventsListener {
        return object : OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                spinner.setSelected(true)
            }

            override fun onSpinnerClosed() {
                spinner.setSelected(false)
            }

            override fun onSpinnerItemClick(position: Int) {
                spinner.setSelected(false)
                val selectedItem: String = spinner.selectedItem.toString()

                Utils().saveSettings(myContext, Constants.OVERLAY_KEY, position.toString())

                val overlayTopLeft: String = resources.getStringArray(R.array.overlay_array).get(0).toString()
                val overlayTopRight: String = resources.getStringArray(R.array.overlay_array).get(1).toString()
                val overlayBottomLeft: String = resources.getStringArray(R.array.overlay_array).get(2).toString()
                val overlayBottomRight: String = resources.getStringArray(R.array.overlay_array).get(3).toString()
                val overlayDefault: String = resources.getStringArray(R.array.overlay_array).get(4).toString()

                when (selectedItem) {
                    overlayTopLeft -> {
                        Utils().saveSettings(myContext, Constants.OVERLAY_X_KEY, (getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START)).toString())
                        Utils().saveSettings(myContext, Constants.OVERLAY_Y_KEY, (getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)).toString())
                        sendOverlayUpdate(Constants.OVERLAY_TOP_LEFT_KEY,
                            getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START),
                            getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)
                        )
                    }

                    overlayTopRight -> {
                        Utils().saveSettings(myContext, Constants.OVERLAY_X_KEY, (getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_END)).toString())
                        Utils().saveSettings(myContext, Constants.OVERLAY_Y_KEY, (getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)).toString())
                        sendOverlayUpdate(Constants.OVERLAY_TOP_RIGHT_KEY,
                            getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_END),
                            getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)
                        )
                    }

                    overlayBottomLeft -> {
                        Utils().saveSettings(myContext, Constants.OVERLAY_X_KEY, (getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START)).toString())
                        Utils().saveSettings(myContext, Constants.OVERLAY_Y_KEY, (getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_END)).toString())
                        sendOverlayUpdate(Constants.OVERLAY_BOTTOM_LEFT_KEY,
                            getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START),
                            getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_END)
                        )
                    }

                    overlayBottomRight -> {
                        Utils().saveSettings(myContext, Constants.OVERLAY_X_KEY, (getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_END)).toString())
                        Utils().saveSettings(myContext, Constants.OVERLAY_Y_KEY, (getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_END)).toString())
                        sendOverlayUpdate(Constants.OVERLAY_BOTTOM_RIGHT_KEY,
                            getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_END),
                            getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_END)
                        )
                    }

                    overlayDefault -> {
                        Utils().saveSettings(myContext, Constants.OVERLAY_X_KEY, (getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START)).toString())
                        Utils().saveSettings(myContext, Constants.OVERLAY_Y_KEY, (getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)).toString())
                        sendOverlayUpdate(Constants.OVERLAY_TOP_LEFT_KEY,
                            getScreenWidthByPercentage(Constants.OVERLAY_PERCENTAGE_X_START),
                            getScreenHeightByPercentage(Constants.OVERLAY_PERCENTAGE_Y_START)
                        )
                    }
                }
            }
        }
    }

    //==============================================================================================

    fun getScreenWidthByPercentage(percentage: Int): Int {
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
        return (screenWidth / 100 * percentage)
    }

    fun getScreenHeightByPercentage(percentage: Int): Int {
        val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels
        return (screenHeight / 100 * percentage)
    }

    //==============================================================================================

    private fun sendOverlayUpdate(overlay: String, screenWidthByPercentage: Int, screenHeightByPercentage: Int) {
        val intent = Intent(Constants.OVERLAY_UPDATE)
        intent.putExtra(Constants.OVERLAY_KEY, overlay)
        intent.putExtra(Constants.OVERLAY_X_KEY, screenWidthByPercentage)
        intent.putExtra(Constants.OVERLAY_Y_KEY, screenHeightByPercentage)
        LocalBroadcastManager.getInstance(myContext).sendBroadcast(intent)
    }
}
package ir.techfocus.checkconnection

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import ir.techfocus.checkconnection.databinding.FragmentAlertDialogBinding


class AlertDialogFragment(private val myContext: Context, private val alertType: Int, private val isCancelable: Boolean) :
    DialogFragment() {

    private lateinit var binding: FragmentAlertDialogBinding

    private var decorView: View? = null

    private lateinit var inDialogAnim: Animation
    private lateinit var outDialogAnim: Animation

    private var titleText: String? = null
    private var contentText: String? = null

    private var cancelText: String? = null
    private var confirmText: String? = null

    private var cancelClickListener: AlertDialogListener? = null
    private var confirmClickListener: AlertDialogListener? = null

    //==========================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //==========================================================================================================================

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    //==========================================================================================================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlertDialogBinding.inflate(inflater)
        val view: View = binding.getRoot()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        decorView = dialog?.window?.decorView

        inDialogAnim = AnimationUtils.loadAnimation(myContext, R.anim.animation_dialog_in)
        outDialogAnim = AnimationUtils.loadAnimation(myContext, R.anim.animation_dialog_out)

        changeAlertType()

        binding.imgIcon.startAnimation(inDialogAnim)


        if (!titleText.isNullOrEmpty()) {
            binding.txtTitle.visibility = View.VISIBLE
            binding.txtTitle.text = titleText

        } else {
            binding.txtTitle.visibility = View.GONE
        }

        if (!contentText.isNullOrEmpty()) {
            binding.txtContent.visibility = View.VISIBLE
            binding.txtContent.text = contentText

        } else {
            binding.txtContent.visibility = View.GONE
        }

        if (!cancelText.isNullOrEmpty()) {
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnCancel.text = cancelText

        } else {
            binding.btnCancel.visibility = View.GONE
        }

        if (!confirmText.isNullOrEmpty()) {
            binding.btnConfirm.visibility = View.VISIBLE
            binding.btnConfirm.text = confirmText

        } else {
            binding.btnConfirm.visibility = View.GONE
        }


        binding.btnConfirm.setOnClickListener({ v ->
            confirmClickListener?.onClick(this)
        })

        binding.btnCancel.setOnClickListener({ v ->
            cancelClickListener?.onClick(this)
        })

        return view
    }

    //==========================================================================================================================

    override fun onStart() {
        super.onStart()

        if (decorView != null) {
            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                decorView,
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f)
            )

            scaleDown.setDuration(300)
            scaleDown.start()
        }
    }

    //==========================================================================================================================

    fun dismissWithAnimation() {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            decorView,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.95f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.95f),
            PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f)
        )

        scaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                try {
                    dismissAllowingStateLoss()
                } catch (e: Exception) {
                    e.printStackTrace()
                    dismiss()
                }
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })

        scaleDown.duration = 200
        scaleDown.start()
    }

    //==========================================================================================================================

    private fun changeAlertType() {
        when (alertType) {
            Constants.NORMAL_TYPE -> binding.imgIcon.setVisibility(View.GONE)
            Constants.SUCCESS_TYPE -> binding.imgIcon.setImageResource(R.drawable.dialog_success)
            Constants.ERROR_TYPE -> binding.imgIcon.setImageResource(R.drawable.dialog_failed)
            Constants.WARNING_TYPE -> binding.imgIcon.setImageResource(R.drawable.dialog_warning)
        }
    }

    //==========================================================================================================================

    fun getTitleText(): String? {
        return this.titleText
    }

    fun setTitleText(titleText: String) {
        this.titleText = titleText
    }

    //==========================================================================================================================

    fun getContentText(): String? {
        return this.contentText
    }

    fun setContentText(contentText: String) {
        this.contentText = contentText
    }

    //==========================================================================================================================

    fun getCancelText(): String? {
        return this.cancelText
    }

    fun setCancelText(cancelText: String) {
        this.cancelText = cancelText
    }

    //==========================================================================================================================

    fun getConfirmText(): String? {
        return this.confirmText
    }

    fun setConfirmText(confirmText: String) {
        this.confirmText = confirmText
    }

    //==========================================================================================================================

    fun setCancelClickListener(listener: AlertDialogListener?): AlertDialogFragment {
        this.cancelClickListener = listener
        return this
    }

    fun setConfirmClickListener(listener: AlertDialogListener?): AlertDialogFragment {
        this.confirmClickListener = listener
        return this
    }

    //==========================================================================================================================

    interface AlertDialogListener {
        fun onClick(alertDialog: AlertDialogFragment?)
    }
}
package kg.autojuuguch.automoikakg.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kg.autojuuguch.automoikakg.R

abstract class BaseBSFragment : BottomSheetDialogFragment {

    constructor() : super()
    constructor(lightDim: Boolean, isTransparent: Boolean) : super() {
        isLightDimBackground = lightDim
        isTransparentBackground = isTransparent
    }

    private var mActivity: BaseActivity? = null
    private var isLightDimBackground = false
    private var isTransparentBackground = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) this.mActivity = context
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            if (isLightDimBackground) dialog.window?.setDimAmount(0.3f)
        }
        return dialog
    }

    override fun getTheme(): Int {
        return if (!isTransparentBackground) super.getTheme()
        else R.style.TransparentBottomSheetDialogTheme
    }


    fun hideBottomSheetFragment() = dismiss()


    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    @LayoutRes
    abstract fun layout(): Int
}
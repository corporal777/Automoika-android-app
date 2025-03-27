package kg.autojuuguch.automoikakg.ui.base

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.ScaleProvider
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.isVisibleAnim
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.main.MainActivity
import kg.autojuuguch.automoikakg.ui.views.LoadingButton


abstract class BaseVBFragment<VB : ViewBinding> : Fragment() {

    private var mActivity: BaseActivity? = null

    abstract fun binding(): Class<VB>
    private val localBinding get() = binding()
    protected val mBinding: VB by viewBinding(localBinding)

    abstract val viewModel: BaseViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) this.mActivity = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (animationType() == AnimType.AXIS) {
            postponeEnterTransition()
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = (300).toLong()
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                duration = (400).toLong()
            }
        } else if (animationType() == AnimType.FADE) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                primaryAnimatorProvider.apply {
                    if (this is ScaleProvider) incomingStartScale = 0.94f
                }
            }
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBarOffset(appBarScrollOffset)
        setEmptyDataPlaceholder(isEmptyData)

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            showErrorMessage(R.string.request_error_text)
        }
    }


    inline fun <reified T : Any?> LiveData<T>.observe(crossinline block : (value : T) -> Unit){
        observe(viewLifecycleOwner){ block.invoke(it) }
    }

    @LayoutRes
    abstract fun layout(): Int

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    fun navigateUp() {
        mActivity?.doVibration()
        findNavController().navigateUp()
    }


    fun showBackDialog() {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.are_you_sure_exit_text),
            getString(R.string.yes),
            getString(R.string.no)
        ).setSelectCallback { findNavController().navigateUp() }
    }


    fun showErrorMessage(message: Int) = showErrorMessage(getString(message))
    fun showErrorMessage(message: String) {
        mActivity?.showSnackBarMessage(message, R.drawable.ic_not_found, Toast.LENGTH_SHORT)
    }

    fun showSuccessMessage(message: Int) = showSuccessMessage(getString(message))
    fun showSuccessMessage(message: String) {
        mActivity?.showSnackBarMessage(message, R.drawable.ic_done, Toast.LENGTH_SHORT)
    }

    fun showSuccessMessage(message: Int, duration: Int) {
        mActivity?.showSnackBarMessage(getString(message), R.drawable.ic_done, duration)
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        try {
            if (isAdded && context != null) operation(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    enum class AnimType { FADE, AXIS, NONE }

    open fun animationType(): AnimType = AnimType.NONE
    open fun scrollToFirstItem() {}
    open fun viewPlaceholder(): View? = null

    private var isEmptyData = false
    protected fun setEmptyDataPlaceholder(show: Boolean) {
        isEmptyData = show
        try {
            viewPlaceholder()?.isVisibleAnim = show
        } catch (e: Exception) {
        }
    }


    fun isPreviousDestination(id: Int): Boolean {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val prevId = navHostFragment.navController.previousBackStackEntry?.destination?.id
        return prevId == id
    }


    open fun setAppBarOffset(offset: Float) {}
    fun updateAppBarViews(offset: Float) {
        appBarScrollOffset = offset
        setAppBarOffset(offset)
    }

    private var appBarScrollOffset = 0f
    private var collapseState: Pair<Int, Int>? = null

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }

}
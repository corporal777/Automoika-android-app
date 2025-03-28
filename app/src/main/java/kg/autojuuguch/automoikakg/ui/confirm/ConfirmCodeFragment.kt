package kg.autojuuguch.automoikakg.ui.confirm

import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ScrollingView
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentConfirmCodeBinding
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.extensions.navigatePopUp
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.extensions.startIntent
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kg.autojuuguch.automoikakg.utils.Utils
import kg.autojuuguch.automoikakg.utils.PermissionUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmCodeFragment : BaseToolbarFragment<FragmentConfirmCodeBinding>() {

    private val args: ConfirmCodeFragmentArgs by navArgs()
    override val viewModel by viewModel<ConfirmCodeViewModel>()
    private val permissionsUtil by lazy { PermissionUtils(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.phone = args.phone
        viewModel.user = args.user
        viewModel.sendCode(permissionsUtil)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvMobilePhone.text = Utils.formatMobilePhone(args.phone)
            phoneCodeView.doOnCodeChanged {
                btnConfirm.isEnabled = it.isComplete
                viewModel.onChangeCode(it.code)
            }
            btnSendAgain.setClickListener {
                viewModel.sendCode(permissionsUtil)
            }
            btnConfirm.apply {
                isEnabled = viewModel.isCodeValid(phoneCodeView.getCode())
                setClickListener { viewModel.confirmPhone() }
            }
        }
        observeCodeState()
        observeTimeLeft()
        observeLoading()
        observeCode()
    }

    private fun observeCodeState() {
        viewModel.navigateUp.observe { navigateUpResult() }
        viewModel.welcome.observe { showWelcomeFragment() }
        viewModel.codeError.observe { mBinding.tvCodeError.isVisibleFastAnim = it }
        viewModel.permissionError.observe { showPermissionIsNeed() }
    }


    private fun observeTimeLeft() {
        viewModel.timeLeft.observe(viewLifecycleOwner) {
            mBinding.btnSendAgain.apply {
                isEnabled = it <= 0
                if (it > 0) setText("Получить повторно · 0:$it") else setText("Получить повторно")
            }
        }
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it.first == 0) mBinding.btnConfirm.showProgressLoading(it.second)
            else mBinding.btnSendAgain.showProgressLoading(it.second)
        }
    }

    private fun observeCode() {
        viewModel.codeAutoFill.observe(viewLifecycleOwner) {
            mBinding.phoneCodeView.setCodeAutoFill(it)
            showSuccessMessage(R.string.auto_fill_code)
        }
    }

    private fun navigateUpResult() {
        setFragmentResult("confirm", bundleOf("phone" to true))
        findNavController().navigateUp()
    }

    private fun showWelcomeFragment() {
        findNavController().navigate(R.id.welcome_fragment)
    }



    private fun showPermissionIsNeed() {
        DefaultAlertDialog(
            requireContext(),
            getString(R.string.notification_permission_title),
            getString(R.string.notification_permission_text),
            getString(R.string.open),
            getString(R.string.confirm_phone_negative)
        ).setSelectCallback {
            startIntent(ACTION_APPLICATION_DETAILS_SETTINGS) {
                data = Uri.parse("package:${requireContext().packageName}")
            }
        }
    }

    override fun onDestroy() {
        permissionsUtil.unregister()
        super.onDestroy()
    }

    override val title: CharSequence by lazy { getString(R.string.confirm_phone_text) }
    override fun scrollingView(): ScrollingView = mBinding.scrollView
    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentConfirmCodeBinding::class.java
    override fun layout(): Int = R.layout.fragment_confirm_code
}
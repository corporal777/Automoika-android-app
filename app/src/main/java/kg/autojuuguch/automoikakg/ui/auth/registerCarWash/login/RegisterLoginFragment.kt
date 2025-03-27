package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.login

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.databinding.FragmentUserRegisterShortBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterLoginFragment : BaseToolbarFragment<FragmentUserRegisterShortBinding>() {

    override val viewModel by viewModel<RegisterLoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLogin.onInputTextChanged { viewModel.onChangeLogin(it.toString()) }
            viewPassword.onPasswordChanged { viewModel.onChangePassword(it) }
            btnContinue.setClickListener { viewModel.checkLogin() }
        }
        observeLogin()
        observeButtonState()
    }

    private fun observeLogin() {
        viewModel.showConfirm.observe { showConfirmCodeFragment() }
        viewModel.showRegister.observe { showRegisterMainFragment(it) }
        viewModel.passwordError.observe {
            showPasswordError(it)
            if (it) showPhoneError()
        }
    }

    private fun observeButtonState(){
        mBinding.btnContinue.apply {
            viewModel.buttonEnabled.observe { isEnabled = it }
            viewModel.buttonLoading.observe { isProgressShown = it }
        }
    }


    private fun showPasswordError(show: Boolean) {
        mBinding.tvPassword.apply {
            changeTitleTextColor(show)
            text = if (show) getString(R.string.input_password_error_hint) else getString(R.string.input_password_hint)
        }
    }

    private fun showPhoneError() {
        val message = getString(R.string.register_login_error_text, viewModel.getPhone())
        DefaultAlertDialog(
            requireContext(),
            null,
            message,
            getString(R.string.ok)
        ).setSelectCallback {  }
    }

    private fun showConfirmCodeFragment() {
        val login = LoginBody(viewModel.getPhone(), viewModel.getPassword())
        val bundle = bundleOf("phone" to viewModel.getPhone(), "login" to login)
        findNavController().navigate(R.id.confirm_code_fragment, bundle)
    }

    private fun showRegisterMainFragment(userId : String) {
        val bundle = bundleOf("userId" to userId)
        findNavController().navigate(R.id.register_main_fragment, bundle)
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun scrollingView(): View = mBinding.scrollView
    override fun toolbarView(): ToolbarLayoutView = mBinding.layoutToolbar
    override fun binding() = FragmentUserRegisterShortBinding::class.java
    override fun layout(): Int = R.layout.fragment_user_register_short
}
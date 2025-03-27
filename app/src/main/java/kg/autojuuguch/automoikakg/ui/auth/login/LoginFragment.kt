package kg.autojuuguch.automoikakg.ui.auth.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.firebase.auth.PhoneAuthOptions
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.databinding.FragmentLoginBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.hideKeyboard
import kg.autojuuguch.automoikakg.extensions.isVisibleAnim
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onTextChanged
import kg.autojuuguch.automoikakg.extensions.setBackgroundInput
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.extensions.setResultListener
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.confirm.ConfirmCodeFragmentArgs
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kg.autojuuguch.automoikakg.utils.FirebaseAuthUtils
import kg.autojuuguch.automoikakg.utils.Utils
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class LoginFragment : BaseToolbarFragment<FragmentLoginBinding>() {

    private val args: LoginFragmentArgs by navArgs()
    override val viewModel by viewModel<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLogin.onInputTextChanged { viewModel.onChangeLogin(it.toString()) }
            etPassword.onAfterTextChanged { viewModel.onChangePassword(it.toString()) }
            btnLogin.setClickListener { viewModel.loginToAccount() }
        }
        observeLogin()
        observeButtonState()
    }

    private fun observeLogin() {
        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            if (args.fromUserReg) showCarWashRegisterFragment()
            else showWelcomeFragment()
        }
        viewModel.phoneError.observe(viewLifecycleOwner) {
            showLoginError(it)
            if (it) showErrorMessage(R.string.user_not_found_error_text)
        }
        viewModel.passwordError.observe(viewLifecycleOwner) {
            showPasswordError(it)
            if (it) showErrorMessage(R.string.input_password_error_text)
        }
    }

    private fun observeButtonState() {
        mBinding.btnLogin.apply {
            viewModel.buttonEnabled.observe(viewLifecycleOwner) { isEnabled = it }
            viewModel.buttonLoading.observe(viewLifecycleOwner) { isProgressShown = it }
        }
    }


    private fun showLoginError(show: Boolean) {
        mBinding.tvLoginTitle.apply {
            changeTitleTextColor(show)
            text =
                if (show) getString(R.string.user_with_phone_not_found_error_text)
                else getString(R.string.login_text)
        }
    }

    private fun showPasswordError(show: Boolean) {
        mBinding.tvPasswordTitle.apply {
            changeTitleTextColor(show)
            text =
                if (show) getString(R.string.input_password_error_hint)
                else getString(R.string.input_password_hint)
        }
    }

    private fun showWelcomeFragment() {
        findNavController().navigate(R.id.welcome_fragment)
    }

    private fun showCarWashRegisterFragment() {
        findNavController().navigate(R.id.register_main_fragment)
    }

    override fun scrollingView(): View = mBinding.scrollView
    override fun toolbarView(): ToolbarLayoutView = mBinding.layoutToolbar
    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentLoginBinding::class.java
    override fun layout(): Int = R.layout.fragment_login
}
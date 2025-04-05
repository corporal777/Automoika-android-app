package kg.autojuuguch.automoikakg.ui.auth.registerUser

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ScrollingView
import androidx.navigation.fragment.findNavController
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentUserRegisterBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.getColor
import kg.autojuuguch.automoikakg.extensions.getPrivacyPoliticsText
import kg.autojuuguch.automoikakg.extensions.initChecked
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserRegisterFragment : BaseToolbarFragment<FragmentUserRegisterBinding>() {

    override val viewModel by viewModel<UserRegisterViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etName.onAfterTextChanged { viewModel.onChangeFirstName(it.toString()) }
            etLogin.onInputTextChanged { viewModel.onChangePhone(it.toString()) }
            viewPassword.onPasswordChanged { viewModel.onChangePassword(it) }
            tvAgreement.apply {
                scAgreement.initChecked(viewModel.isAgree()) { viewModel.onChangeAgreement(it) }
                highlightColor = getColor(R.color.bottom_nav_item_default_color)
                movementMethod = LinkMovementMethod.getInstance()
                text = getPrivacyPoliticsText()
            }

            btnContinue.setClickListener { viewModel.checkPhoneIsUnique() }
        }
        observeButtonState()
        observePhoneUnique()
        observeErrors()
    }


    private fun observeButtonState() {
        mBinding.btnContinue.apply {
            viewModel.buttonEnabled.observe(viewLifecycleOwner) { isSelected = it }
            viewModel.buttonLoading.observe(viewLifecycleOwner) { isProgressShown = it }
        }
    }

    private fun observePhoneUnique() {
        viewModel.phoneUnique.observe(viewLifecycleOwner) {
            if (it) showPhoneIsNotUnique(viewModel.getPhone())
            else showConfirmCodeFragment()
        }
    }


    private fun observeErrors() {
        mBinding.apply {
            viewModel.firstNameError.observe { tvFirstName.changeTitleTextColor(it) }
            viewModel.phoneError.observe { tvLogin.changeTitleTextColor(it) }
            viewModel.passwordError.observe { tvPassword.changeTitleTextColor(it) }
            viewModel.agreementError.observe { scAgreement.isSelected = it }
        }

    }

    private fun showPhoneIsNotUnique(login: String) {
        val message = getString(R.string.is_not_unique_phone_text, login)
        DefaultAlertDialog(
            requireContext(),
            null,
            message,
            getString(R.string.ok),
        ).setSelectCallback {  }
    }


    private fun showConfirmCodeFragment() {
        val bundle = bundleOf("phone" to viewModel.getPhone(), "user" to viewModel.getBody())
        findNavController().navigate(R.id.confirm_code_fragment, bundle)
    }

    override val title: CharSequence by lazy { getString(R.string.register_text) }
    override fun animationType(): AnimType = AnimType.FADE
    override fun scrollingView(): ScrollingView = mBinding.scrollView
    override fun binding() = FragmentUserRegisterBinding::class.java
    override fun layout(): Int = R.layout.fragment_user_register
}
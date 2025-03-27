package kg.autojuuguch.automoikakg.ui.auth.registerUser

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.databinding.FragmentUserRegisterBinding
import kg.autojuuguch.automoikakg.databinding.FragmentUserRegisterShortBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.getColor
import kg.autojuuguch.automoikakg.extensions.getPrivacyPoliticsText
import kg.autojuuguch.automoikakg.extensions.initChecked
import kg.autojuuguch.automoikakg.extensions.initInput
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onCheckedChanged
import kg.autojuuguch.automoikakg.extensions.onTextChanged
import kg.autojuuguch.automoikakg.extensions.setArgument
import kg.autojuuguch.automoikakg.extensions.setBackgroundInput
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.extensions.setColorSpan
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.gallery.GalleryBottomSheet
import kg.autojuuguch.automoikakg.ui.gallery.GalleryBottomSheet.Companion.GALLERY_TAG
import kg.autojuuguch.automoikakg.ui.gallery.GalleryType
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class UserRegisterFragment : BaseToolbarFragment<FragmentUserRegisterShortBinding>() {

    override val viewModel by viewModel<UserRegisterViewModel>()
    private val args : UserRegisterFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setBody(args.fromUserReg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etFirstName.onAfterTextChanged { viewModel.onChangeFirstName(it.toString()) }
            etLogin.onInputTextChanged { viewModel.onChangePhone(it.toString()) }
            viewPassword.onPasswordChanged { viewModel.onChangePassword(it) }
            tvAgreement.apply {
                scAgreement.initChecked(viewModel.isAgree()) { viewModel.onChangeAgreement(it) }
                highlightColor = getColor(R.color.bottom_nav_item_default_color)
                movementMethod = LinkMovementMethod.getInstance()
                text = getPrivacyPoliticsText()
            }
            tvGoLogin.apply {
                isInvisible = args.fromUserReg
                setClickListener { showLoginFragment() }
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

    private fun showLoginFragment() {
        val bundle = bundleOf("fromUserReg" to true)
        findNavController().navigate(R.id.login_fragment, bundle)
    }

    private fun showConfirmCodeFragment() {
        val bundle = bundleOf("phone" to viewModel.getPhone(), "user" to viewModel.getBody())
        findNavController().navigate(R.id.confirm_code_fragment, bundle)
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun toolbarView(): ToolbarLayoutView = mBinding.layoutToolbar
    override fun scrollingView(): View = mBinding.scrollView
    override fun binding() = FragmentUserRegisterShortBinding::class.java
    override fun layout(): Int = R.layout.fragment_user_register_short
}
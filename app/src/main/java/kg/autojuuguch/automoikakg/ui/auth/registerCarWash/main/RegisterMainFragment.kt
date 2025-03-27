package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.body.CarWashRegisterBody
import kg.autojuuguch.automoikakg.databinding.FragmentRegisterCarWashMainBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.initDropDownAdapter
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onBackPressedCallback
import kg.autojuuguch.automoikakg.extensions.setArgument
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.confirm.ConfirmCodeFragmentArgs
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import kg.autojuuguch.automoikakg.ui.gallery.GalleryBottomSheet
import kg.autojuuguch.automoikakg.ui.gallery.GalleryBottomSheet.Companion.GALLERY_TAG
import kg.autojuuguch.automoikakg.ui.gallery.GalleryType
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.PermissionUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RegisterMainFragment : BaseToolbarFragment<FragmentRegisterCarWashMainBinding>() {

    override val viewModel by viewModel<RegisterMainViewModel>()
    private lateinit var permissionUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionUtils = PermissionUtils(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etName.onAfterTextChanged { viewModel.onChangeName(it.toString()) }
            etDescription.onAfterTextChanged { viewModel.onChangeDescription(it.toString()) }
            etBoxes.onAfterTextChanged { viewModel.onChangeBoxes(it.toString()) }
            etType.apply {
                initDropDownAdapter(mutableListOf("Сам мой", "Услуги автомойщика"))
                onAfterTextChanged { viewModel.onChangeType(it.toString()) }
            }
            tvAddPhoto.setOnClickListener {
                permissionUtils.checkGalleryPermission { if (it) showGallery() }
            }
            btnContinue.setClickListener { viewModel.continueRegister() }
        }
        observeImage()
        observeState()
        observeErrors()
    }

    private fun observeImage() {
        viewModel.imageBitmap.observe {
            mBinding.cardAvatar.setImage(it)
            mBinding.tvAddPhoto.text =
                if (it == null) getString(R.string.add_image_text) else getString(R.string.change_image_text)
        }
    }

    private fun observeState() {
        viewModel.buttonEnabled.observe { mBinding.btnContinue.setSelected(it) }
        viewModel.buttonLoading.observe { mBinding.btnContinue.showProgressLoading(it) }
        viewModel.register.observe { showContactsFragment(it) }
    }

    private fun observeErrors() {
        mBinding.apply {
            viewModel.nameError.observe { tvName.changeTitleTextColor(it) }
            viewModel.descriptionError.observe { tvDescription.changeTitleTextColor(it) }
            viewModel.boxesError.observe { tvBoxes.changeTitleTextColor(it) }
            viewModel.typeError.observe { tvType.changeTitleTextColor(it) }
        }
    }


    private fun showContactsFragment(body: CarWashRegisterBody) {
        val bundle = bundleOf("register" to body)
        findNavController().navigate(R.id.register_contacts_fragment, bundle)
    }


    private fun showGallery() {
        GalleryBottomSheet()
            .setArgument<GalleryBottomSheet>(GALLERY_TAG, GalleryType.CAR_WASH)
            .setCropCallback { viewModel.onChangeImage(it, requireContext()) }
            .show(childFragmentManager)
    }


    override fun onResume() {
        super.onResume()
        onBackPressedCallback {
            if (viewModel.isDataFilled()) showBackDialog()
            else findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        permissionUtils.unregister()
        super.onDestroy()
    }

    override fun scrollingView(): View = mBinding.scrollView
    override fun toolbarView(): ToolbarLayoutView = mBinding.layoutToolbar
    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentRegisterCarWashMainBinding::class.java
    override fun layout(): Int = R.layout.fragment_register_car_wash_main
}
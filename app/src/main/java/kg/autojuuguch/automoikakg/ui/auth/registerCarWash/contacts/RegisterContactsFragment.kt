package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.contacts

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kg.autojuuguch.automoikakg.databinding.FragmentRegisterCarWashContactsBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.initChecked
import kg.autojuuguch.automoikakg.extensions.initDropDownAdapter
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onBackPressedCallback
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.extensions.setResultListener
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kg.autojuuguch.automoikakg.utils.Utils.getCityDistrict
import kg.autojuuguch.automoikakg.utils.Utils.getCityDistricts
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterContactsFragment : BaseToolbarFragment<FragmentRegisterCarWashContactsBinding>() {

    private val args: RegisterContactsFragmentArgs by navArgs()
    override val viewModel by viewModel<RegisterContactsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.body = args.register
        viewModel.performDataChange()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etCity.onAfterTextChanged { viewModel.onChangeCity(it.toString()) }
            etStreet.onAfterTextChanged { viewModel.onChangeStreet(it.toString()) }
            etDistrict.apply {
                initDropDownAdapter(getCityDistricts().toMutableList())
                onAfterTextChanged { viewModel.onChangeDistrict(it.toString()) }
            }
            etWay.onAfterTextChanged { viewModel.onChangeWay(it.toString()) }

            etPhone.onInputTextChanged { viewModel.onChangePhone(it.toString()) }
            etWhatsapp.onInputTextChanged { viewModel.onChangeWhatsapp(it.toString()) }
            etInstagram.onAfterTextChanged { viewModel.onChangeInstagram(it.toString()) }

            scAgreement.initChecked(viewModel.isAgree) { viewModel.onChangeAgreement(it) }
            tvFindInMap.setOnClickListener { showMapFragment() }
            btnContinue.setClickListener { viewModel.registerCarWash() }
        }
        observeState()
        observeErrors()
        observeLocation()
    }



    private fun observeState() {
        viewModel.buttonEnabled.observe { mBinding.btnContinue.setSelected(it) }
        viewModel.buttonLoading.observe { mBinding.btnContinue.showProgressLoading(it) }
        viewModel.registerSuccess.observe { showWelcomeFragment() }
    }

    private fun observeErrors() {
        mBinding.apply {
            viewModel.cityError.observe { tvCity.changeTitleTextColor(it) }
            viewModel.streetError.observe { tvStreet.changeTitleTextColor(it) }
            viewModel.phoneError.observe { tvPhone.changeTitleTextColor(it) }
            viewModel.agreementError.observe { scAgreement.isSelected = it }
        }
    }

    private fun observeLocation() {
        viewModel.location.observe {
            mBinding.etCity.setText(it.getGeoCity())
            mBinding.etStreet.setText(it.getGeoStreet())
            mBinding.etDistrict.setText(getCityDistrict(it.getGeoDistrict()))
        }
    }

    private fun showMapFragment(){
        findNavController().navigate(R.id.map_fragment, bundleOf("fromReg" to true))
        setResultListener<YandexGeoModel>("map", "loc") { viewModel.onChangeLocation(it) }
    }

    private fun showWelcomeFragment() {
        findNavController().navigate(R.id.welcome_fragment)
    }


    override fun onResume() {
        super.onResume()
        onBackPressedCallback {
            if (viewModel.isDataFilled()) showBackDialog()
            else findNavController().navigateUp()
        }
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun scrollingView(): View = mBinding.scrollView
    override fun toolbarView(): ToolbarLayoutView = mBinding.layoutToolbar
    override fun binding() = FragmentRegisterCarWashContactsBinding::class.java
    override fun layout(): Int = R.layout.fragment_register_car_wash_contacts
}
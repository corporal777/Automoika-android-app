package kg.autojuuguch.automoikakg.ui.city

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentCityBinding
import kg.autojuuguch.automoikakg.extensions.isVisibleAnim
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.extensions.navigatePopUp
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onBackPressedCallback
import kg.autojuuguch.automoikakg.extensions.onFocusChanged
import kg.autojuuguch.automoikakg.extensions.setBackgroundInput
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.location.FlowableLocation
import kg.autojuuguch.automoikakg.utils.location.LocationUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class CityFragment : BaseVBFragment<FragmentCityBinding>() {

    override val viewModel by viewModel<CityViewModel>()

    private lateinit var locationUtils: FlowableLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationUtils = FlowableLocation(this)
        viewModel.initLocation(locationUtils.getLocation())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback { showHomeFragment() }
        mBinding.apply {
            etCity.onAfterTextChanged {
                viewModel.onChangeCity(it.toString())
                btnSave.isEnabled = it.toString().isNotEmpty()
            }
            btnChange.setOnClickListener { viewModel.onShowEnterCity() }
            btnAccept.setOnClickListener { viewModel.onSaveUserCity() }
            btnSave.apply {
                isEnabled = etCity.text.toString().isNotEmpty()
                setClickListener { viewModel.onSaveUserCity() }
            }
        }
        observeUserLocation()
        observeLoading()
    }

    private fun observeUserLocation() {
        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            mBinding.apply {
                lnChangeCity.isVisibleFastAnim = location == null
                lnCurrentCity.isVisibleAnim = location != null

                if (location != null) tvCityTitle.apply {
                    isVisibleFastAnim = true
                    text = getString(R.string.is_your_city_is, location.city)
                }
            }
        }
        viewModel.citySuccessSaved.observe(viewLifecycleOwner){
            if (it) {
                showSuccessMessage(R.string.city_success_saved_text)
                showHomeFragment()
            } else showErrorMessage(R.string.city_not_found_error_text)
        }
    }

    private fun observeLoading() {
        viewModel.buttonLoading.observe(viewLifecycleOwner) {
            mBinding.btnChange.isEnabled = !it
            mBinding.btnAccept.showProgressLoading(it)
        }
    }


    private fun showHomeFragment(){
        findNavController().navigatePopUp(R.id.home_fragment)
    }

    override fun onDestroy() {
        locationUtils.clearFields()
        super.onDestroy()
    }

    override fun layout(): Int = R.layout.fragment_city
    override fun binding() = FragmentCityBinding::class.java
}
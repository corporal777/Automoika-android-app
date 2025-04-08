package kg.autojuuguch.automoikakg.ui.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.ScrollingView
import androidx.navigation.fragment.findNavController
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentProfileBinding
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.extensions.showActionDialog
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseToolbarFragment<FragmentProfileBinding>() {

    override val viewModel by viewModel<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvLogout.setOnClickListener {
                showActionDialog(getString(R.string.logout_text)){ viewModel.logoutUser() }
            }
        }
        observeUser()
        observeLoading()
    }

    private fun observeUser(){
        viewModel.user.observe {
            if (it != null) mBinding.apply {
                mBinding.viewAvatar.setImage(it.image.loadImage())
                mBinding.tvName.text = it.name
                mBinding.tvCity.text = "г. ${viewModel.getCity()}"
            }
            else logoutSuccess()
        }
    }

    private fun observeLoading(){
        viewModel.buttonLoading.observe {
            mBinding.clHeader.isVisibleFastAnim = !it
            mBinding.shimmerView.isVisibleFastAnim = it
        }
    }


    private fun logoutSuccess(){
        showSuccessMessage(R.string.logout_success_text)
        findNavController().navigateUp()
    }

    override val title: CharSequence by lazy { viewModel.getUserId() }
    override fun scrollingView(): ScrollingView = mBinding.profileScrollView
    override fun binding() = FragmentProfileBinding::class.java
    override fun layout(): Int = R.layout.fragment_profile
}
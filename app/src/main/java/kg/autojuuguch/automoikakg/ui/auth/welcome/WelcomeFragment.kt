package kg.autojuuguch.automoikakg.ui.auth.welcome

import android.os.Bundle
import android.view.View
import androidx.collection.emptyLongSet
import androidx.navigation.fragment.findNavController
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentWelcomeBinding
import kg.autojuuguch.automoikakg.extensions.navigatePopUp
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeFragment : BaseVBFragment<FragmentWelcomeBinding>() {

    override val viewModel by viewModel<WelcomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.tvWelcome.text =
            if (!viewModel.getUserName().isNullOrEmpty())
                getString(R.string.welcome_user_text, viewModel.getUserName())
            else getString(R.string.welcome_user_short_text)

        viewModel.withNavigateUp.observe(viewLifecycleOwner) {
            showHomeFragment()
        }
    }

    private fun showHomeFragment() {
        findNavController().navigatePopUp(R.id.home_fragment)
    }

    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentWelcomeBinding::class.java
    override fun layout(): Int = R.layout.fragment_welcome
}
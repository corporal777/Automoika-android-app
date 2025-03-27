package kg.autojuuguch.automoikakg.ui.splash

import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentSplashBinding
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseVBFragment<FragmentSplashBinding>() {

    override val viewModel by viewModel<SplashViewModel>()

    override fun binding() = FragmentSplashBinding::class.java
    override fun layout(): Int = R.layout.fragment_splash
}
package kg.autojuuguch.automoikakg.ui.main

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.getFragmentLifecycleCallback
import kg.autojuuguch.automoikakg.extensions.navigatePopUp
import kg.autojuuguch.automoikakg.extensions.onBackPressedCallback
import kg.autojuuguch.automoikakg.extensions.setClickListener
import kg.autojuuguch.automoikakg.extensions.setSystemBarsAppearance
import kg.autojuuguch.automoikakg.ui.base.BaseActivity
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.home.HomeFragment
import kg.autojuuguch.automoikakg.ui.map.MapFragment
import kg.autojuuguch.automoikakg.ui.profile.ProfileFragment
import kg.autojuuguch.automoikakg.utils.SYSTEM_UI_LIGHT_NAV_BAR
import kg.autojuuguch.automoikakg.utils.YandexAdUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override val navFragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onFragmentStarted = { f -> setupBackgroundTransparency(f) },
        onFragmentViewCreated = { f ->
            setupNavBarItems(f)
            setupNavigateUpButton(f)
            setupBackgroundImageFragment(f)

            binding.toolbar.apply {
                isVisible = f is BaseToolbarFragment<*>
                if (f is BaseToolbarFragment<*>) setToolbarTitle(f.title)
            }
        }
    )

    override val backClick = onBackPressedCallback {
        val fragment = getChildNavHostFragment() ?: return@onBackPressedCallback
        when (fragment) {
            is HomeFragment -> finish()
            else -> navigateUp()
        }
    }

    private var yandexAdUtils: YandexAdUtils? = null
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        super.onCreate(savedInstanceState)
        hideSplashScreen()

        MapKitFactory.initialize(this)

        viewModel.connectSocket()

        setupMainNavBar()
        setupYandexAds()
        observeUserCity()

        binding.toolbar.getBackButton().setOnClickListener { navigateUpVibration() }
    }

    private fun setupYandexAds() {
        yandexAdUtils = YandexAdUtils(this).setLoadCallback {
            viewModel.saveYandexAd(it)
        }
    }

    private fun observeUserCity() {
        viewModel.userCity.observe(this) {
            if (it.isNullOrEmpty()) showCityFragment()
            else showHomeFragment()
        }
    }


    private fun setupMainNavBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
            setSystemBarsAppearance(SYSTEM_UI_LIGHT_NAV_BAR)

        binding.bottomNavView.setupWithNavController(getNavHostFragment().navController)
        binding.bottomNavView.setOnItemReselectedListener { item ->
            val fragment = getChildNavHostFragment()
            if (item.itemId == R.id.main && fragment is HomeFragment) fragment.scrollToFirstItem()
            if (item.itemId == R.id.map && fragment is MapFragment) fragment.scrollToFirstItem()
        }
        val doPopBackStack: (Int) -> Boolean = { res ->
            if (!findNavController().popBackStack(res, false)) findNavController().navigate(res)
            true
        }
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main -> doPopBackStack.invoke(R.id.home_fragment)
                R.id.map -> doPopBackStack.invoke(R.id.map_fragment)
                R.id.favorites -> {
                    true
                }

                R.id.profile -> {
                    if (viewModel.isUserAuthorized()) doPopBackStack.invoke(R.id.profile_fragment)
                    else doPopBackStack.invoke(R.id.authorization_fragment)
                }

                else -> false
            }
        }
    }

    private fun setupNavBarItems(f: Fragment) {
        val menuItem: (Int) -> MenuItem = { binding.bottomNavView.menu.findItem(it) }
        when (f) {
            is HomeFragment -> {
                showNavigationBar()
                menuItem.invoke(R.id.main).isChecked = true
            }

            is MapFragment -> {
                showNavigationBar()
                menuItem.invoke(R.id.map).isChecked = true
            }

            is ProfileFragment -> {
                showNavigationBar()
                menuItem.invoke(R.id.profile).isChecked = true
            }
            //is ChatListTabsFragment -> menuItem.invoke(R.id.chats).isChecked = true

            else -> hideNavigationBar()
        }
    }

    private fun setupNavigateUpButton(f: Fragment) {
        when (f) {
            is ProfileFragment -> binding.toolbar.getBackButton().isInvisible = true
            else -> binding.toolbar.getBackButton().isInvisible = false
        }
    }


    private fun showCityFragment() {
        findNavController().navigatePopUp(R.id.city_fragment)
    }

    private fun showHomeFragment() {
        findNavController().navigatePopUp(R.id.home_fragment)
    }

    private fun showLoginFragment() {
        findNavController().navigate(R.id.authorization_fragment)
    }

    private fun showProfileFragment() {
        findNavController().navigate(R.id.profile_fragment)
    }

    private fun showNavigationBar() {
        window.navigationBarColor = getColor(R.color.bottom_navigation_bar_color)
        binding.navBarContainer.isVisible = true
    }

    private fun hideNavigationBar() {
        window.navigationBarColor = getColor(R.color.main_background)
        binding.navBarContainer.isVisible = false
    }


    private fun showSplashScreen() {
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }
    }

    private fun hideSplashScreen() {
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
    }

    fun setAppbarElevation(offset: Int) {
        binding.toolbar.setToolbarShadow(offset)
    }
}
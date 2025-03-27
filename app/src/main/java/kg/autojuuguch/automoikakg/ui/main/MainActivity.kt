package kg.autojuuguch.automoikakg.ui.main

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.yandex.mapkit.MapKitFactory
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.getFragmentLifecycleCallback
import kg.autojuuguch.automoikakg.extensions.onBackPressedCallback
import kg.autojuuguch.automoikakg.extensions.setSystemBarsAppearance
import kg.autojuuguch.automoikakg.ui.base.BaseActivity
import kg.autojuuguch.automoikakg.ui.home.HomeFragment
import kg.autojuuguch.automoikakg.ui.map.MapFragment
import kg.autojuuguch.automoikakg.utils.SYSTEM_UI_LIGHT_NAV_BAR
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override val navFragmentsLifecycleCallback = getFragmentLifecycleCallback(
        onFragmentStarted = { f -> setupBackgroundTransparency(f) },
        onFragmentViewCreated = { f ->
            setupNavBar(f)
            setupNavBarItems(f)
            setupBackgroundImageFragment(f)
        }
    )

    private val backClick = onBackPressedCallback {
        val fragment = getChildNavHostFragment() ?: return@onBackPressedCallback
        when (fragment) {
            is HomeFragment -> finish()
            else -> navigateUp()
        }
    }

    private lateinit var splashScreen: SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        super.onCreate(savedInstanceState)
        hideSplashScreen()

        MapKitFactory.initialize(this)

        setupMainNavBar()
        observeUserCity()

        onBackPressedDispatcher.addCallback(this, backClick)
        viewModel.connectSocket()
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
                R.id.favorites -> { true}
                R.id.profile -> {
                    if (!viewModel.isUserAuthorized()) showLoginFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavBarItems(f: Fragment) {
        val menuItem: (Int) -> MenuItem = { binding.bottomNavView.menu.findItem(it) }
        when (f) {
            is HomeFragment -> menuItem.invoke(R.id.main).isChecked = true
            is MapFragment -> menuItem.invoke(R.id.map).isChecked = true
            //is ProfileFragment -> menuItem.invoke(R.id.profile).isChecked = true
            //is ChatListTabsFragment -> menuItem.invoke(R.id.chats).isChecked = true
            //is NotificationsListFragment -> menuItem.invoke(R.id.notification).isChecked = true
            //is MyEventsFragment -> menuItem.invoke(R.id.my_events).isChecked = true
        }
    }

    private fun setupNavBar(f: Fragment) {
        when (f) {
            is HomeFragment -> showNavigationBar()
            is MapFragment -> {
                if (!isPreviousDestination(R.id.register_contacts_fragment)) showNavigationBar()
            }
            else -> hideNavigationBar()
        }
    }

    private fun navigateUp() = findNavController().navigateUp()

    private fun showCityFragment(){
        val options = navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
        findNavController().navigate(R.id.city_fragment,null, options)
    }

    private fun showHomeFragment(){
        val options = navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
        findNavController().navigate(R.id.home_fragment,null, options)
    }

    private fun showLoginFragment(){
        findNavController().navigate(R.id.authorization_fragment)
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
}
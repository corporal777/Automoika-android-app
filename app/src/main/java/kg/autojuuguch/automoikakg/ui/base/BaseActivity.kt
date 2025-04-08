package kg.autojuuguch.automoikakg.ui.base

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.Vibrator
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ActivityMainBinding
import kg.autojuuguch.automoikakg.extensions.cancelWindowTransparency
import kg.autojuuguch.automoikakg.extensions.doEdgeWindow
import kg.autojuuguch.automoikakg.extensions.setWindowTransparency
import kg.autojuuguch.automoikakg.extensions.statusBarColorValue
import kg.autojuuguch.automoikakg.extensions.withDelayed
import kg.autojuuguch.automoikakg.ui.detail.CarWashDetailFragment
import kg.autojuuguch.automoikakg.ui.home.HomeFragment
import kg.autojuuguch.automoikakg.ui.stories.StoriesFragment
import kg.autojuuguch.automoikakg.utils.SYSTEM_UI_LIGHT_STATUS_BAR

abstract class BaseActivity : FragmentActivity() {

    lateinit var binding: ActivityMainBinding

    abstract val navFragmentsLifecycleCallback : FragmentManager.FragmentLifecycleCallbacks
    abstract val backClick : OnBackPressedCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerFragmentLifecycleCallback()
        onBackPressedDispatcher.addCallback(this, backClick)
    }


    fun showSnackBarMessage(message: String, icon: Int, duration : Int) {
        if (binding.viewSnackBar.isVisible) return
        binding.viewSnackBar.apply {
            setText(message)
            setIcon(icon)
            show()
            withDelayed(if (duration == 0) 2500 else 3000) { hide() }
        }
    }

    fun setupBackgroundImageFragment(f : Fragment){
        val isLightStatus= if (f is BackgroundImageFragment) f.isLightStatus else true
        statusBarColorValue = if (isLightStatus) SYSTEM_UI_LIGHT_STATUS_BAR else 0
    }


    fun setupBackgroundTransparency(f : Fragment){
        if (f is HomeFragment) window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
        else window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)

        if (f is CarWashDetailFragment) setWindowTransparency { f.setupToolbarTopMargin(it) }
        else if (f is StoriesFragment) doEdgeWindow()
        else cancelWindowTransparency()
    }


    fun isPreviousDestination(id : Int) : Boolean {
        val prevId = getNavHostFragment().navController.previousBackStackEntry?.destination?.id
        return prevId == id
    }


    fun findNavController() = findNavController(R.id.fragmentContainerView)


    fun getNavHostFragment(): NavHostFragment {
        return supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }


    fun getChildNavHostFragment(): Fragment? {
        return getNavHostFragment().childFragmentManager.fragments.firstOrNull()
    }


    fun doVibration(){
        if (Build.VERSION.SDK_INT >= 29){
            val vibrator = getSystemService(Vibrator::class.java)
            vibrator?.vibrate(VibrationEffect.createPredefined(EFFECT_CLICK))
        }
    }

    fun navigateUp() = findNavController().navigateUp()

    fun navigateUpVibration(){
        doVibration()
        navigateUp()
    }


    private fun registerFragmentLifecycleCallback() {
        getNavHostFragment().childFragmentManager
            .registerFragmentLifecycleCallbacks(navFragmentsLifecycleCallback, false)
    }


    private fun unregisterFragmentLifecycleCallback() {
        getNavHostFragment().childFragmentManager
            .unregisterFragmentLifecycleCallbacks(navFragmentsLifecycleCallback)
    }


    override fun onDestroy() {
        unregisterFragmentLifecycleCallback()
        super.onDestroy()
    }
}
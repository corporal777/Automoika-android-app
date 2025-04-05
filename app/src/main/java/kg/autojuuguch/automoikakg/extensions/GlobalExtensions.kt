package kg.autojuuguch.automoikakg.extensions

import android.R.attr.key
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.OpenableColumns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import kg.autojuuguch.automoikakg.BuildConfig
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.ui.base.BaseVBFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random


fun Context.isConnectedToNetwork(): Boolean {
    val callback = object : ConnectivityManager.NetworkCallback() {

    }
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}

fun getFragmentLifecycleCallback(
    onFragmentStarted: ((f: Fragment) -> Unit?)? = null,
    onFragmentStopped: ((f: Fragment) -> Unit?)? = null,
    onFragmentDestroyed: ((f: Fragment) -> Unit?)? = null,
    onFragmentViewCreated: (f: Fragment) -> Unit,
): FragmentManager.FragmentLifecycleCallbacks {
    val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            if (onFragmentDestroyed != null) onFragmentDestroyed(f)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            onFragmentViewCreated(f)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            if (onFragmentStarted != null) onFragmentStarted(f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            if (onFragmentStopped != null) onFragmentStopped(f)
        }
    }
    return callback
}

fun Activity.onBackPressedCallback(onBackClick: () -> Unit): OnBackPressedCallback {
    return object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() = onBackClick.invoke()
    }
}

fun Fragment.onBackPressedCallback(onBackClick: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onBackClick.invoke()
        }
    )
}

fun Activity.setWindowTransparency(listener: (Int) -> Unit) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
//    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.cancelWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.returnSystemInsets(window.decorView, listener)
    window.statusBarColor = getColor(R.color.main_background)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.doEdgeDisplay(window.decorView, listener)
}

typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtil {

    fun doEdgeDisplay(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }

    fun removeSystemInsets(view: View, listener: (Int) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            listener.invoke(insets.systemWindowInsetTop)
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom,
                )
            )
        }
    }
}


fun Fragment.getBitmapFromDrawable(res: Int): Bitmap? {
    return AppCompatResources.getDrawable(requireContext(), res)?.toBitmap(24.dp, 24.dp)
}

fun Fragment.getBitmapDrawable(res: Int, size : Int): Bitmap? {
    return AppCompatResources.getDrawable(requireContext(), res)?.toBitmap(size, size)
}

fun isGPSEnabled(context: Fragment): Boolean {
    val locationManager =
        context.requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun withDelayed(millis : Long, block : () -> Unit ){
    Handler().postDelayed({ block.invoke() }, millis)
}

fun BaseVBFragment<*>.startIntent(type : String, intent : Intent.() -> Unit){
    try {
        val newIntent = Intent(type).apply(intent)
        requireContext().startActivity(newIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.startIntent(type : String, intent : Intent.() -> Unit){
    try {
        val newIntent = Intent(type).apply(intent)
        startActivity(newIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun showKeyboard(context : Context, view: View){
    WindowCompat.getInsetsController((context as Activity).window, view)
        .show(WindowInsetsCompat.Type.ime())
}

fun hideKeyboard(context: Context, v: View?){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    v?.let {
        imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun Fragment.setResultListener(key : String, block: (value : Bundle) -> Unit){
    setFragmentResultListener(key) { _, bundle ->
        block.invoke(bundle)
        clearFragmentResultListener(key)
    }
}

inline fun <reified T : Parcelable> Fragment.setResultListener(
    key: String,
    code : String,
    crossinline block: (value: T) -> Unit
) {
    setFragmentResultListener(key) { _, bundle ->
        val obj = bundle.getParcelable<T>(code)
        if (obj != null) block.invoke(obj)
        clearFragmentResultListener(key)
    }
}

fun NavController.navigatePopUp(id : Int){
    val options = navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
    navigate(id, null, options)
}


fun Fragment.getMakeSceneTransition(view: View): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(
        requireActivity(),
        Pair(view, view.transitionName)
    )
}

fun saveImageToCache(context: Context, image: Bitmap): Uri? {
    val imagesFolder = File(context.cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "shared_image.png")
        val stream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return uri
}

fun Uri.fileName(contentResolver: ContentResolver): String? {
    return contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).let { nameIndex ->
                cursor.moveToFirst()
                if (nameIndex >= 0) cursor.getString(nameIndex)
                else null
            }
        }
}

fun objectTapListener(block : (point : Point) -> Unit): GeoObjectTapListener {
    val listener = GeoObjectTapListener {
        val point = it.geoObject.geometry.first().point
        if (point != null) block.invoke(point)
        true
    }
    return listener
}

fun mapObjectTapListener(block : (obj : MapObject) -> Unit): MapObjectTapListener {
    val listener = MapObjectTapListener { mapObject, _ ->
        block.invoke(mapObject)
        return@MapObjectTapListener true
    }
    return listener
}


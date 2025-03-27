package kg.autojuuguch.automoikakg.extensions

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import com.yandex.mapkit.layers.GeoObjectTapEvent
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.asArgument
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kotlin.properties.ReadOnlyProperty

fun showCustomTabsBrowser(context: Context, url: String) {
    val pageUrl =
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            Uri.parse("https://$url")
        else Uri.parse(url)
    try {
        val customTabsIntent = CustomTabsIntent.Builder().apply {
            setStartAnimations(context, R.anim.browser_popup_enter, android.R.anim.fade_out)
            setExitAnimations(context, android.R.anim.fade_in, R.anim.browser_popup_exit)
        }.build()
        customTabsIntent.launchUrl(context, pageUrl)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Не удалось открыть страницу", Toast.LENGTH_SHORT).show()
    }
}

inline fun <reified F : Fragment> Fragment.setArgument(key: String, args: Any?): F {
    return (this as F).apply {
        arguments = Bundle(1).apply { putParcelable(key, args.asArgument()) }
    }
}

internal inline fun <reified T : Parcelable> parcelableArgument(name: String): ReadOnlyProperty<Fragment, T> {
    return object : ReadOnlyProperty<Fragment, T> {
        private var value: T? = null
        override fun getValue(thisRef: Fragment, property: kotlin.reflect.KProperty<*>): T {
            val data = BundleCompat.getParcelable(thisRef.requireArguments(), name, T::class.java)
            return value ?: requireNotNull(data) { "Arg $name is missing" }.also { value = it }
        }
    }
}
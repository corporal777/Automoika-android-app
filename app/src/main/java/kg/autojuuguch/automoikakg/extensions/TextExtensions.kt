package kg.autojuuguch.automoikakg.extensions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.utils.ClickableSpanNew
import java.util.Locale

fun SpannableString.setColorSpan(color: Int, context: Context): SpannableString {
    return this.apply {
        setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun TextView.getColorizedText(text: String, color: Int): CharSequence {
    return SpannableString(text).setColorSpan(color, context)
}

fun View.getStaticMapUrl(lat : String, lon : String): String {
    return "https://static-maps.yandex.ru/v1?lang=ru_RU" +
            "&size=600,450&z=15&maptype=map" +
            "&ll=$lon,$lat" + "&pt=$lon,$lat,pm2rdl" +
            "&apikey=" + context.getString(R.string.yandex_static_map_api)
}

fun String.getFileNameAndExtension(): Pair<String, String> {
    val extDotIndex = lastIndexOf(".")
    val name = if (extDotIndex == -1) this else substring(0, extDotIndex)
    val extension = substring(extDotIndex + 1, length).lowercase(Locale.getDefault())
    return name to extension
}

fun TextView.getPrivacyPoliticsText(): CharSequence {
    return SpannableString(context.getString(R.string.user_agreement_text)).apply {
        setSpan(
            ClickableSpanNew(this@getPrivacyPoliticsText) {
                showCustomTabsBrowser(context, context.getString(R.string.privacy_politics_url))
            }, 52, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun getSymbols(): String {
    return "\\@\\#\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>" +
            "\\•\\√\\π\\§\\∆\\£\\¢\\€\\¥\\°\\©\\®\\™\\✓\\[\\]"
}
package kg.autojuuguch.automoikakg.extensions

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.Transformation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.adapter.NoFilterArrayAdapter
import kotlin.math.abs

fun TextView.setLeftDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0)
}

fun TextView.setRightDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
}

fun AppCompatToggleButton.onCheckedChanged(onCheckedChanged: (checked: Boolean) -> Unit): CompoundButton.OnCheckedChangeListener {
    val listener = CompoundButton.OnCheckedChangeListener { p0, p1 -> onCheckedChanged(p1) }
    setOnCheckedChangeListener(listener)
    return listener
}

fun AppCompatCheckBox.onCheckedChanged(onCheckedChanged: (checked: Boolean) -> Unit): CompoundButton.OnCheckedChangeListener {
    val listener = CompoundButton.OnCheckedChangeListener { p0, p1 -> onCheckedChanged(p1) }
    setOnCheckedChangeListener(listener)
    return listener
}

fun AppCompatCheckBox.initChecked(
    isChecked: Boolean = false,
    onCheckedChanged: (checked: Boolean) -> Unit
) {
    setChecked(isChecked)
    onCheckedChanged(onCheckedChanged)
}

fun EditText.initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onAfterTextChanged(onTextChanged)
}

fun TextView.onAfterTextChanged(onTextChanged: (text: CharSequence?) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { onTextChanged(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    addTextChangedListener(watcher)
    return watcher
}

fun TextView.onTextChanged(onTextChanged: (text: CharSequence?) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s)
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

fun TextView.onFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit): View.OnFocusChangeListener {
    val watcher = View.OnFocusChangeListener { v, hasFocus -> onFocusChanged(hasFocus) }
    onFocusChangeListener = watcher
    return watcher
}

fun TextView.onActionDone(onTextChangeSearch: (text: CharSequence?) -> Unit) {
    setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            onTextChangeSearch.invoke(textView.text.toString())
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    })
}

fun <T> AppCompatAutoCompleteTextView.initDropDownAdapter(list: MutableList<T>) {
    keyListener = null
    setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, list))
}

fun AppBarLayout.offsetChangedListener(
    offsetChanged: (appBarLayout: AppBarLayout, offset: Int) -> Unit
): AppBarLayout.OnOffsetChangedListener {
    val listener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        offsetChanged(appBarLayout, verticalOffset)
    }
    addOnOffsetChangedListener(listener)
    return listener
}

fun NestedScrollView.onScrolled(onScrolled: (scrollY: Int, oldScrollY: Int, scrollX: Int, oldScrollX: Int) -> Unit): NestedScrollView.OnScrollChangeListener {
    val listener =
        NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrolled(scrollY, oldScrollY, scrollX, oldScrollX)
        }
    setOnScrollChangeListener(listener)
    return listener
}

fun NestedScrollView.onScroll(onScrolled: () -> Unit): NestedScrollView.OnScrollChangeListener {
    val listener = NestedScrollView.OnScrollChangeListener { v, _, _, _, _ -> onScrolled() }
    setOnScrollChangeListener(listener)
    return listener
}

fun RecyclerView.onScrolled(
    onScrolled: (dx: Int, dy: Int) -> Unit,
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = onScrolled(dx, dy)
    }
    addOnScrollListener(listener)
    return listener
}

fun RecyclerView.onScroll(
    onScrolled: () -> Unit,
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = onScrolled()
    }
    addOnScrollListener(listener)
    return listener
}

fun NestedScrollView.computeVerticalOffset(): Int {
    return computeVerticalScrollOffset()
}

fun RecyclerView.computeVerticalOffset(): Int {
    return computeVerticalScrollOffset()
}


fun ImageView.setImage(
    image: Any?, crossfade: Int = 300,
    transformations: List<Transformation>? = null,
    error: Int? = null,
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) { setParams(crossfade, transformations, error) }
        is Uri -> load(resImage) { setParams(crossfade, transformations, error) }
        is String -> load(resImage) { setParams(crossfade, transformations, error) }
        is Drawable -> load(resImage) { setParams(crossfade, transformations, error) }
        is Bitmap -> load(resImage) { setParams(crossfade, transformations, error) }
    }
}

fun ImageRequest.Builder.setParams(
    crossFade: Int = 300,
    transformations: List<Transformation>? = null,
    error: Int?
) {
    crossfade(crossFade)
    //if (placeholder != null) placeholder(placeholder)
    if (error != null) error(error)
    if (!transformations.isNullOrEmpty()) transformations(transformations)
    scale(Scale.FILL)
    diskCachePolicy(CachePolicy.ENABLED)
    listener(
        onStart = {},
        onCancel = {},
        onError = { _, _ -> }
    )
}

fun View.withLayoutParams(height: Int, width: Int): View {
    return this.apply { layoutParams = LinearLayout.LayoutParams(height, width) }
}

fun View.getDrawable(res: Int): Drawable? {
    return ContextCompat.getDrawable(context, res)
}

fun View.getColor(res: Int): Int {
    return ContextCompat.getColor(context, res)
}

fun View.getColorStateList(res: Int): ColorStateList? {
    return ContextCompat.getColorStateList(context, res)
}

var View.isVisibleAnim: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (isVisible == value) return
        if (value) {
            alpha = 0F
            visibility = View.VISIBLE
            animate().setDuration(500).alpha(1.0f)
        } else visibility = View.GONE
    }


var View.isVisibleFastAnim: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) {
            alpha = 0F
            visibility = View.VISIBLE
            animate().setDuration(200).alpha(1.0f)
        } else visibility = View.GONE
    }

var TextView.textColor: Int
    get() = currentTextColor
    set(value) {
        setTextColor(getColor(value))
    }

var View.topMargin: Int
    get() = marginTop
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (topMargin == value || marginTop == value) return
            this.topMargin = value
        }
    }

var MaterialCardView.cardShadow: Float
    get() = cardElevation
    set(value) {
        if (value == cardElevation) return
        else if (value <= 0f) cardElevation = 0f
        else {
            //val elevation = abs(value / 100f)
            //cardElevation = if (elevation <= 10f) elevation else 10f
            val elevation = abs(value / 20f)
            cardElevation = if (elevation <= 8f) elevation else 8f
        }
    }


fun ViewGroup.setBackgroundSearch(it: Boolean) {
    setBackgroundResource(if (it) R.drawable.background_search_focused else R.drawable.background_search_normal)
}

fun ViewGroup.setBackgroundInput(it: Boolean) {
    setBackgroundResource(if (it) R.drawable.background_input_focused else R.drawable.background_input_normal)
}

var ImageView.tint: Int
    get() = imageTintList?.defaultColor ?: 0
    set(value) {
        if (value == imageTintList?.defaultColor) return
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, value))
    }

var Activity.statusBarColorValue: Int
    get() = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    set(value) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (value == getSystemBarAppearance()) return
            else setSystemBarsAppearance(value, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            if (window.decorView.systemUiVisibility == value) return
            else window.decorView.systemUiVisibility = value
        }
    }

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.setSystemBarsAppearance(start: Int, end: Int) {
    window.insetsController?.setSystemBarsAppearance(start, end)
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.getSystemBarAppearance(): Int? {
    return window.insetsController?.systemBarsAppearance
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.setSystemBarsAppearance(start: Int) {
    window.insetsController?.setSystemBarsAppearance(start, start)
}

var PlacemarkMapObject.icon: Bitmap?
    get() = null
    set(value) {
        setIcon(ImageProvider.fromBitmap(value))
    }

fun TextView.changeTitleTextColor(show: Boolean) {
    textColor = if (show) R.color.title_text_error_red else R.color.input_title_text_color
}

fun View.setClickListener(block: (v: View) -> Unit) {
    setOnClickListener {
        hideKeyboard(context, it)
        block.invoke(it)
    }
}
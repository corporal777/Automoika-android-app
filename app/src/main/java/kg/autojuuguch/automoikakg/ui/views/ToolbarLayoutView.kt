package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.card.MaterialCardView
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.LayoutLoadingButtonBinding
import kg.autojuuguch.automoikakg.databinding.LayoutToolbarBinding
import kg.autojuuguch.automoikakg.extensions.cardShadow
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.getDrawable
import kotlin.math.abs

class ToolbarLayoutView : AppBarLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarLayoutView)
        val buttonTextSize = a.getDimensionPixelSize(R.styleable.ToolbarLayoutView_toolbarTextSize, resources.getDimensionPixelSize(R.dimen.toolbar_title_text_size))
        val buttonText = a.getText(R.styleable.ToolbarLayoutView_toolbarText) ?: ""

        a.recycle()
        initToolbarLayout(buttonTextSize, buttonText,)
    }

    private fun initToolbarLayout(buttonTextSize: Int, buttonText: CharSequence?, ) {
        binding.tvToolbarTitle.apply {
            text = buttonText
            setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize.toFloat())
        }
    }

    private val binding = LayoutToolbarBinding.inflate(LayoutInflater.from(context), this, true)


    init {

    }


    fun setToolbarTitle(text : CharSequence){
        binding.tvToolbarTitle.text = text
    }

    fun getBackButton() = binding.ivBack
    fun setToolbarShadow(offset : Int){
        if (offset.toFloat() == elevation) return
        else if (offset.toFloat() <= 0f) elevation = 0f
        else {
            val shadow = abs(offset / 20f)
            elevation = if (shadow <= 8f) shadow else 8f
        }
    }
}
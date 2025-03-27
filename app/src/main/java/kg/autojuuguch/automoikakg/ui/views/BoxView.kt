package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginTop
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.getDrawable

class BoxView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val params = LinearLayout.LayoutParams(24.dp, 24.dp)

    init {
        textSize = 15f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }


    fun setData(boxNum: String, isContains: Boolean): BoxView {
        return this.apply {
            viewTopMargin = if (boxNum.toInt() > 1) 10.dp else 0.dp
            text = boxNum
            changeBackground(isContains)
        }
    }

    fun changeBackground(isContains: Boolean){
        background = if (isContains) getDrawable(R.drawable.view_box_background_green)
        else getDrawable(R.drawable.view_box_background_red)
    }

    private var View.viewTopMargin: Int
        get() = marginTop
        set(value) {
            params.setMargins(value, 0, 0, 0)
            setLayoutParams(params);
        }
}
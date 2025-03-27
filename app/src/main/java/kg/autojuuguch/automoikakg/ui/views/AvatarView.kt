package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.getColor
import kg.autojuuguch.automoikakg.extensions.px
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.extensions.textColor

class AvatarView : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var textViewSize = 0f
    private var imageBitmap: Bitmap? = null

    private val imageView = ImageView(context).apply {
        setColorFilter(getColor(R.color.blue_dark_avatar_gradient))
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private val textView = TextView(context).apply {
        isVisible = imageBitmap == null
        setTypeface(ResourcesCompat.getFont(context, R.font.sf_pro_text_semibold))
        letterSpacing = -0.01f
        text = "Нет фото"
        textColor = R.color.white
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
    }

    init {
        removeAllViews()
        addView(imageView)
        addView(textView)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (textViewSize <= 0f && width != 0) {
            textViewSize = if (width.px > 100) 14f else 12f
            textView.textSize = textViewSize
        }
    }

    fun setImage(uri: Bitmap?) {
        imageBitmap = uri
        textView.isVisible = imageBitmap == null
        imageView.apply {
            if (imageBitmap == null) setColorFilter(getColor(R.color.blue_dark_avatar_gradient))
            else setColorFilter(getColor(R.color.trans))
            setImage(imageBitmap, 200, error = R.drawable.avatar_placeholder_rectangle)
        }
    }
}
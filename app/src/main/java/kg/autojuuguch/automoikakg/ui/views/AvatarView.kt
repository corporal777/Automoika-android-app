package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.Argument
import kg.autojuuguch.automoikakg.data.asArgument
import kg.autojuuguch.automoikakg.extensions.getColor
import kg.autojuuguch.automoikakg.extensions.px
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.extensions.textColor
import java.util.Optional

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {


    private var textViewSize = 0f
    private var image: Any? = null

    private val imageView = ImageView(context).apply {
        setColorFilter(getColor(R.color.blue_dark_avatar_gradient))
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    private val textView = TextView(context, null, 0, R.style.TextStyle_Semibold).apply {
        isVisible = image != null
        text = "Нет фото"
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply { gravity = Gravity.CENTER }
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


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("image", image.asArgument())
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        val image = bundle.getParcelable<Argument<*>>("image")
        setImage(image?.value)
        super.onRestoreInstanceState(state.getParcelable("instanceState"))
    }


    fun setImage(uri: Any?) {
        image = uri
        textView.isVisible = image == null
        imageView.apply {
            if (image == null) setColorFilter(getColor(R.color.blue_dark_avatar_gradient))
            else setColorFilter(getColor(R.color.trans))
            setImage(uri, 200, error = R.drawable.avatar_placeholder_rectangle)
        }
    }
}
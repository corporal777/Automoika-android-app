package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.LayoutLoadingButtonBinding
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.getColor
import kg.autojuuguch.automoikakg.extensions.getColorStateList
import kg.autojuuguch.automoikakg.extensions.getDrawable
import kg.autojuuguch.automoikakg.extensions.isVisibleFastAnim
import kg.autojuuguch.automoikakg.utils.LOG_TAG

class LoadingButton : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }


    private var canChangeAlpha = true
    private var isProgressVisible = false
    private val binding = LayoutLoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        val buttonBack = a.getDrawable(R.styleable.LoadingButton_buttonBackground)
        val buttonMinHeight = a.getDimensionPixelSize(
            R.styleable.LoadingButton_buttonMinHeight,
            R.dimen.action_button_min_height
        )
        val buttonTextColor = a.getColor(R.styleable.LoadingButton_buttonTextColor, Color.WHITE)
        val buttonTextSize = a.getDimensionPixelSize(
            R.styleable.LoadingButton_buttonTextSize,
            resources.getDimensionPixelSize(R.dimen.action_button_text_size)
        )
        val buttonText = a.getText(R.styleable.LoadingButton_buttonText) ?: ""
        val buttonPadding =
            a.getDimensionPixelSize(R.styleable.LoadingButton_buttonPaddingHorizontal, 0.dp)
        val buttonDrawable = a.getDrawable(R.styleable.LoadingButton_buttonDrawable)

        a.recycle()
        binding.flAction.apply {
            background = buttonBack
            minimumHeight = buttonMinHeight
            setPadding(buttonPadding, 0, buttonPadding, 0)
        }
        binding.tvAction.apply {
            setText(buttonText)
            setTextColor(buttonTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize.toFloat())
        }
        binding.ivAction.apply {
            isVisible = buttonDrawable != null
            setImageDrawable(buttonDrawable)
        }
        binding.progressLoad.apply {
            isVisible = false
            setProgressColor(buttonTextColor)
            setSize(25.dp)
        }
    }


    fun setText(buttonText: CharSequence) = run { binding.tvAction.text = buttonText }
    fun setTextColor(textColor: Int) = binding.tvAction.setTextColor(textColor)
    fun setButtonBackground(back: Int) = run { background = getDrawable(context, back) }
    fun setProgressColor(color: Int) = run { binding.progressLoad.setProgressColor(getColor(color)) }


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (canChangeAlpha) alpha = if (enabled) 1.0f else 0.5f
    }

    override fun setSelected(selected: Boolean) {
        if (canChangeAlpha) alpha = if (selected) 1.0f else 0.5f
    }

    fun showProgressLoading(show: Boolean) {
        if (isProgressVisible == show) return
        isProgressVisible = show
        binding.apply {
            progressLoad.isVisibleFastAnim = show
            tvAction.isVisibleFastAnim = !show

            canChangeAlpha = !show
            isEnabled = !show
        }
    }

    var LoadingButton.isProgressShown: Boolean
        get() = isProgressVisible
        set(value) {
            if (isProgressVisible == value) return
            isProgressVisible = value
            binding.apply {
                progressLoad.isVisibleFastAnim = value
                tvAction.isVisibleFastAnim = !value

                canChangeAlpha = !value
                isEnabled = !value
            }
        }

}
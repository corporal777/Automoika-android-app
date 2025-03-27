package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.view.isVisible
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.LayoutEditTextInputBinding
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onFocusChanged
import kg.autojuuguch.automoikakg.extensions.setBackgroundInput
import kg.autojuuguch.automoikakg.utils.LOG_TAG

class CustomTextInputView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var generatedId : Int = generateViewId()
    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputView)
        val hintText = a.getText(R.styleable.CustomTextInputView_hint)
        val type = a.getInt(R.styleable.CustomTextInputView_inputType, InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        val maxLines = a.getInt(R.styleable.CustomTextInputView_maxLines, 1)
        val minLines = a.getInt(R.styleable.CustomTextInputView_minLines, 1)
        val textColor = a.getColor(R.styleable.CustomTextInputView_textColor, Color.BLACK)
        val inputId = a.getResourceId(R.styleable.CustomTextInputView_inputId, generatedId)

        a.recycle()
        binding.etLayout.apply {
            id = inputId
            hint = hintText
            setTextColor(textColor)
            setMinMaxLines(minLines, maxLines)
            inputType = type
        }
        binding.passwordToggle.apply {
            isVisible = isPasswordInputType(type)
            setOnCheckedChangeListener { _, isChecked ->
                binding.etLayout.showHidePasswordText(isChecked)
            }
        }
    }

    private val binding = LayoutEditTextInputBinding.inflate(LayoutInflater.from(context), this, true)


    private var onTextChanged: (text: String?) -> Unit = {}
    private var onFocused: (focused: Boolean) -> Unit = {}

    init {
        binding.apply {
            etLayout.apply {
                onAfterTextChanged { onTextChanged.invoke(it.toString()) }
                onFocusChanged { hasFocus -> flInput.setBackgroundInput(hasFocus) }
            }
        }
    }

    fun onAfterTextChanged(onTextChanged: (text: CharSequence?) -> Unit){
        this.onTextChanged = onTextChanged
    }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        binding.etLayout.setText(text ?: "")
        this.onTextChanged = onTextChanged
    }

    fun setText(text: String?) = binding.etLayout.setText(text)
    fun setHint(text: String?) = binding.etLayout.setHint(text)


    private fun setMinMaxLines(min : Int, max : Int){
        binding.etLayout.apply {
            if (min > 1) {
                gravity = Gravity.START and Gravity.TOP
                setPadding(0, 10.dp, 0, 10.dp)
            } else setPadding(0, 0, 0, 0)
            minLines = min
            maxLines = max
        }
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        return (variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    private fun EditText.showHidePasswordText(show: Boolean) {
        if (!show) this.transformationMethod = PasswordTransformationMethod()
        else this.transformationMethod = null
        this.setSelection(this.length());
    }
}
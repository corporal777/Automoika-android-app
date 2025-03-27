package kg.autojuuguch.automoikakg.ui.views

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.LayoutPasswordViewBinding
import kg.autojuuguch.automoikakg.extensions.changeTitleTextColor
import kg.autojuuguch.automoikakg.extensions.getSymbols
import kg.autojuuguch.automoikakg.extensions.onAfterTextChanged
import kg.autojuuguch.automoikakg.extensions.onFocusChanged
import kg.autojuuguch.automoikakg.extensions.onTextChanged
import kg.autojuuguch.automoikakg.extensions.setBackgroundInput
import kg.autojuuguch.automoikakg.extensions.textColor
import java.nio.charset.Charset

class PasswordView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var binding =
        LayoutPasswordViewBinding.inflate(LayoutInflater.from(context), this, true)


    private val defaultTypeFace = ResourcesCompat.getFont(context, R.font.sf_pro_text)
    private val boldTypeFace = ResourcesCompat.getFont(context, R.font.sf_pro_text_semibold)
    private val symbols = getSymbols()

    private var password: CharSequence? = null
    private var isUncaughtSymbolsUsed = false
    private var isPasswordValid = false

    private var onPasswordValid: (password: PasswordModel) -> Unit = {}

    init {
        binding.apply {
            toggleOne.apply {
                isVisible = !password.isNullOrEmpty()
                setOnCheckedChangeListener { _, c ->
                    if (!c) etPasswordOne.transformationMethod = PasswordTransformationMethod()
                    else etPasswordOne.transformationMethod = null
                    etPasswordOne.setSelection(etPasswordOne.length())
                }
            }
        }
        initPasswordField()
        validatePassword(password)
    }


    private fun initPasswordField() {
        binding.apply {
            etPasswordOne.apply {
                onFocusChanged { hasFocus ->
                    tilPasswordOne.setBackgroundInput(hasFocus)
                    if (hasFocus) validatePassword(password)
                }
                onAfterTextChanged {
                    password = it.toString()
                    validatePassword(password)
                    toggleOne.isVisible = !password.isNullOrEmpty()
                }

            }
        }

    }

    private fun validatePassword(password: CharSequence?) {
        val isLengthValid = (password?.length ?: 0) >= MIN_LENGTH

        val isUpperCaseLettersValid = password.matchesLettersRegister(".*[A-Z].*")
        val isUnderCaseLettersValid = password.matchesLettersRegister(".*[a-z].*")

        val isLettersValid = isUpperCaseLettersValid && isUnderCaseLettersValid
        val isNumbersValid = password?.matches(Regex(".*\\d.*")) == true

        isUncaughtSymbolsUsed = if (password.isNullOrEmpty()) false
        else if (password.contains(Regex("[$symbols]"))) true
        else if (!Charset.forName("US-ASCII").newEncoder().canEncode(password)) true
        else false


        binding.apply {
            tvErrorLength.changeTextColorError(isLengthValid)
            tvErrorLetters.apply {
                if (isUpperCaseLettersValid && !isUnderCaseLettersValid)
                    text = context.getString(R.string.password_small_letters_error)
                else if (!isUpperCaseLettersValid && isUnderCaseLettersValid)
                    text = context.getString(R.string.password_big_letters_error)
                else text = context.getString(R.string.password_big_small_letters_error)

                changeTextColorError(isLettersValid)
            }
            tvErrorNumbers.changeTextColorError(isNumbersValid)
            lnErrorDescription.isVisible = !isUncaughtSymbolsUsed
        }
        binding.tvErrorUncaughtSymbols.apply {
            text = context.getString(R.string.used_unacceptable_symbols)
            isVisible = isUncaughtSymbolsUsed
        }

        isPasswordValid = !isUncaughtSymbolsUsed && isLengthValid && isLettersValid && isNumbersValid
        onPasswordValid(getPasswordModel())
    }



    private fun TextView.changeTextColorError(isValid: Boolean) {
        typeface = if (isValid) boldTypeFace else defaultTypeFace
        textColor =
            if (isValid) R.color.password_errors_text_color_valid
            else R.color.password_errors_text_color
    }

    fun setPasswords(password: String?) = binding.etPasswordOne.setText(password)

    private fun getPasswordModel(): PasswordModel {
        return PasswordModel(isPasswordValid, password.toString())
    }

    private fun CharSequence?.matchesLettersRegister(regex: String): Boolean {
        return if (this == "null" || this.isNullOrEmpty()) false else this.matches(Regex(regex))
    }

    fun onPasswordChanged(block: (password: PasswordModel) -> Unit): PasswordView {
        onPasswordValid = block
        return this
    }

    companion object {
        const val MIN_LENGTH = 8
    }
}

data class PasswordModel(
    val isValid: Boolean,
    val password: String?
)
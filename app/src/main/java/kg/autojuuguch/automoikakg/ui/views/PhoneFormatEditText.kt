package kg.autojuuguch.automoikakg.ui.views

import android.R.attr.end
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.extensions.onFocusChanged
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.Utils.formatMobilePhone
import kg.autojuuguch.automoikakg.utils.Utils.isContainsNumbers


class PhoneFormatEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PhoneFormatEditText)
        val hintText = a.getText(R.styleable.PhoneFormatEditText_phoneHint)
        a.recycle()
        setPhoneHint(hintText)
    }

    private var onInputFocusChanged: (hasFocus: Boolean) -> Unit = {}
    private var onInputTextChanged: (text: CharSequence?) -> Unit = {}

    private val mask: String = "+996"
    private var editingOnChanged = false
    private var editingAfter = false
    private var textSelection = 0
    private var formattedText = mask

    init {
        onFocusChanged { hasFocus ->
            onInputFocusChanged.invoke(hasFocus)
            if (hasFocus) setText(makeMaskedText(formattedText))
            else {
                if (formattedText.isBlank() || formattedText == mask) text = null
            }
        }
        doOnTextChanged { text, start, before, count ->
            if (!editingOnChanged && hasFocus()) editingOnChanged = true
        }
        doAfterTextChanged {
            if (!editingAfter && editingOnChanged) {
                editingAfter = true
                setText(makeMaskedText(it.toString()))
                setSelection(textSelection)
                editingAfter = false
                editingOnChanged = false
            }
            onInputTextChanged.invoke(clearPhoneText(text.toString()))
        }
    }


    private fun makeText(text: String): String {
        if (text.isEmpty()) return ""
        else {
            val str = text.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")

            val makeText = if (str.length >= 17) {
                val textWithoutMask = str.replace("+996", "")
                if (textWithoutMask.length > 13) textWithoutMask.substring(0, 13)
                else textWithoutMask
            } else if (str.length in 14..16){
                val textWithoutMask = str.replace("+996", "")
                if (textWithoutMask.length > 9){
                    if (textWithoutMask.first().toString() == "0"){
                        textWithoutMask.substring(1, 10)
                    } else textWithoutMask.substring(0, 9)
                } else textWithoutMask
            } else str

            return makeText
        }
    }

    private fun makeMaskedText(text: String): String {
        formattedText = makeText(text)

        if (formattedText.isNullOrBlank()) {
            formattedText = mask
            textSelection = mask.length
        } else if (formattedText.length < mask.length || formattedText == mask) {
            formattedText = mask
            textSelection = mask.length
        } else if (formattedText.length > mask.length && formattedText.contains(mask)) {
            formattedText = formatPhoneText(formattedText)
            textSelection = formattedText.length
        } else {
            formattedText = formatPhoneText(mask + formattedText)
            textSelection = formattedText.length
        }
        return formattedText
    }

    fun setPhoneHint(text: CharSequence?) {
        hint =
            if (isContainsNumbers(text.toString())) formatMobilePhone(text.toString())
            else text
    }

    fun setPhoneText(text: CharSequence?) {
        if (!text.isNullOrBlank()) {
            setText(formatPhoneText(text.toString()))
            formattedText = formatPhoneText(text.toString())
        }
    }

    private fun clearPhoneText(text: String): String {
        if (text.isNullOrEmpty()) return ""
        else {
            val str = text.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
            if (str.length > 13) return str.substring(0, 13)
            else return str
        }
    }

    private fun formatPhoneText(text: String?): String {
        if (text.isNullOrBlank()) return ""
        else {
            return if (text.length <= 7) StringBuilder(text).insert(4, " ").toString()
            else if (text.length in 8..10)
                StringBuilder(text)
                    .insert(4, " ")
                    .insert(8, " ").toString()
            else StringBuilder(text)
                .insert(4, " ")
                .insert(8, " ")
                .insert(12, " ").toString()


//            return if (text.length <= 5) StringBuilder(text).insert(2, " (").toString()
//            else if (text.length in 5..8)
//                StringBuilder(text)
//                    .insert(2, " (")
//                    .insert(7, ") ").toString()
//            else if (text.length in 8..10)
//                StringBuilder(text)
//                    .insert(2, " (")
//                    .insert(7, ") ")
//                    .insert(12, "-").toString()
//            else StringBuilder(text)
//                .insert(2, " (")
//                .insert(7, ") ")
//                .insert(12, "-")
//                .insert(15, "-").toString()
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        val text: CharSequence? = text
        if (text != null) {
            if (selStart == text.length || selEnd != text.length) {
                setSelection(text.length, text.length)
                return
            }
        }
        super.onSelectionChanged(selStart, selEnd)
    }


    fun onInputFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit) {
        this.onInputFocusChanged = onFocusChanged
    }

    fun onInputTextChanged(onTextChanged: (text: CharSequence?) -> Unit) {
        this.onInputTextChanged = onTextChanged
    }
}
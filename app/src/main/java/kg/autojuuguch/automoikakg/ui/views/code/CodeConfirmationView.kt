package kg.autojuuguch.automoikakg.ui.views.code

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Space
import androidx.core.view.children
import androidx.core.view.postDelayed
import kg.autojuuguch.automoikakg.R

class CodeConfirmationView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CodeConfirmationView)
        codeLength = a.getInt(R.styleable.CodeConfirmationView_codeViewLength, DEFAULT_CODE_LENGTH)
        style = CodeConfirmationViewUtils.getFromAttributes(context, a)
        a.recycle()
    }

    var codeLength = DEFAULT_CODE_LENGTH

    private var enteredCode: String = ""
        set(value) {
            val digits = value.digits()
            field = digits
            onCodeChanged.invoke(CodeViewData(enteredCode, digits.length == codeLength))

            updateState()
        }

    private var onCodeChanged: (code: CodeViewData) -> Unit = {}

    internal var style: Style? = null
        set(value) {
            if (field == value) return
            field = value
            removeAllViews()
            updateState()
        }

    private val symbolSubviews: Sequence<SymbolView>
        get() = children.filterIsInstance<SymbolView>()

    init {
        orientation = HORIZONTAL
        isFocusable = true
        isFocusableInTouchMode = true

        setOnClickListener {
            if (requestFocus()) showKeyboard()
        }
    }

    private fun updateState() {
        val codeLengthChanged = codeLength != symbolSubviews.count()
        if (codeLengthChanged) setupSymbolSubviews()

        val viewCode = symbolSubviews.map { it.state.symbol }
            .filterNotNull()
            .joinToString(separator = "")

        val isViewCodeOutdated = enteredCode != viewCode
        if (isViewCodeOutdated) {
            symbolSubviews.forEachIndexed { index, view ->
                view.state = SymbolView.State(
                    symbol = enteredCode.getOrNull(index),
                    isActive = (enteredCode.length == index)
                )
            }
        }
    }

    private fun setupSymbolSubviews() {
        removeAllViews()

        if (style != null){
            for (i in 0 until codeLength) {
                val symbolView = SymbolView(context, style!!.symbolViewStyle)
                symbolView.state = SymbolView.State(isActive = (i == enteredCode.length))
                addView(symbolView)

                if (i < codeLength.dec()) {
                    val space = Space(context).apply {
                        layoutParams = ViewGroup.LayoutParams(style!!.symbolsSpacing, 0)
                    }
                    addView(space)
                }
            }
        }
    }

    fun getCode() = enteredCode

    fun showError(show : Boolean){
        symbolSubviews.forEach {
            it.isError = show
        }
    }

    fun setCodeAutoFill(code : String){
        if (code.length < DEFAULT_CODE_LENGTH) return
        enteredCode = code
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setOnKeyListener { _, keyCode, event -> handleKeyEvent(keyCode, event) }
        postDelayed(KEYBOARD_AUTO_SHOW_DELAY) {
            requestFocus()
            showKeyboard()
        }
    }

    private fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean = when {
        event.action != KeyEvent.ACTION_DOWN -> false
        event.isDigitKey() -> {
            val enteredSymbol = event.keyCharacterMap.getNumber(keyCode)
            appendSymbol(enteredSymbol)
            true
        }

        event.keyCode == KeyEvent.KEYCODE_DEL -> {
            removeLastSymbol()
            true
        }

        event.keyCode == KeyEvent.KEYCODE_ENTER -> {
            hideKeyboard()
            true
        }

        else -> false
    }

    private fun KeyEvent.isDigitKey(): Boolean {
        return keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9
    }

    private fun appendSymbol(symbol: Char) {
        if (enteredCode.length == codeLength) return
        this.enteredCode += symbol
    }

    private fun removeLastSymbol() {
        if (enteredCode.isEmpty()) return
        this.enteredCode = enteredCode.substring(0, enteredCode.length - 1)
    }

    override fun onCheckIsTextEditor(): Boolean = true

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        with(outAttrs) {
            inputType = InputType.TYPE_CLASS_NUMBER
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
        return object : InputConnectionWrapper(BaseInputConnection(this, false), true) {

            override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
                return if (beforeLength == 1 && afterLength == 0) {
                    sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                } else super.deleteSurroundingText(beforeLength, afterLength)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        return SavedState(superState, enteredCode)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        enteredCode = state.enteredCode
    }

    private class SavedState(
        superState: Parcelable?,
        val enteredCode: String
    ) : BaseSavedState(superState) {

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(enteredCode)
        }
    }

    internal data class Style(
        val codeLength: Int,
        val isPasteEnabled: Boolean,
        val symbolsSpacing: Int,
        val symbolViewStyle: SymbolView.Style
    )

    data class CodeViewData(
        val code: String,
        val isComplete: Boolean
    )

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun String.digits(): String = filter { char -> char.isDigit() }

    fun doOnCodeChanged(onTextChanged: (text: CodeViewData) -> Unit) {
        this.onCodeChanged = onTextChanged
    }

    companion object {
        internal const val DEFAULT_CODE_LENGTH = 4
        internal const val KEYBOARD_AUTO_SHOW_DELAY = 500L
    }
}
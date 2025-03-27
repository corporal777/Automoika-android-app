package kg.autojuuguch.automoikakg.ui.views.code

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.Log
import android.util.Size
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import kg.autojuuguch.automoikakg.R

private const val textPaintAlphaAnimDuration = 25L
private const val borderPaintAlphaAnimDuration = 150L

private const val cursorAlphaAnimDuration = 500L
private const val cursorAlphaAnimStartDelay = 200L

private const val cursorSymbol = "|"

@SuppressLint("ViewConstructor")
internal class SymbolView(context: Context, private val symbolStyle: Style) : View(context) {

    data class State(
        val symbol: Char? = null,
        val isActive: Boolean = false
    )

    var isError = false
        set(value) {
            if (field == value) return
            field = value
            updateState(state)
        }

    var state: State = State()
        set(value) {
            if (field == value) return
            field = value
            updateState(state)
        }

    private val showCursor: Boolean = symbolStyle.showCursor
    private val desiredW: Int = symbolStyle.width
    private val desiredH: Int = symbolStyle.height
    private val textSizePx: Int = symbolStyle.textSize
    private val cornerRadius: Float = symbolStyle.borderCornerRadius

    private val backgroundPaint: Paint = Paint().apply {
        color = symbolStyle.backgroundColor
        style = Paint.Style.FILL
    }

    private val borderPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = symbolStyle.borderColor
        style = Paint.Style.STROKE
        strokeWidth = symbolStyle.borderWidth.toFloat()
    }
    private val textPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = symbolStyle.textColor
        textSize = textSizePx.toFloat()
        typeface = ResourcesCompat.getFont(context, R.font.sf_pro_text)
        textAlign = Paint.Align.CENTER
    }

    private var textSize: Size = calculateTextSize('0')

    private val backgroundRect = RectF()

    private var textAnimator: Animator? = null

    @Suppress("SameParameterValue")
    private fun calculateTextSize(symbol: Char): Size {
        val textBounds = Rect()
        textPaint.getTextBounds(symbol.toString(), 0, 1, textBounds)
        return Size(textBounds.width(), textBounds.height())
    }

    private fun updateState(state: State) = with(state) {
        textAnimator?.cancel()
        if (symbol == null && isActive && showCursor) {
            backgroundPaint.color = symbolStyle.backgroundColorActive
            borderPaint.strokeWidth = symbolStyle.borderWidthActive.toFloat()
            textPaint.color = symbolStyle.borderColorActive

            textAnimator = ObjectAnimator.ofInt(textPaint, "alpha", 255, 255, 0, 0)
                .apply {
                    duration = cursorAlphaAnimDuration
                    startDelay = cursorAlphaAnimStartDelay
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                    addUpdateListener { invalidate() }
                }
            animateBorderColor(symbolStyle.borderColor, symbolStyle.borderColorActive)
        }
        else if (symbol != null && !isActive && isError) {
            animateBorderColor(symbolStyle.borderColorEntered, symbolStyle.borderColorError)
        }
        else if (symbol != null && !isActive){
            backgroundPaint.color = symbolStyle.backgroundColorActive
            borderPaint.strokeWidth = symbolStyle.borderWidth.toFloat()
            textPaint.color = symbolStyle.textColor

            val startAlpha = 127
            val endAlpha = 255
            textAnimator = ObjectAnimator.ofInt(textPaint, "alpha", startAlpha, endAlpha)
                .apply {
                    duration = textPaintAlphaAnimDuration
                    addUpdateListener { invalidate() }
                }
            animateBorderColor(symbolStyle.borderColorActive, symbolStyle.borderColorEntered)
        } else {
            backgroundPaint.color = symbolStyle.backgroundColor
            borderPaint.strokeWidth = symbolStyle.borderWidth.toFloat()
            textPaint.color = symbolStyle.textColor

            val startAlpha = if (symbol == null) 255 else 127
            val endAlpha = if (symbol == null) 0 else 255
            textAnimator = ObjectAnimator.ofInt(textPaint, "alpha", startAlpha, endAlpha)
                .apply {
                    duration = textPaintAlphaAnimDuration
                    addUpdateListener { invalidate() }
                }
            animateBorderColor(symbolStyle.borderColorActive, symbolStyle.borderColor)
        }

        textAnimator?.start()
        //animateBorderColorChange(isActive)
    }

    private fun animateBorderColor(colorFrom : Int, colorTo : Int) {
        if (colorFrom == colorTo) return
        ObjectAnimator.ofObject(borderPaint, "color", ArgbEvaluator(), colorFrom, colorTo)
            .apply {
                duration = borderPaintAlphaAnimDuration
                addUpdateListener { invalidate() }
            }.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(desiredW, widthMeasureSpec, 0)
        val h = resolveSizeAndState(desiredH, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val borderWidthHalf = borderPaint.strokeWidth / 2
        backgroundRect.left = borderWidthHalf
        backgroundRect.top = borderWidthHalf
        backgroundRect.right = measuredWidth.toFloat() - borderWidthHalf
        backgroundRect.bottom = measuredHeight.toFloat() - borderWidthHalf
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        canvas.drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            borderPaint
        )

        canvas.drawText(
            if (state.isActive && showCursor) cursorSymbol else state.symbol?.toString() ?: "",
            backgroundRect.width() / 2 + borderPaint.strokeWidth / 2,
            backgroundRect.height() / 2 + textSize.height / 2 + borderPaint.strokeWidth / 2,
            textPaint
        )
    }

    data class Style(
        val showCursor: Boolean,
        @Px val width: Int,
        @Px val height: Int,
        @ColorInt val backgroundColor: Int,
        @ColorInt val backgroundColorActive: Int,
        @ColorInt val borderColor: Int,
        @ColorInt val borderColorActive: Int,
        @ColorInt val borderColorEntered: Int,
        @ColorInt val borderColorError: Int,
        @Px val borderWidthActive: Int,
        @Px val borderWidth: Int,
        val borderCornerRadius: Float,
        @ColorInt val textColor: Int,
        @Px val textSize: Int,
        val typeface: Typeface = Typeface.DEFAULT_BOLD
    )
}
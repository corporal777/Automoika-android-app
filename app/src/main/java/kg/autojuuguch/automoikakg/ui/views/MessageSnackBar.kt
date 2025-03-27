package kg.autojuuguch.automoikakg.ui.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFade
import kg.autojuuguch.automoikakg.databinding.LayoutMessageSnackBarBinding
import kg.autojuuguch.automoikakg.extensions.dp
import kg.autojuuguch.automoikakg.extensions.px

class MessageSnackBar : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = LayoutMessageSnackBarBinding.inflate(LayoutInflater.from(context), this)


    private var topY = 0f
    init {
        topY = if (top == 0) 1954f else top.toFloat()
        translationY = topY
    }

    fun setText(text: CharSequence?) = binding.tvMessage.setText(text)

    fun setIcon(icon: Int) = binding.ivIcon.setImageResource(icon)


//    fun show(root : ViewGroup, view: View){
//        val materialFade = MaterialFade().apply { duration = 150L }
//        TransitionManager.beginDelayedTransition(root, materialFade)
//        visibility = View.VISIBLE
//    }
//
//    fun hide(root : ViewGroup){
//        val materialFade = MaterialFade().apply { duration = 84L }
//        TransitionManager.beginDelayedTransition(root, materialFade)
//        visibility = View.GONE
//    }



    fun hide() {
        ObjectAnimator.ofFloat(this, "translationY", topY).apply {
            duration = 650
            start()
            addListener(onEnd = { visibility = View.GONE }, onCancel = { visibility = View.GONE })
        }
    }

    fun show() {
        visibility = View.VISIBLE
        ObjectAnimator.ofFloat(this, "translationY", 0f).apply {
            duration = 700
            start()
        }
    }
}
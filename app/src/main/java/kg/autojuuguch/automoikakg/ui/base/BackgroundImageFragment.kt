package kg.autojuuguch.automoikakg.ui.base

import android.graphics.drawable.Drawable

interface BackgroundImageFragment {
    val isLightStatus: Boolean
    fun getFragmentBackgroundDrawable(): Drawable?
}
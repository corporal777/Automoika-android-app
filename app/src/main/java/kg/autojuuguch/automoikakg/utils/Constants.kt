package kg.autojuuguch.automoikakg.utils

import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi

val SYSTEM_UI_LIGHT_STATUS_BAR =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) APPEARANCE_LIGHT_STATUS_BARS
    else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

@RequiresApi(Build.VERSION_CODES.R)
const val SYSTEM_UI_LIGHT_NAV_BAR = APPEARANCE_LIGHT_NAVIGATION_BARS


const val LOG_TAG = "REQUEST INFO"
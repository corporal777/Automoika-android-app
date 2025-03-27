package kg.autojuuguch.automoikakg.data.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CodeBody(
    val phone: String,
    val code: String
) : Parcelable
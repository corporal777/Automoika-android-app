package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoginModel(
    val id: String,
    val accountType: String,
    val login: String
) : Parcelable
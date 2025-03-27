package kg.autojuuguch.automoikakg.data.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginBody(
    val login: String,
    val password : String
) : Parcelable
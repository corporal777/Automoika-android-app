package kg.autojuuguch.automoikakg.data.body

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleUserBody(
    val id : String? = "",
    var name: String? = "",
    var lastName: String? = "",
) : Parcelable
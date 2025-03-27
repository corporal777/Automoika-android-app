package kg.autojuuguch.automoikakg.data.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class LocationBody(
    val lat: String,
    val lon: String
) : Parcelable
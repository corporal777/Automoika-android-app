package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityLocationModel(
    val city: String,
    val lat: String,
    val lon: String
) : Parcelable
package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YandexGeoModel(
    val fullAddress : String?,
    val country : String?,
    val city : String?,
    val street : String?,
    val district : String?,
    var latitude : Double? = null,
    var longitude : Double? = null
): Parcelable {

    fun getGeoCity() : String? {
        return city?.trim()
    }

    fun getGeoStreet(): String? {
        return street?.replace("улица", "")?.trim()
    }

    fun getGeoDistrict(): String? {
        return district?.replace("район", "")?.trim()
    }
}


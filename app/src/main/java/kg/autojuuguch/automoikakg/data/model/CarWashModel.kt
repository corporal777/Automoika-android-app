package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.yandex.mobile.ads.nativeads.NativeAd
import kg.autojuuguch.automoikakg.BuildConfig
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CarWashModel(
    val id: String,
    val name: String? = null,
    @SerializedName("backgroundImage")
    val image: String? = null,
    val address: CarWashAddressModel? = null,
    val boxes: CarWashBoxesModel? = null,
    val owner: String? = null,
    val favourites : List<String>? = null,
    var additionalData :  CarWashAdditionalData? = null
) : Parcelable {

    fun getBackgroundImage(): String? {
        return image?.replace("localhost:8080", BuildConfig.API_URL)
    }

    fun isFreeBoxesEqual(data: CarWashBoxModel): Boolean {
        return boxes?.free == data.freeBoxes
    }


    companion object {
        fun createAddModel(ad : NativeAd?): CarWashModel {
            return CarWashModel("0", additionalData = CarWashAdditionalData((ad)))
        }
    }
}


@Parcelize
data class CarWashAddressModel(
    val city: String,
    val street: String,
    val district: String,
    val lat: String,
    val lon: String
) : Parcelable {
    fun getAddressStreet(): String {
        return if (street.contains("Улица", true)
            || street.contains("Ул", true)
            || street.contains("Проспект", true)
            ) street
        else "ул. $street"
    }
}


@Parcelize
data class CarWashBoxModel(
    val id: String,
    val freeBoxes: String
) : Parcelable

@Parcelize
data class CarWashBoxesModel(
    var count : String,
    var free : String,
) : Parcelable


@Parcelize
data class CarWashReviewModel(
    val name: String,
    val lastName: String,
    val image: String,
    val reviewText: String,
) : Parcelable

@Parcelize
data class CarWashAdditionalData(
    val yandexAd: @RawValue NativeAd?,
) : Parcelable
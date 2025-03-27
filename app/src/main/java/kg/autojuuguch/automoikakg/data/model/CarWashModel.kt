package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kg.autojuuguch.automoikakg.BuildConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarWashModel(
    val id: String,
    val name: String,
    @SerializedName("backgroundImage")
    val image: String,
    val address: CarWashAddressModel,
    val boxes: CarWashBoxesModel,
    val owner: String,
    val favourites : List<String>
) : Parcelable {

    fun getBackgroundImage(): String {
        return image.replace("localhost:8080", BuildConfig.API_URL)
    }

    fun isFreeBoxesEqual(data: CarWashBoxModel): Boolean {
        return boxes.free == data.freeBoxes
    }
}


@Parcelize
data class CarWashAddressModel(
    val city: String,
    val street: String,
    val district: String,
    val lat: String,
    val lon: String
) : Parcelable


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
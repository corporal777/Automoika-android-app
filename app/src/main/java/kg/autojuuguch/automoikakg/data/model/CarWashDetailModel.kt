package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kg.autojuuguch.automoikakg.BuildConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarWashDetailModel(
    val id: String,
    val name: String,
    val description: String,
    val createdAt : String,
    val backgroundImage: CarWashDetailImageModel,
    val images : List<CarWashDetailImageModel>,
    val address: CarWashDetailAddressModel,
    val contacts: CarWashDetailContactsModel,
    val boxes: CarWashBoxesModel,
    val owner: String,
    val favourites : List<String>,
    val type: String,
    val binds: CarWashBinds
) : Parcelable

@Parcelize
data class CarWashDetailAddressModel(
    val city: String,
    val street: String,
    val district: String,
    val lat: String,
    val lon: String,
    val wayDescription: String
) : Parcelable {

    fun getFullAddress(): String {
        return "г. $city, ул. $street"
    }
}

@Parcelize
data class CarWashDetailContactsModel(
    val phone: String,
    val whatsapp: String,
    val instagram: String
) : Parcelable

@Parcelize
data class CarWashDetailImageModel(
    val imageName: String,
    val imageUrl: String
) : Parcelable {

    fun loadImage(): String {
        return imageUrl.replace("localhost:8080", BuildConfig.API_URL)
    }
}

@Parcelize
data class CarWashBinds(
    @SerializedName("review")
    val reviews : List<CarWashReviewModel>
) : Parcelable
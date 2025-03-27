package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val id: String,
    @SerializedName("name")
    val firstName: String,
    val image: CarWashDetailImageModel,
    val login: UserContactsModel,
    @SerializedName("passwordIsAbsent")
    val passwordAbsent : Boolean,
    val account : UserTypeModel
) : Parcelable


@Parcelize
data class UserContactsModel(
    val phone: String,
    val isConfirmed: Boolean,
) : Parcelable

@Parcelize
data class UserTypeModel(
    val type: UserType,
    val carWash: List<String>,
) : Parcelable

enum class UserType {
    @SerializedName("user")
    USER,

    @SerializedName("car-wash-owner")
    OWNER
}
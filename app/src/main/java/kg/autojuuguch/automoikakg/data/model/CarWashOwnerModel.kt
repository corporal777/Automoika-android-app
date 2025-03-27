package kg.autojuuguch.automoikakg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarWashOwnerModel(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val backgroundImage: CarWashDetailImageModel,
    val images : List<CarWashDetailImageModel>,
    val address: CarWashDetailAddressModel,
    val contacts: CarWashDetailContactsModel,
    val boxes: CarWashBoxesModel,
    val owner: String,
    val favourites : List<String>,
    val type: String
) : Parcelable
package kg.autojuuguch.automoikakg.data

import android.os.Parcelable
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarWashData(
    val mainData: CarWashDetailModel,
    val reviewData: List<CarWashReviewModel>
) : Parcelable
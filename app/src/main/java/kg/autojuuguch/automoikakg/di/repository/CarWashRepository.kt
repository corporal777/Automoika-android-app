package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.data.PaginationResponse
import kg.autojuuguch.automoikakg.data.CarWashData
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import okhttp3.RequestBody

interface CarWashRepository {

    fun getCarWashList(map : Map<String, String>) : Maybe<PaginationResponse<CarWashModel>>
    fun getCarWashById(id : String, binds : String) : Maybe<CarWashDetailModel>
    fun getCarWashReviews(id : String) : Maybe<List<CarWashReviewModel>>
}
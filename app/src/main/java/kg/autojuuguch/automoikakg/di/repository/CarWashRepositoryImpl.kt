package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.data.PaginationResponse
import kg.autojuuguch.automoikakg.data.CarWashData
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.model.CarWashOwnerModel
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import kg.autojuuguch.automoikakg.di.data.AppData
import okhttp3.RequestBody

class CarWashRepositoryImpl(private val api: ApiService, private val appData: AppData) :
    CarWashRepository {

    override fun getCarWashList(map: Map<String, String>): Maybe<PaginationResponse<CarWashModel>> {
        return api.getCarWashList(map)
    }

    override fun getCarWashById(id: String, binds: String): Maybe<CarWashDetailModel> {
        return api.getCarWashById(id, binds)
    }

    override fun getCarWashReviews(id: String): Maybe<List<CarWashReviewModel>> {
        return api.getCarWashReviews(id).map { it.data }
    }

    override fun registerCarWash(body: RequestBody): Completable {
        return api.registerCarWash(body)
            .doOnSuccess { appData.setCarWash(it) }
            .ignoreElement()
    }
}
package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.data.PaginationResponse
import kg.autojuuguch.automoikakg.data.PaginationData
import kg.autojuuguch.automoikakg.data.model.CarWashAdditionalData
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import kg.autojuuguch.automoikakg.di.data.AppData
import okhttp3.RequestBody

class CarWashRepositoryImpl(private val api: ApiService, private val appData: AppData) :
    CarWashRepository {

    override fun getCarWashListWithAd(map: Map<String, String>): Maybe<PaginationData<CarWashModel>> {
        return api.getCarWashList(map).map {
            if (map.size > 2) PaginationData(it.data, it.data.size, it.totalCount)
            else if (appData.getYandexAd() == null) PaginationData(it.data, it.data.size, it.totalCount)
//            else {
//                var position = 0
//                val list = arrayListOf<CarWashModel>()
//                it.data.forEach {
//                    list.add(it)
//                    position += 1
//                    if (position == 2) {
//                        list.add(CarWashModel.createAddModel(appData.getYandexAd()))
//                        position = 0
//                    }
//                }
//                PaginationData(list, it.data.size, it.totalCount)
//            }
            else {
                val list = arrayListOf<CarWashModel>()
                it.data.forEachIndexed { index, model ->
                    list.add(model)
                    if (index == 1) {
                        list.add(CarWashModel.createAddModel(appData.getYandexAd()))
                    }
                }
                PaginationData(list, it.data.size, it.totalCount)
            }
        }
    }

    override fun getCarWashList(map: Map<String, String>): Maybe<PaginationResponse<CarWashModel>> {
        return api.getCarWashList(map)
    }

    override fun getCarWashById(id: String, binds: String): Maybe<CarWashDetailModel> {
        return api.getCarWashById(id, binds)
    }

    override fun getCarWashReviews(id: String): Maybe<List<CarWashReviewModel>> {
        return api.getCarWashReviews(id).map { it.data }
    }
}
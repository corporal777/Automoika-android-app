package kg.autojuuguch.automoikakg.ui.home

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.api.socket.SocketIOManager
import kg.autojuuguch.automoikakg.data.model.CarWashBoxModel
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.extensions.build
import kg.autojuuguch.automoikakg.utils.pagination.PagingDataSourceFactory
import kg.autojuuguch.automoikakg.utils.pagination.applyErrorHandler
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.di.repository.StoriesRepository
import kg.autojuuguch.automoikakg.extensions.withDelay

class HomeViewModel(
    private val appData: AppData,
    private val carWashRepository: CarWashRepository,
    private val storiesRepository: StoriesRepository,
    private val socket: SocketIOManager
) : BaseViewModel(appData) {

    private val _updateData = SingleLiveEvent<CarWashBoxModel>()
    val updateData: LiveData<CarWashBoxModel> get() = _updateData

    private val _pagingData = SingleLiveEvent<PagingData<CarWashModel>>()
    val pagingData: LiveData<PagingData<CarWashModel>> get() = _pagingData

    private val _storiesData = SingleLiveEvent<List<StoriesModel>>()
    val storiesData: LiveData<List<StoriesModel>> get() = _storiesData

    private val pagination = PagingDataSourceFactory { limit, offset ->
        carWashRepository.getCarWashList(buildFilters(limit, offset))
    }.applyErrorHandler { }.build(initialSize = 30, distance = 5)

    private var isFirstLaunch = true
    private var searchText = ""

    init {
        getStories()
        getCarWashListData()
        subscribeSocket()
    }

    private fun getStories() {
        compositeDisposable += storiesRepository.getStories()
            .onErrorResumeNext(Maybe.just(emptyList()))
            .doOnSuccess { appData.setStories(it) }
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _storiesData.setValue(it) }
    }

    private fun getCarWashListData() {
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { _pagingData.setValue(it) })
    }

    private fun subscribeSocket() {
        compositeDisposable += socket.subscribeFreeBoxesMessage()
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _updateData.value = it }
    }

    fun onSearchRequest(text: String) {
        if (text == searchText) return
        searchText = text
        pagination.invalidate()
    }

    private fun buildFilters(limit: Int, offset: Int): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put("limit", limit.toString())
            put("offset", offset.toString())

            if (searchText.isNotBlank()) put("search", searchText)
        }
    }
}
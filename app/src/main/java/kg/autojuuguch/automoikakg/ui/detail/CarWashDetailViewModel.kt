package kg.autojuuguch.automoikakg.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.CarWashData
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.toSingleEvent
import kg.autojuuguch.automoikakg.extensions.call

class CarWashDetailViewModel(
    private val appData: AppData,
    private val repository: CarWashRepository
) : BaseViewModel(appData) {

    var carWashId = ""
    private var scrollOffset = 0

    private val _carWashDetail = SingleLiveEvent<CarWashDetailModel>()
    val carWashDetail: LiveData<CarWashDetailModel> get() = _carWashDetail

    private val _scrollOffsetData = MutableLiveData(scrollOffset)
    val scrollOffsetData get() = _scrollOffsetData.toSingleEvent()


    fun getCarWashDetail() {
        repository.getCarWashById(carWashId, getBinds())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { _carWashDetail.setValue(it) }
            ).call(compositeDisposable)
    }

    fun changeScrollOffset(value: Int) {
        scrollOffset = value
        _scrollOffsetData.setValue(scrollOffset)
    }

    private fun getBinds(): String {
        return "reviewShort"
    }
}
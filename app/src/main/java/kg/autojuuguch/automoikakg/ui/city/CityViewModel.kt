package kg.autojuuguch.automoikakg.ui.city

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.body.LocationBody
import kg.autojuuguch.automoikakg.data.model.CityLocationModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.LocationRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.extensions.withLoading
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class CityViewModel(
    private val appData: AppData,
    private val repository: LocationRepository
) : BaseViewModel(appData) {

    private val _userLocation = MutableLiveData<CityLocationModel?>()
    val userLocation: LiveData<CityLocationModel?> get() = _userLocation

    private val _citySuccessSaved = MutableLiveData<Boolean>()
    val citySuccessSaved: LiveData<Boolean> get() = _citySuccessSaved


    private var userCity = ""

    fun initLocation(request: Observable<Location>) {
        compositeDisposable += request.firstElement()
            .flatMap { repository.getCityFromLocation(it) }
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = {
                    _citySuccessSaved.value = false
                    _userLocation.value = null
                },
                onSuccess = {
                    if (it.city.isNotEmpty()) userCity = it.city
                    _userLocation.value = it
                })

    }

    fun onSaveUserCity() {
        compositeDisposable += Completable.fromAction { appData.setUserCity(userCity) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _citySuccessSaved.setValue(true) }
    }

    fun onShowEnterCity() = run { _userLocation.value = null }

    fun onChangeCity(city: String) = run { userCity = city }
}
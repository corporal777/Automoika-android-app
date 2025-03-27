package kg.autojuuguch.automoikakg.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.AddressComponentType
import com.google.maps.model.AddressType
import com.google.maps.model.LatLng
import com.google.maps.model.LocationType
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.di.repository.LocationRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackground
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG

class MapViewModel(
    private val appData: AppData,
    private val carWashRepository: CarWashRepository,
    private val locationRepository: LocationRepository
) : BaseViewModel(appData) {

    private val _location = SingleLiveEvent<YandexGeoModel>()
    val location: LiveData<YandexGeoModel> get() = _location

    private val _fromRegister = SingleLiveEvent<Boolean>()
    val fromRegister: LiveData<Boolean> get() = _fromRegister

    private val _instructions = SingleLiveEvent<Boolean>()
    val instructions: LiveData<Boolean> get() = _instructions


    init {
        startInstructionsTimer()
    }

    fun initMap(fromRegister : Boolean){
        _fromRegister.postValue(fromRegister)
    }

    fun getYandexGeoLocation(lat : Double, lon : Double){
        locationRepository.getAddressInfo(lat, lon)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { _location.setValue(it) }
            ).call(compositeDisposable)
    }


    fun startInstructionsTimer(){
        if (appData.isMapInstructionsShown) return
        compositeDisposable += Completable.complete()
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _instructions.setValue(true)
                stopInstructionsTimer()
            }
    }

    private fun stopInstructionsTimer(){
        compositeDisposable += Completable.complete()
            .withDelay(3000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _instructions.setValue(false)
                appData.isMapInstructionsShown = true
            }
    }
}
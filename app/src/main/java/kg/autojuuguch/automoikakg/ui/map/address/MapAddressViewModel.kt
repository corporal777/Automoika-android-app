package kg.autojuuguch.automoikakg.ui.map.address

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.AddressType
import com.google.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.di.repository.LocationRepository
import kg.autojuuguch.automoikakg.extensions.performOnBackground
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG

class MapAddressViewModel (
    private val appData: AppData,
    private val repository: LocationRepository
) : BaseViewModel(appData) {

    private val _location = SingleLiveEvent<YandexGeoModel>()
    val location: LiveData<YandexGeoModel> get() = _location


    private var lat : Double = 0.0
    private var lon : Double = 0.0

    fun getYandexGeoLocation(lat : Double, lon : Double){
        compositeDisposable += repository.getAddressInfo(lat, lon)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { _location.setValue(it) })
    }

    init {
//        compositeDisposable += Completable.fromAction {
//            val context = GeoApiContext.Builder()
//                .apiKey("AIzaSyBksdE6JZouoxDwvknFQkW86Enl2pbP0iY")
//                .build()
//            val response = GeocodingApi.newRequest(context)
//                .latlng(LatLng(42.88423144538085, 74.56303243178958))
//                .language("ru")
//                .resultType(AddressType.STREET_ADDRESS, AddressType.POLITICAL)
//                .await()
//
//            val gson = GsonBuilder().setPrettyPrinting().create()
//            Log.e(LOG_TAG, gson.toJson(response.get(0).formattedAddress))
//
//            context.shutdown()
//        }
//            .performOnBackground()
//            .subscribeSimple {
//
//            }
    }

    fun getLatLng() = LatLng(lat, lon)
}
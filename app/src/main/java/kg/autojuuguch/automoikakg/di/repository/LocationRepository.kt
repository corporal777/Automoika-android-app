package kg.autojuuguch.automoikakg.di.repository

import android.location.Location
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.data.model.CityLocationModel
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel

interface LocationRepository {

    fun getCityFromLocation(location: Location) : Maybe<CityLocationModel>
    fun getAddressInfo(lat: Double, lon : Double) : Maybe<YandexGeoModel>
}
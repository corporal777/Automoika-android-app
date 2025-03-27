package kg.autojuuguch.automoikakg.di.repository

import android.location.Location
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.body.LocationBody
import kg.autojuuguch.automoikakg.data.model.CityLocationModel
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel

class LocationRepositoryImpl(private val api: ApiService) : LocationRepository {

    override fun getCityFromLocation(location: Location): Maybe<CityLocationModel> {
        val body =  LocationBody(
            location.latitude.toString(),
            location.longitude.toString()
        )
        return api.getCityFromLocation(body)
    }

    override fun getAddressInfo(lat: Double, lon: Double): Maybe<YandexGeoModel> {
        return api.getAddressInfoFromLatLng(LocationBody(lat.toString(), lon.toString())).map {
            it.apply {
                latitude = lat
                longitude = lon
            }
        }
    }
}
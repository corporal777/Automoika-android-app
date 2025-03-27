package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.contacts

import android.content.ContentResolver
import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.body.CarWashRegisterBody
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.di.repository.UserRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackground
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.Utils
import kg.autojuuguch.automoikakg.utils.Utils.isPhone
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid
import okhttp3.MultipartBody

class RegisterContactsViewModel(
    private val appData: AppData,
    private val carWashRepository: CarWashRepository,
    private val contentResolver: ContentResolver
) : BaseViewModel(appData) {

    lateinit var body: CarWashRegisterBody
    var isAgree = false

    val cityError = SingleLiveEvent<Boolean>()
    val streetError = SingleLiveEvent<Boolean>()
    val phoneError = SingleLiveEvent<Boolean>()
    val agreementError = SingleLiveEvent<Boolean>()
    val buttonEnabled = SingleLiveEvent<Boolean>()

    private val _location = SingleLiveEvent<YandexGeoModel>()
    val location: LiveData<YandexGeoModel> get() = _location

    private val _registerSuccess = SingleLiveEvent<Unit>()
    val registerSuccess: LiveData<Unit> get() = _registerSuccess


    fun registerCarWash() {
        if (!isDataValid()) return
        carWashRepository.registerCarWash(getRequestBody())
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { _registerSuccess.setValue(Unit) }
            ).call(compositeDisposable)
    }

    fun onChangeLocation(location: YandexGeoModel) {
        body.lat = location.latitude.toString()
        body.lon = location.longitude.toString()
        _location.setValue(location)
    }

    fun onChangeCity(city: String) {
        body.city = city
        cityError.value = false
        performDataChange()
    }

    fun onChangeStreet(street: String) {
        body.street = street
        streetError.value = false
        performDataChange()
    }

    fun onChangePhone(phone: String) {
        body.phone = phone
        phoneError.value = false
        performDataChange()
    }

    fun onChangeAgreement(agree: Boolean) {
        this.isAgree = agree
        agreementError.value = false
        performDataChange()
    }

    fun onChangeDistrict(district: String) = run { body.district = district }
    fun onChangeWay(way: String) = run { body.way = way }
    fun onChangeWhatsapp(whatsapp: String) = run { body.whatsapp = whatsapp }
    //fun onChangeTelegram(telegram: String) = run { body.telegram = telegram }
    fun onChangeInstagram(instagram: String) = run { body.instagram = instagram }


    fun performDataChange() = buttonEnabled.setValue(body.isContactsValid() && isAgree)

    private fun isDataValid(): Boolean {
        cityError.setValue(body.city.isBlank())
        streetError.setValue(body.street.isBlank())
        phoneError.setValue(!isPhoneNumberValid(body.phone))
        agreementError.setValue(!isAgree)
        return body.isContactsValid() && isAgree
    }

    fun isDataFilled(): Boolean {
        return body.city.isNotEmpty() || body.street.isNotEmpty() || body.phone.isNotEmpty()
    }

    private fun getRequestBody(): MultipartBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .apply {
                addFormDataPart("name", body.name)
                addFormDataPart("description", body.description)

                addFormDataPart("city", body.city)
                addFormDataPart("street", body.street)
                addFormDataPart("district", body.district)
                addFormDataPart("lat", body.lat)
                addFormDataPart("lon", body.lon)
                addFormDataPart("wayDescription", body.way)

                addFormDataPart("boxes", body.boxes)

                addFormDataPart("phone", body.phone)
                addFormDataPart("whatsapp", body.whatsapp)
                addFormDataPart("instagram", body.instagram)

                addFormDataPart("type", body.getCarWashType())

                addFormDataPart("user", appData.getUserId() ?: "")

                if (body.uri != null) {
                    val name = body.getFileName(contentResolver)
                    body.getRequestBody(contentResolver) { body ->
                        addFormDataPart("backgroundImage", name, body)
                    }
                }
            }.build()

    }
}
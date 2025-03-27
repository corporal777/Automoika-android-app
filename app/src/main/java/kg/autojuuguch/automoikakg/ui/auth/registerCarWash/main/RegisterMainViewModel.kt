package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.body.CarWashRegisterBody
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.saveImageToCache
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.Utils.isPhone

class RegisterMainViewModel(private val appData: AppData) : BaseViewModel(appData) {

    private val body = CarWashRegisterBody()
    private var image: Bitmap? = null

    val nameError = SingleLiveEvent<Boolean>()
    val descriptionError = SingleLiveEvent<Boolean>()
    val boxesError = SingleLiveEvent<Boolean>()
    val typeError = SingleLiveEvent<Boolean>()

    private val _imageBitmap = MutableLiveData<Bitmap?>(image)
    val imageBitmap: LiveData<Bitmap?> get() = _imageBitmap

    private val _buttonEnabled = SingleLiveEvent<Boolean>()
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled

    private val _register = SingleLiveEvent<CarWashRegisterBody>()
    val register: LiveData<CarWashRegisterBody> get() = _register


    init {
        performDataChange()
    }

    fun continueRegister() {
        if (!isDataValid()) return
        compositeDisposable += Maybe.just(body)
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple { _register.setValue(it) }
    }

    fun onChangeImage(bitmap: Bitmap, context: Context) {
        Maybe.fromCallable { saveImageToCache(context, bitmap) }
            .doOnSuccess {
                image = bitmap
                body.uri = it
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { _imageBitmap.setValue(image) })
            .call(compositeDisposable)
    }


    fun onChangeName(name: String) {
        body.name = name
        performDataChange()
        nameError.setValue(false)
    }

    fun onChangeDescription(desc: String) {
        body.description = desc
        performDataChange()
        descriptionError.setValue(false)
    }

    fun onChangeBoxes(boxes: String) {
        body.boxes = boxes
        performDataChange()
        boxesError.setValue(false)
    }

    fun onChangeType(type: String) {
        body.type = type
        performDataChange()
        typeError.setValue(false)
    }


    private fun isDataValid(): Boolean {
        val nameValid = body.name.isNotBlank() && !isPhone(body.name)
        val descValid = body.description.isNotBlank()
        val boxesValid = body.boxes.isNotBlank()
        val typeValid = body.type.isNotBlank()

        nameError.setValue(!nameValid)
        descriptionError.setValue(!descValid)
        boxesError.setValue(!boxesValid)
        typeError.setValue(!typeValid)
        return nameValid && descValid && boxesValid && typeValid
    }

    private fun performDataChange() {
        val isValid = body.name.isNotBlank() && body.description.isNotBlank()
                && body.boxes.isNotBlank() && body.type.isNotBlank()
        _buttonEnabled.setValue(isValid)
    }

    fun isDataFilled(): Boolean {
        return body.name.isNotEmpty() || body.description.isNotEmpty()
                || body.boxes.isNotEmpty() || body.type.isNotEmpty() || body.uri != null
    }
}
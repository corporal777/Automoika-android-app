package kg.autojuuguch.automoikakg.ui.auth.registerUser

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.Optional
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.asOptional
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.exceptions.PermissionNotGrantedException
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.saveImageToCache
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.ui.views.PasswordModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.Utils
import kg.autojuuguch.automoikakg.utils.Utils.isContainsNumbers
import kg.autojuuguch.automoikakg.utils.Utils.isPhone
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid
import kg.autojuuguch.automoikakg.utils.getGalleryImages
import java.util.concurrent.Callable

class UserRegisterViewModel(
    private val appData: AppData,
    private val repository: AuthRepository
) : BaseViewModel(appData) {

    private var isAgree: Boolean = false
    private var isPasswordValid: Boolean = false
    private val userBody = UserRegisterBody()

    private val _buttonEnabled = SingleLiveEvent<Boolean>()
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled

    private val _phoneUnique = SingleLiveEvent<Boolean>()
    val phoneUnique: LiveData<Boolean> get() = _phoneUnique

    val firstNameError = SingleLiveEvent<Boolean>()
    val phoneError = SingleLiveEvent<Boolean>()
    val passwordError = SingleLiveEvent<Boolean>()
    val agreementError = SingleLiveEvent<Boolean>()


    init {
        performDataChange()
    }


    fun checkPhoneIsUnique() {
        if (!isDataValid()) return
        repository.checkPhoneExist(userBody.phone)
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { _phoneUnique.setValue(it) }
            ).call(compositeDisposable)
    }


    private fun isDataValid(): Boolean {
        val firstNameValid = userBody.name.isNotBlank() && !isPhone(userBody.name)
        firstNameError.setValue(!firstNameValid)
        phoneError.setValue(!isPhoneNumberValid(userBody.phone))
        passwordError.setValue(!isPasswordValid)
        agreementError.setValue(!isAgree)
        return firstNameValid && isPhoneNumberValid(userBody.phone) && isPasswordValid && isAgree
    }


    fun onChangeFirstName(name: String) {
        userBody.name = name
        performDataChange()
        firstNameError.setValue(false)
    }

    fun onChangePhone(phone: String) {
        userBody.phone = phone
        phoneError.setValue(false)
        performDataChange()
    }

    fun onChangePassword(password: PasswordModel) {
        userBody.password = password.password ?: ""
        isPasswordValid = password.isValid
        passwordError.setValue(false)
        performDataChange()
    }

    fun onChangeAgreement(isAgree: Boolean) {
        this.isAgree = isAgree
        agreementError.setValue(false)
        performDataChange()
    }


    private fun performDataChange() {
        _buttonEnabled.setValue(userBody.isComplete() && isPasswordValid && isAgree)
    }

    fun getPhone() = userBody.phone
    fun getBody() = userBody
    fun isAgree() = isAgree
    fun setBody(isReg: Boolean) = run { userBody.fromUserReq = isReg }
}
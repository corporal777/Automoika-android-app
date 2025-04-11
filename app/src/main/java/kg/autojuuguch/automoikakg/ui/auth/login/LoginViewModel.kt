package kg.autojuuguch.automoikakg.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.google.firebase.auth.PhoneAuthOptions
import com.google.gson.Gson
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.model.ErrorResponse
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.ui.views.PasswordModel
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val appData: AppData,
    private val repository: AuthRepository
) : BaseViewModel(appData) {


    val phoneError = SingleLiveEvent<Boolean>()
    val passwordError = SingleLiveEvent<Boolean>()

    private val _loginSuccess = SingleLiveEvent<Unit>()
    val loginSuccess: LiveData<Unit> get() = _loginSuccess

    private val _buttonEnabled = SingleLiveEvent<Boolean>()
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled

    private var login = ""
    private var password = ""


    init {
        performDataChange()
    }

    fun loginToAccount() {
        compositeDisposable += repository.login(LoginBody(login, password))
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { catchError(it) },
                onComplete = { _loginSuccess.setValue(Unit) }
            )
    }


    fun onChangeLogin(phone: String) = run {
        this.login = phone
        performDataChange()
        phoneError.setValue(false)
    }

    fun onChangePassword(password: String) {
        this.password = password
        performDataChange()
        passwordError.setValue(false)
    }


    private fun performDataChange() {
        _buttonEnabled.setValue(isPhoneNumberValid(login) && password.isNotBlank())
    }



    private fun catchError(it: Throwable) {
        it.printStackTrace()
        if (it is HttpException) {
            try {
                val error = Gson().fromJson(it.response()?.errorBody()?.string(), ErrorResponse::class.java)
                if (error.message == "User not found") phoneError.setValue(true)
                else if (error.message == "Password is not valid") passwordError.setValue(true)
                else onReceiveError(it)
            } catch (e: Exception) {
                onReceiveError(it)
            }
        }
    }
}
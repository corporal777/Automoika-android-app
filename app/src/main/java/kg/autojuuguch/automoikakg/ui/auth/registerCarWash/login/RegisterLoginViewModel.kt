package kg.autojuuguch.automoikakg.ui.auth.registerCarWash.login

import androidx.lifecycle.LiveData
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.ui.views.PasswordModel
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid

class RegisterLoginViewModel(
    private val appData: AppData,
    private val repository: AuthRepository
) : BaseViewModel(appData) {

    val passwordError = SingleLiveEvent<Boolean>()

    private val _showConfirm = SingleLiveEvent<Unit>()
    val showConfirm: LiveData<Unit> get() = _showConfirm

    private val _showRegister = SingleLiveEvent<String>()
    val showRegister: LiveData<String> get() = _showRegister

    private val _buttonEnabled = SingleLiveEvent<Boolean>()
    val buttonEnabled: LiveData<Boolean> get() = _buttonEnabled

    private var login = ""
    private var password = ""
    private var passwordIsValid = false


    init {
        performDataChange()
    }


    fun checkLogin() {
        repository.checkLogin(LoginBody(login, password))
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { catchResponse(it) }
            ).call(compositeDisposable)
    }

    fun onChangeLogin(phone: String) {
        this.login = phone
        performDataChange()
    }

    fun onChangePassword(model: PasswordModel) {
        this.password = model.password ?: ""
        this.passwordIsValid = model.isValid
        performDataChange()
        passwordError.setValue(false)
    }


    private fun performDataChange() {
        _buttonEnabled.setValue(isPhoneNumberValid(login) && password.isNotBlank() && passwordIsValid)
    }

    private fun catchResponse(it : String){
        if (it == "Login is free") _showConfirm.setValue(Unit)
        else if (it == "Password is not valid") passwordError.setValue(true)
        else _showRegister.setValue(it)
    }

    fun getPhone() = login
    fun getPassword() = password
}
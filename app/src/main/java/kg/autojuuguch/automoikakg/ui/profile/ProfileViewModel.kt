package kg.autojuuguch.automoikakg.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.UserModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.di.repository.UserRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel

class ProfileViewModel(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseViewModel(appData) {

    private val _user = SingleLiveEvent<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    init {
        compositeDisposable += userRepository.getUser(appData.getUserId())
            .doOnSuccess { appData.setUser(it) }
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { _user.setValue(it) }
            )
    }

    fun logoutUser(){
        compositeDisposable += authRepository.logout(appData.getUserId())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { _user.setValue(null) }
            )
    }

    fun getCity() = appData.getUserCity()
    fun getUserId() = "id" + appData.getUserId()
}
package kg.autojuuguch.automoikakg.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.api.socket.SocketIOManager
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val appData: AppData,
    private val socket: SocketIOManager
) : BaseViewModel(appData) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _userCity = MutableLiveData<String>()
    val userCity: LiveData<String> get() = _userCity

    init {
        checkUserCity()
    }


    private fun checkUserCity() {
        compositeDisposable += Maybe.just(appData.getUserCity())
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _userCity.value = it
                _isLoading.value = false
            }
    }

    fun connectSocket() {
        compositeDisposable += socket.connect()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {}
    }


    override fun onCleared() {
        socket.disconnect()
        super.onCleared()
    }
}
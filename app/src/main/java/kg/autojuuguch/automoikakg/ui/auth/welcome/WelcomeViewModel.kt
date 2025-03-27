package kg.autojuuguch.automoikakg.ui.auth.welcome

import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.model.UserType
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.ui.confirm.ConfirmCodeViewModel.Companion.TIMER_SECONDS_COUNT
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import java.util.concurrent.TimeUnit

class WelcomeViewModel(private val appData: AppData) : BaseViewModel(appData) {


    private val _withNavigateUp = SingleLiveEvent<Unit>()
    val withNavigateUp: LiveData<Unit> get() = _withNavigateUp


    init {
        startWelcomeTimer()
    }

    private fun startWelcomeTimer() {
        compositeDisposable += Completable.complete()
            .withDelay(4000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _withNavigateUp.setValue(Unit) }
    }

    fun getUserName(): String {
        return appData.getUser()?.firstName ?: ""
    }
}
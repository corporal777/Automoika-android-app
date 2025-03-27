package kg.autojuuguch.automoikakg.ui.confirm

import android.Manifest
import android.content.ContentResolver
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import java.util.concurrent.TimeUnit
import io.reactivex.functions.Consumer
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.di.repository.UserRepository
import kg.autojuuguch.automoikakg.exceptions.CodeInvalidException
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import kg.autojuuguch.automoikakg.utils.PermissionUtils
import okhttp3.MultipartBody

class ConfirmCodeViewModel(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val contentResolver: ContentResolver
) : BaseViewModel(appData) {

    var phone = ""
    var user: UserRegisterBody? = null
    var login: LoginBody? = null
    private var code = ""

    private val _timeLeft = SingleLiveEvent<Int>()
    val timeLeft: LiveData<Int> get() = _timeLeft

    private val _loading = SingleLiveEvent<Pair<Int, Boolean>>()
    val loading: LiveData<Pair<Int, Boolean>> get() = _loading

    private val _codeError = SingleLiveEvent<Boolean>()
    val codeError: LiveData<Boolean> get() = _codeError

    private val _permissionError = SingleLiveEvent<Unit>()
    val permissionError: LiveData<Unit> get() = _permissionError

    val navigateUp = SingleLiveEvent<Unit>()
    val welcome = SingleLiveEvent<Boolean>()


    private val _codeAutoFill = SingleLiveEvent<String>()
    val codeAutoFill: LiveData<String> get() = _codeAutoFill

    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }


    init {
        compositeDisposable += appData.getConfirmationCodeSubject()
            .performOnBackgroundOutOnMain()
            .subscribeSimple { _codeAutoFill.setValue(it) }
    }

    fun sendCode(permissionUtils: PermissionUtils) {
        permissionUtils.checkNotificationsPermission { isGranted ->
            if (isGranted) authRepository.sendConfirmationCode(phone)
                .performOnBackgroundOutOnMain()
                .withLoading(1)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onComplete = { startTimer() }
                ).call(compositeDisposable)
            else _permissionError.setValue(Unit)
        }
    }

    fun confirmPhone() {
        compositeDisposable += authRepository.confirmCode(code, phone)
            .andThen(afterConfirmRequest())
            .performOnBackgroundOutOnMain()
            .withLoading(0)
            .subscribeSimple(
                onError = {
                    if (it !is CodeInvalidException) onReceiveError(it)
                    else _codeError.setValue(true)
                },
                onComplete = {
                    if (user != null) welcome.setValue(user!!.fromUserReq)
                    else navigateUp.setValue(Unit)
                }
            )
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        _timeLeft.value = TIMER_SECONDS_COUNT

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                _timeLeft.value = timeLeft
                if (timeLeft <= 0) timerCompositeDisposable.clear()
            }
    }


    fun onChangeCode(text: String) {
        code = text
        _codeError.value = false
    }

    fun isCodeValid(code: String): Boolean = code.length == CODE_SIZE




    private fun afterConfirmRequest(): Completable {
        return Completable.defer {
            if (user != null) userRepository.registerUser(user!!)
            else Completable.complete().withDelay(1000)
        }
    }



    private fun Completable.withLoading(type: Int): Completable {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { _loading.value = Pair(type, true) }
            .doOnDispose { _loading.value = Pair(type, false) }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) _loading.value = Pair(type, false)
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) _loading.value = Pair(type, false)
            else loadingDisposable.dispose()
        }
        return this.doOnDispose(actionHide)
            .doOnComplete(actionHide)
            .doOnError(actionConsumer())
    }


    companion object {
        const val TIMER_SECONDS_COUNT = 60
        const val CODE_SIZE = 6
    }

}
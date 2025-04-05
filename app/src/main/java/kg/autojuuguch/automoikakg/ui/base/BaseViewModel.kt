package kg.autojuuguch.automoikakg.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.withLoading
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel(private val appData: AppData) : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    private val _buttonLoading = SingleLiveEvent<Boolean>()
    val buttonLoading: LiveData<Boolean> get() = _buttonLoading

    private val _errorMessage = SingleLiveEvent<Boolean>()
    val errorMessage: LiveData<Boolean> get() = _errorMessage

    private val _freeBoxes = MutableStateFlow<String>("")
    val freeBoxes = _freeBoxes.asStateFlow()


    fun isUserAuthorized() = appData.isUserAuthorized()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


    fun Completable.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onComplete: () -> Unit
    ): Disposable = subscribe(Action(onComplete), createOnErrorConsumer(onError))


    fun <T> Single<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(Consumer(onSuccess), createOnErrorConsumer(onError))


    fun <T> Maybe<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(Consumer(onSuccess), createOnErrorConsumer(onError))


    fun <T> Observable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable = subscribe(Consumer(onNext), createOnErrorConsumer(onError))


    fun <T> Flowable<T>.subscribeSimple(
        onError: ((Throwable) -> Unit)? = null,
        onNext: (T) -> Unit
    ): Disposable = subscribe(Consumer(onNext), createOnErrorConsumer(onError))



    private fun createOnErrorConsumer(onError: ((Throwable) -> Unit)?): Consumer<Throwable> {
        return Consumer {
            it.printStackTrace()
            if (onError != null) onError(it)
            if (it is java.lang.NullPointerException && it.message == "User is not authorized!") {
                //_errorMessage.postValue("Необходимо войти в аккаунт")
            }
        }
    }

    fun onReceiveError(t : Throwable, value : Boolean? = null){
        t.printStackTrace()
        _errorMessage.value = value ?: true
    }

    fun <T> Maybe<T>.withButtonLoading() : Maybe<T> {
        return withLoading(_buttonLoading)
    }

    fun Completable.withButtonLoading() : Completable {
        return withLoading(_buttonLoading)
    }
}
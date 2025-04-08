package kg.autojuuguch.automoikakg.extensions

import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Completable]
 * */
fun Completable.performOnBackgroundOutOnMain(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Flowable]
 * */
fun <T> Flowable<T>.performOnBackgroundOutOnMain(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Single]
 * */
fun <T> Single<T>.performOnBackgroundOutOnMain(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Maybe]
 * */
fun <T> Maybe<T>.performOnBackgroundOutOnMain(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Observable]
 * */
fun <T> Observable<T>.performOnBackgroundOutOnMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to add a Disposable to a CompositeDisposable
 */
@Deprecated("Use rxkotlin", ReplaceWith("compositeDisposable += this"))
fun Disposable.call(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

/**
 * Extension function to subscribe on the background thread for a Flowable
 * */
fun <T> Flowable<T>.performOnBackground(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Maybe
 * */
fun <T> Maybe<T>.performOnBackground(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Completable
 * */
fun Completable.performOnBackground(): Completable {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Observable
 * */
fun <T> Observable<T>.performOnBackground(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the main thread for a Observable
 * */
fun <T> Observable<T>.performOnMain(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}


fun <T> Single<T>.withDelay(time: Long): Single<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Maybe<T>.withDelay(time: Long): Maybe<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Observable<T>.withDelay(time: Long): Observable<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Flowable<T>.withDelay(time: Long): Flowable<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun Completable.withDelay(time: Long): Completable {
    return delay(time, TimeUnit.MILLISECONDS)
}



fun <T> Maybe<T>.withLoading(loading : MutableLiveData<Boolean>): Maybe<T> {
    val loadingDisposable = Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { loading.value = true }
        .doOnDispose { loading.value = false }
        .subscribe()
    val actionHide = Action {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }

    fun <T> actionConsumer() = Consumer<T> {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }
    return this.doOnDispose(actionHide)
        .doOnSuccess(actionConsumer())
        .doOnError(actionConsumer())
}

fun <T> Single<T>.withLoading(loading : MutableLiveData<Boolean>): Single<T> {
    val loadingDisposable = Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { loading.value = true }
        .doOnDispose { loading.value = false }
        .subscribe()
    val actionHide = Action {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }

    fun <T> actionConsumer() = Consumer<T> {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }
    return this.doOnDispose(actionHide)
        .doOnSuccess(actionConsumer())
        .doOnError(actionConsumer())
}

fun Completable.withLoading(loading : MutableLiveData<Boolean>): Completable {
    val loadingDisposable = Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { loading.value = true }
        .doOnDispose { loading.value = false }
        .subscribe()
    val actionHide = Action {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }

    fun <T> actionConsumer() = Consumer<T> {
        if (loadingDisposable.isDisposed) loading.value = false
        else loadingDisposable.dispose()
    }
    return this.doOnDispose(actionHide)
        .doOnComplete(actionHide)
        .doOnError(actionConsumer())
}

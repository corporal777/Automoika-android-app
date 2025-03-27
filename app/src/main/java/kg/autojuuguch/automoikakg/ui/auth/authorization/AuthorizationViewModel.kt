package kg.autojuuguch.automoikakg.ui.auth.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.data.body.GoogleUserBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.data.model.UserModel
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.repository.UserRepository
import kg.autojuuguch.automoikakg.extensions.call
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.extensions.withDelay
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.FirebaseAuthUtils
import kg.autojuuguch.automoikakg.utils.GoogleAuthUtils
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import retrofit2.HttpException

class AuthorizationViewModel(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BaseViewModel(appData) {

    private val _googleAccount = SingleLiveEvent<Boolean>()
    val googleAccount: LiveData<Boolean> get() = _googleAccount

    fun initGoogleAccount(acc: GoogleSignInAccount?) {
        if (acc == null) _googleAccount.setValue(false)
        else userRepository.getUser(getAccountId(acc) ?: "0")
            .doOnSuccess { appData.setUser(it) }.ignoreElement()
            .onErrorResumeNext { createUserRequest(it, acc) }
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .withButtonLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { _googleAccount.setValue(true) }
            ).call(compositeDisposable)
    }

    private fun createUserRequest(it: Throwable, acc: GoogleSignInAccount): Completable {
        return if (it is HttpException && it.code() == 404) {
            val userBody = UserRegisterBody(
                id = getAccountId(acc),
                name = acc.givenName ?: acc.familyName ?: "",
                phone = "-",
                password = "-"
            )
            return userRepository.registerUser(userBody)
        } else Completable.error(it)
    }

    private fun getAccountId(acc: GoogleSignInAccount): String? {
        return if (acc.id.isNullOrEmpty()) null
        else if (acc.id!!.length > 10) acc.id?.substring(0, 10)
        else acc.id
    }
}
package kg.autojuuguch.automoikakg.di.repository

import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Completable
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.body.CodeBody
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.body.PhoneBody
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.exceptions.CodeInvalidException
import kg.autojuuguch.automoikakg.extensions.withDelay
import okhttp3.RequestBody
import retrofit2.HttpException

class AuthRepositoryImpl(private val api: ApiService, private val appData: AppData) :
    AuthRepository {

    override fun getFcmToken(): Maybe<String> {
        return Maybe.create { emitter ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { t -> emitter.onSuccess(t) }
                .addOnFailureListener { e -> emitter.onError(e) }
        }
    }

    override fun login(phone: LoginBody): Completable {
        return api.login(phone).doOnSuccess { appData.setUser(it) }.ignoreElement()
    }

    override fun checkLogin(phone: LoginBody): Maybe<String> = api.checkLogin(phone)


    override fun checkPhoneExist(phone: String): Maybe<Boolean> {
        return api.checkPhoneExists(mapOf("phone" to phone))
            .map { it != "-1" }
    }

    override fun sendConfirmationCode(phone: String): Completable {
        return getFcmToken().flatMapCompletable { api.sendCode(PhoneBody(phone, it)) }
            .withDelay(1000)
    }

    override fun confirmCode(code: String, phone: String): Completable {
        return api.confirmCode(CodeBody(phone, code))
            .onErrorResumeNext {
                if (it is HttpException && it.code() == 404)
                    Completable.error(CodeInvalidException())
                else Completable.error(it)
            }
    }


}
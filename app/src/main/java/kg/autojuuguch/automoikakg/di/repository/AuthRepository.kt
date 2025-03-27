package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.body.PhoneBody
import kg.autojuuguch.automoikakg.data.model.LoginModel
import kg.autojuuguch.automoikakg.data.model.UserModel
import okhttp3.RequestBody

interface AuthRepository {

    fun getFcmToken() : Maybe<String>
    fun login(phone : LoginBody) : Completable
    fun checkLogin(phone : LoginBody) : Maybe<String>

    fun checkPhoneExist(phone : String) : Maybe<Boolean>
    fun sendConfirmationCode(phone : String) : Completable
    fun confirmCode(code : String, phone: String) : Completable
}
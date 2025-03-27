package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kg.autojuuguch.automoikakg.data.body.GoogleUserBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.data.model.UserModel
import okhttp3.RequestBody

interface UserRepository {
    fun getUser(id : String): Maybe<UserModel>
    fun registerUser(body: UserRegisterBody) : Completable
    fun registerGoogleUser(body: GoogleUserBody) : Completable
}
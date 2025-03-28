package kg.autojuuguch.automoikakg.di.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.data.body.GoogleUserBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.data.model.UserModel
import kg.autojuuguch.automoikakg.di.data.AppData
import okhttp3.RequestBody

class UserRepositoryImpl(private val api: ApiService, private val appData: AppData) : UserRepository {

    override fun getUser(id : String?): Maybe<UserModel> {
        return api.getUserById(id ?: "0")
    }

    override fun registerUser(body: UserRegisterBody): Completable {
        return api.registerUser(body).doOnSuccess { appData.setUser(it) }.ignoreElement()
    }

    override fun registerGoogleUser(body: GoogleUserBody): Completable {
        val userBody = UserRegisterBody(
            id = body.id,
            name = body.name ?: "",
            phone = "-",
            password = "-"
        )
        return api.registerUser(userBody).doOnSuccess { appData.setUser(it) }.ignoreElement()
    }
}
package kg.autojuuguch.automoikakg.di.data

import android.content.Context
import io.reactivex.subjects.PublishSubject
import kg.autojuuguch.automoikakg.data.model.UserModel

class AppData(private val appPrefs: AppPrefs, private val context: Context) {

    var isMapInstructionsShown = false

    var token: String? = appPrefs.token
        set(value) {
            if (field == value) return
            field = value
            appPrefs.token = value
        }

    private var userId: String? = appPrefs.userId
        set(value) {
            if (field == value) return
            field = value
            appPrefs.userId = value
        }

    private var userTown: String? = appPrefs.userCity
        set(value) {
            if (field == value) return
            field = value
            appPrefs.userCity = value
        }

    private var deviceId: String? = appPrefs.deviceId
        set(value) {
            if (field.isNullOrEmpty()) {
                field = value
                appPrefs.deviceId = value
            }
        }

    private var accountType: String? = appPrefs.accountType
        set(value) {
            if (field == value) return
            field = value
            appPrefs.accountType = value
        }

    //subjects
    private val codeSubject = PublishSubject.create<String>()


    private var userModel : UserModel? = null

    fun getUser() = userModel
    fun setUser(user : UserModel?)  {
        userId = user?.id
        userModel = user
    }

    fun getUserCity() = userTown ?: ""
    fun setUserCity(town: String) = run { userTown = town }

    fun setConfirmationCode(code : String) = codeSubject.onNext(code)
    fun getConfirmationCodeSubject(): PublishSubject<String> = codeSubject

    fun getSearchHistory() = appPrefs.search?.split(",") ?: emptyList()
    fun saveSearchHistory(text: String) {
        if (getSearchHistory().contains(text)) return
        val history = appPrefs.search
        appPrefs.search = if (history.isNullOrEmpty()) text else "$history,$text"
    }

    fun isUserAuthorized() = !userId.isNullOrEmpty()
    fun getUserId() = userId
}
package kg.autojuuguch.automoikakg.di.data

import android.annotation.SuppressLint
import android.content.Context

class AppPrefsImpl (val context: Context) : AppPrefs {
    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    override var userId: String?
        get() = prefs.getString(USER_ID, null)
        set(value) {
            prefs.edit().putString(USER_ID, value).apply()
        }


    override var token: String?
        get() = prefs.getString(TOKEN, null)
        set(value) {
            prefs.edit().putString(TOKEN, value).apply()
        }


    override var userCity: String?
        get() = prefs.getString(USER_CITY, null)
        set(value) {
            prefs.edit().putString(USER_CITY, value).apply()
        }

    override var deviceId: String?
        get() = prefs.getString(DEVICE_ID, null)
        set(value) {
            prefs.edit().putString(DEVICE_ID, value).apply()
        }

    override var search: String?
        get() = prefs.getString(SEARCH, null)
        set(value) {
            prefs.edit().putString(SEARCH, value).apply()
        }

    override var accountType: String?
        get() = prefs.getString(ACCOUNT_TYPE, null)
        set(value) {
            prefs.edit().putString(ACCOUNT_TYPE, value).apply()
        }


    companion object {
        const val TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_CITY = "user_city"
        const val DEVICE_ID = "user_device"
        const val SEARCH = "user_search"
        const val ACCOUNT_TYPE = "account_type"
    }
}
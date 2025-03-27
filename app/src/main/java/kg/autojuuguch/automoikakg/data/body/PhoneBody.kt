package kg.autojuuguch.automoikakg.data.body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneBody(
    val phone: String,
    val fcmToken: String
) : Parcelable



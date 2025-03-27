package kg.autojuuguch.automoikakg.data.body

import android.content.ContentResolver
import android.net.Uri
import android.os.Parcelable
import kg.autojuuguch.automoikakg.extensions.fileName
import kg.autojuuguch.automoikakg.extensions.getFileNameAndExtension
import kg.autojuuguch.automoikakg.utils.Utils.isPhoneNumberValid
import kotlinx.parcelize.Parcelize
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Parcelize
data class CarWashRegisterBody(
    var name: String = "",
    var description: String = "",

    var boxes: String = "",
    var type: String = "",

    var city: String = "",
    var street: String = "",
    var district: String = "",
    var way: String = "",
    var lat: String = "",
    var lon: String = "",

    var phone: String = "",
    var whatsapp: String = "",
    var instagram: String = "",

    var uri: Uri? = null
) : Parcelable {

    fun getFileName(contentResolver: ContentResolver): String {
        val nameAndExtension = (uri?.fileName(contentResolver)
            ?: uri.toString()).getFileNameAndExtension()

        val fileExtension = nameAndExtension.second
        return "image.${fileExtension}"
    }

    fun getRequestBody(contentResolver: ContentResolver, block: (body: RequestBody) -> Unit) {
        contentResolver.openInputStream(uri!!)?.buffered()
            ?.use { stream -> stream.readBytes() }?.let { bytes ->
                val body = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                block.invoke(body)
            }
    }

    fun getCarWashType(): String {
        return if (type == "Сам мой") "own-wash" else "cleaner-service"
    }

    fun isContactsValid(): Boolean {
        return city.isNotBlank() && street.isNotBlank() && isPhoneNumberValid(phone)
    }
}
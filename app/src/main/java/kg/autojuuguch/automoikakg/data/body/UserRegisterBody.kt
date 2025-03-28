package kg.autojuuguch.automoikakg.data.body

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kg.autojuuguch.automoikakg.extensions.fileName
import kg.autojuuguch.automoikakg.extensions.getFileNameAndExtension
import kg.autojuuguch.automoikakg.utils.Utils
import kotlinx.parcelize.Parcelize
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Parcelize
data class UserRegisterBody(
    var id : String? = null,
    var name: String = "",
    var phone: String = "",
    var password: String = ""
) : Parcelable {


    fun isComplete(): Boolean {
        return name.isNotBlank() && password.isNotBlank() && Utils.isPhoneNumberValid(phone)
    }

//    fun getFileName(contentResolver: ContentResolver): String {
//        val nameAndExtension = (uri?.fileName(contentResolver)
//            ?: uri.toString()).getFileNameAndExtension()
//
//        val fileExtension = nameAndExtension.second
//        return "image.${fileExtension}"
//    }
//
//    fun getRequestBody(contentResolver: ContentResolver, block: (body: RequestBody) -> Unit) {
//        contentResolver.openInputStream(uri!!)?.buffered()
//            ?.use { stream -> stream.readBytes() }?.let { bytes ->
//                val body = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
//                block.invoke(body)
//            }
//    }
}
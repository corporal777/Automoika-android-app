package kg.autojuuguch.automoikakg.utils.rxtakephoto

import android.graphics.Bitmap
import io.reactivex.subjects.SingleSubject

object CropCallbackHelper {

    private val cropRequests = mutableMapOf<String, SingleSubject<Bitmap>>()
    private lateinit var cropFinishedRequest : SingleSubject<Bitmap?>

    fun createRequest(key: String): SingleSubject<Bitmap> {
        cropRequests.clear()
        return cropRequests.getOrPut(key) { SingleSubject.create() }
    }

    fun getRequest(key: String): SingleSubject<Bitmap>? {
        return cropRequests[key]
    }

    fun createCropFinishedRequest(): SingleSubject<Bitmap> {
        return SingleSubject.create<Bitmap>().apply { cropFinishedRequest = this }
    }

    fun getCropFinishedRequest(): SingleSubject<Bitmap?> {
        return cropFinishedRequest
    }

}
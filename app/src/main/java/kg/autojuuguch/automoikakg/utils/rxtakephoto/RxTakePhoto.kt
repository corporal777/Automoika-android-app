package kg.autojuuguch.automoikakg.utils.rxtakephoto

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.core.RxImagePicker
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.Result
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.Observable
import kg.autojuuguch.automoikakg.exceptions.PermissionNotGrantedException
import kg.autojuuguch.automoikakg.utils.Utils
import kg.autojuuguch.automoikakg.utils.getGalleryImages
import java.io.IOException


class RxTakePhoto(val context: FragmentActivity) {

    private val rxPermissions = RxPermissions(context)
    private val rxImagePicker = RxImagePicker.create()

    fun takeFile(): Observable<Result> {
        return rxImagePicker.openFile(context)
    }

    fun takeImage(): Observable<Result> {
        return rxImagePicker.openGallery(context)
    }


    fun takeAllGalleryImages(): Maybe<MutableList<Uri>> {
        return getGalleryImages(context)
    }

    fun takeCameraImage(): Observable<ResultRotation> {
        return rxImagePicker.openCamera(context)
            .findRotation()
    }

    fun takeGalleryImage(): Observable<ResultRotation> {
        return rxImagePicker
            .openGallery(context)
            .findRotation()
    }

//    fun crop(
//        resultRotation: ResultRotation,
//        outputMaxWidth: Int = 0,
//        outputMaxHeight: Int = 0,
//        outputQuality: Int = 0,
//        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
//    ): Single<Bitmap> {
//        return crop(
//            resultRotation.uri,
//            resultRotation.rotation,
//            outputMaxWidth,
//            outputMaxHeight,
//            outputQuality,
//            cropMode
//        )
//    }
//
//    private fun crop(
//        uri: Uri,
//        rotation: Int = 0,
//        outputMaxWidth: Int = 0,
//        outputMaxHeight: Int = 0,
//        outputQuality: Int = 0,
//        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
//    ): Single<Bitmap> {
//        context.startActivity(
//            CropActivity.getStartIntent(
//                context,
//                uri,
//                rotation,
//                outputMaxWidth,
//                outputMaxHeight,
//                outputQuality,
//                cropMode
//            )
//        )
//        return CropCallbackHelper.createRequest(uri.toString())
//    }
//
    private fun Observable<Result>.findRotation(): Observable<ResultRotation> {
        return this.map { ResultRotation(it.uri, findImageRotation(it.uri)) }
    }

    private fun findImageRotation(uri: Uri): Int {
        return try {
            with(context.contentResolver.openInputStream(uri)!!) {
                val exifInterface = ExifInterface(this)

                var rotation = 0
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                }

                rotation
            }
        } catch (ignored: IOException) {
            0
        }
    }
}
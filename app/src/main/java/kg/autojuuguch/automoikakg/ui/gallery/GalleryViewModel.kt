package kg.autojuuguch.automoikakg.ui.gallery

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kg.autojuuguch.automoikakg.data.SingleLiveEvent
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.performOnBackgroundOutOnMain
import kg.autojuuguch.automoikakg.ui.base.BaseViewModel
import kg.autojuuguch.automoikakg.utils.rxtakephoto.ResultRotation
import kg.autojuuguch.automoikakg.utils.rxtakephoto.RxTakePhoto

class GalleryViewModel(private val appData: AppData) : BaseViewModel(appData) {

    private val _galleryImages = SingleLiveEvent<List<Uri>?>()
    val galleryImages: LiveData<List<Uri>?> get() = _galleryImages

    private val _croppedImage = SingleLiveEvent<Bitmap>()
    val croppedImage: LiveData<Bitmap> get() = _croppedImage

    private val _galleryImage = SingleLiveEvent<Uri>()
    val galleryImage: LiveData<Uri> get() = _galleryImage


    fun getGalleryImages(rxTakePhoto: RxTakePhoto){
        compositeDisposable += rxTakePhoto.takeAllGalleryImages()
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { _galleryImages.setValue(null) },
                onSuccess = { _galleryImages.setValue(it) }
            )
    }

    fun onGalleryClick(rxTakePhoto: RxTakePhoto) {
        compositeDisposable += rxTakePhoto.takeGalleryImage()
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext = { _galleryImage.setValue(it.uri) })
    }

    fun observeCropFinished(request: Single<Bitmap>) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .subscribeBy { _croppedImage.setValue(it) }
    }

}
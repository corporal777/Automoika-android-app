package kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.ui

import android.content.Context
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.sources.Camera
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.sources.Gallery
import io.reactivex.Observable
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.Result
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.sources.File

interface BasicImagePicker {

    @Gallery
    fun openGallery(context: Context): Observable<Result>

    @Camera
    fun openCamera(context: Context): Observable<Result>

    @File
    fun openFile(context: Context): Observable<Result>
}
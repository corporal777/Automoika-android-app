package kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.core

import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.ConfigProvider
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.sources.SourcesFrom
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.scheduler.IRxImagePickerSchedulers
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.ui.ActivityPickerViewController
import io.reactivex.Observable

class ConfigProcessor(private val schedulers: IRxImagePickerSchedulers) {

    fun process(configProvider: ConfigProvider): Observable<*> {
        return Observable.just(0)
            .flatMap {
                if (!configProvider.asFragment) {
                    return@flatMap ActivityPickerViewController.instance.pickImage()
                }
                when (configProvider.sourcesFrom) {
                    SourcesFrom.GALLERY,
                    SourcesFrom.FILE,
                    SourcesFrom.CAMERA -> configProvider.pickerView.pickImage()
                }
            }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
    }
}
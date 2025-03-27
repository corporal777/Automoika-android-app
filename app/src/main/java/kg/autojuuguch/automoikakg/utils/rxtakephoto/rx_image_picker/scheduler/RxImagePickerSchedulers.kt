package kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.scheduler

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.scheduler.IRxImagePickerSchedulers

class RxImagePickerSchedulers : IRxImagePickerSchedulers {

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}
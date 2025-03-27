package kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.function

import android.net.Uri
import kg.autojuuguch.automoikakg.utils.rxtakephoto.rx_image_picker.entity.Result

fun parseResultNoExtraData(uri: Uri): Result {
    return Result.Builder(uri).build()
}
package kg.autojuuguch.automoikakg.extensions

import androidx.fragment.app.Fragment
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.ui.dialogs.DefaultAlertDialog

fun Fragment.showActionDialog(text : String, block : () -> Unit){
    DefaultAlertDialog(
        requireContext(),
        null,
        text,
        getString(R.string.yes),
        getString(R.string.no)
    ).setSelectCallback { block.invoke() }
}
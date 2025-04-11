package kg.autojuuguch.automoikakg.ui.holders

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemSearchBinding

class SearchItem (
    val itemId: Int,
    format: String?,
    private val onFormatClick: (format: String) -> Unit
) : BindableItem<ItemSearchBinding>(itemId.toLong()) {

    private var maskText = format

    override fun bind(viewBinding: ItemSearchBinding, position: Int) {
        viewBinding.apply {
            if (itemId == 0) tvContent.text = "Искать «" + (maskText ?: "") + "»"
            else tvContent.text = maskText

            root.setOnClickListener {
                onFormatClick.invoke(maskText ?: "")
            }
        }
    }

    override fun bind(viewBinding: ItemSearchBinding, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is String) {
                maskText = payload
                viewBinding.tvContent.text = "Искать «" + (maskText ?: "") + "»"
            }
        }
    }




    override fun initializeViewBinding(view: View) = ItemSearchBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_search
}
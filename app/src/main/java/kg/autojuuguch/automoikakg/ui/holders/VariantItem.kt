package kg.autojuuguch.automoikakg.ui.holders

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.databinding.ItemStoryBinding
import kg.autojuuguch.automoikakg.databinding.ItemVariantBinding
import kg.autojuuguch.automoikakg.extensions.setImage

class VariantItem(
    private val variant: String,
    private val chosen: String?,
    private val onClick : (id: String) -> Unit
) : BindableItem<ItemVariantBinding>(variant.hashCode().toLong()) {

    private var isChosen = variant == chosen

    override fun bind(viewBinding: ItemVariantBinding, position: Int) {
        viewBinding.apply {
            tvVariant.text = variant
            scVariant.isChecked = isChosen
            root.setOnClickListener { onClick.invoke(variant) }
        }
    }


    override fun getLayout(): Int = R.layout.item_variant
    override fun initializeViewBinding(view: View) = ItemVariantBinding.bind(view)
}
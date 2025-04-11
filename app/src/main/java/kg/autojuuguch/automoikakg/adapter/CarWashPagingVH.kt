package kg.autojuuguch.automoikakg.adapter

import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.databinding.ItemListCarWashBinding
import kg.autojuuguch.automoikakg.extensions.getColorizedText
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.ui.views.BoxView

class CarWashPagingVH(
    private val itemView: View,
    private val onClick: (id: String) -> Unit
) : PagingVH(itemView) {

    private val viewBinding by viewBinding(ItemListCarWashBinding::bind)

    override fun bind(model: CarWashModel) {
        with(viewBinding) {
            itemContainer.apply {
                clipToOutline = true
                setOnClickListener { onClick.invoke(model.id) }
            }
            tvCarWashName.apply {
                text = SpannableStringBuilder().apply {
                    append("Автомойка - ")
                    append(getColorizedText(model.name ?: "", R.color.yellow))
                }
            }
            tvCarWashAddress.apply {
                val address =
                    context.getString(R.string.address_text, model.address?.getAddressStreet())
                text = address
            }
            ivLogo.setImage(model.getBackgroundImage())

            createBoxes(model.boxes?.free, model.boxes?.count?.toInt() ?: 0)
        }
    }

    private fun createBoxes(free: String?, count: Int) {
        viewBinding.lnBoxes.apply {
            removeAllViews()
            for (index in 0 until count) {
                val box = (index + 1).toString()
                val isContains = free?.contains(box) ?: false
                addView(BoxView(context).setData(box, isContains))
            }
        }
    }

    fun updateBoxes(free: String?, count: Int?) {
        if (viewBinding.lnBoxes.childCount <= 0) createBoxes(free, count ?: 0)
        else viewBinding.lnBoxes.children.forEach {
            if (it is BoxView) {
                val isContains = free?.contains(it.text) ?: false
                it.changeBackground(isContains)
            }
        }
    }
}
package kg.autojuuguch.automoikakg.ui.detail.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemCarWashLaberBinding

class CarWashLabelItem(val text : Int) : BindableItem<ItemCarWashLaberBinding>() {

    override fun bind(viewBinding: ItemCarWashLaberBinding, position: Int) {
        viewBinding.apply {
            tvLabel.text = root.context.getString(text)
        }
    }

    override fun getLayout(): Int = R.layout.item_car_wash_laber

    override fun initializeViewBinding(view: View) = ItemCarWashLaberBinding.bind(view)

}
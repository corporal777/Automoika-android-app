package kg.autojuuguch.automoikakg.ui.detail.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashDetailAddressModel
import kg.autojuuguch.automoikakg.databinding.ItemCarWashLocationBinding
import kg.autojuuguch.automoikakg.extensions.getStaticMapUrl
import kg.autojuuguch.automoikakg.extensions.setImage

class CarWashLocationItem(private val model: CarWashDetailAddressModel) :
    BindableItem<ItemCarWashLocationBinding>() {


    override fun bind(viewBinding: ItemCarWashLocationBinding, position: Int) {
        viewBinding.apply {
            tvWayDescription.text = model.wayDescription
            ivLocation.apply {
                val imageUrl = getStaticMapUrl(model.lat, model.lon)
                setImage(imageUrl)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_car_wash_location
    override fun initializeViewBinding(view: View) = ItemCarWashLocationBinding.bind(view)
}
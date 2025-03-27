package kg.autojuuguch.automoikakg.ui.detail.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.BuildConfig
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import kg.autojuuguch.automoikakg.databinding.ItemCarWashReviewBinding
import kg.autojuuguch.automoikakg.extensions.setImage

class CarWashReviewItem(
    private val model: CarWashReviewModel
) : BindableItem<ItemCarWashReviewBinding>() {

    override fun bind(viewBinding: ItemCarWashReviewBinding, position: Int) {
        viewBinding.apply {
            val avatar = if (model.image.contains("localhost:8080"))
                model.image.replace("localhost:8080", BuildConfig.API_URL)
            else model.image

            ivAvatar.setImage(avatar)
            tvReview.text = model.reviewText
        }
    }

    override fun getLayout(): Int = R.layout.item_car_wash_review
    override fun initializeViewBinding(view: View) = ItemCarWashReviewBinding.bind(view)
}
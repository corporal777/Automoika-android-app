package kg.autojuuguch.automoikakg.ui.detail.items

import android.view.View
import androidx.core.view.isVisible
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemAddCarWashReviewBinding

class CarWashAddReviewItem(private val isEmpty: Boolean) :
    BindableItem<ItemAddCarWashReviewBinding>() {

    override fun bind(viewBinding: ItemAddCarWashReviewBinding, position: Int) {
        viewBinding.apply {
            tvReviewEmpty.isVisible = isEmpty
            btnAddReview.isVisible = isEmpty
            tvAddReview.isVisible = !isEmpty
        }
    }

    override fun getLayout(): Int = R.layout.item_add_car_wash_review

    override fun initializeViewBinding(view: View) = ItemAddCarWashReviewBinding.bind(view)
}
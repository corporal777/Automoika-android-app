package kg.autojuuguch.automoikakg.ui.gallery

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemGalleryBinding
import kg.autojuuguch.automoikakg.extensions.setImage

class GalleryImageItem (
    private val id : Int,
    private val image: Uri,
    val onCameraClick: (uri : Uri) -> Unit
) : BindableItem<ItemGalleryBinding>(id.toLong()) {


    override fun bind(viewBinding: ItemGalleryBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                onCameraClick.invoke(image)
            }
            ivGalleryImage.apply {
                transitionName = image.toString()
                setImage(image)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is GalleryImageItem) return false
        if (image != other.image) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemGalleryBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_gallery
}
package kg.autojuuguch.automoikakg.ui.stories.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.databinding.ItemStoryBinding
import kg.autojuuguch.automoikakg.extensions.setImage

class StoryItem(
    private val model: StoriesModel,
    private val onClick : ((id: String, view : View) -> Unit?)?
) : BindableItem<ItemStoryBinding>(model.id.toLong()) {


    override fun bind(viewBinding: ItemStoryBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = model.title
            ivStory.apply {
                transitionName = model.id
                setImage(model.image)
            }
            root.setOnClickListener { onClick?.invoke(model.id, ivStory) }
        }
    }


    override fun getLayout(): Int = R.layout.item_story
    override fun initializeViewBinding(view: View) = ItemStoryBinding.bind(view)
}
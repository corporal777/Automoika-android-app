package kg.autojuuguch.automoikakg.ui.stories.items

import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.ui.holders.HorizontalListItem
import kg.autojuuguch.automoikakg.ui.holders.PlaceholderItem

class StoriesListItem(
    val list: List<StoriesModel?>,
    private val itemId: Long = -101L,
    private val onClick: ((id: String, view : View) -> Unit?)? = null
) : HorizontalListItem<GroupieViewHolder>(itemId) {

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(list.map { story ->
                if (story == null) PlaceholderItem(PlaceholderItem.Type.STORY)
                else StoryItem(story, onClick)
            })
        }
    }

    fun updateItems(list: List<StoriesModel>) {
        getAdapter().update(list.map { story -> StoryItem(story, onClick) })
    }


}
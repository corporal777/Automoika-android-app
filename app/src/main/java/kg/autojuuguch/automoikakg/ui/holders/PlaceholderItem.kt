package kg.autojuuguch.automoikakg.ui.holders

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kg.autojuuguch.automoikakg.R

class PlaceholderItem(
    private val type: Type
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout() = when (type) {
        Type.STORY -> R.layout.item_story_placeholder
    }

    enum class Type {
        STORY
    }
}
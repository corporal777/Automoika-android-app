package kg.autojuuguch.automoikakg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupieAdapter
import kg.autojuuguch.automoikakg.databinding.ItemHorizontalListBinding
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.ui.stories.items.StoryItem

class StoriesAdapter : CustomLoadStateAdapter<StoriesAdapter.StoriesViewHolder>() {

    var isNeedShowStories = false

    private val storiesList = arrayListOf<StoriesModel>()
    private val groupAdapter = GroupieAdapter()


    override fun getViewHolder(view: ViewGroup): StoriesViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return StoriesViewHolder(layoutInflater.inflate(R.layout.item_horizontal_list, view, false))
    }

    override fun getItemsCount(): Int = 1


    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        holder.bind()
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.NotLoading
    }

    inner class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemHorizontalListBinding::bind)

        fun bind() = with(viewBinding) {
            recyclerView.adapter = groupAdapter
        }
    }
}
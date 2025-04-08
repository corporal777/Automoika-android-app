package kg.autojuuguch.automoikakg.ui.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemHorizontalListBinding

open class HorizontalListItem<VH : RecyclerView.ViewHolder>(id : Long) : BindableItem<ItemHorizontalListBinding>(id) {

    var adapter: RecyclerView.Adapter<VH>? = null

    override fun bind(viewBinding: ItemHorizontalListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@HorizontalListItem.adapter
        }
    }

    fun getAdapter() = (adapter as GroupAdapter<*>)

    override fun getLayout() = R.layout.item_horizontal_list
    override fun initializeViewBinding(view: View) = ItemHorizontalListBinding.bind(view)
}
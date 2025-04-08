package kg.autojuuguch.automoikakg.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashBoxModel
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.databinding.ItemListCarWashBinding
import kg.autojuuguch.automoikakg.extensions.executePlaceholderLoadState
import kg.autojuuguch.automoikakg.extensions.getColorizedText
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.ui.views.BoxView

class CarWashPagingAdapter(val onClick: (id: String) -> Unit) :
    PagingDataAdapter<CarWashModel, CarWashPagingAdapter.CarWashPagingVH>(AsyncDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarWashPagingVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflater = layoutInflater.inflate(R.layout.item_list_car_wash, parent, false)
        return CarWashPagingVH(inflater)
    }

    override fun onBindViewHolder(holder: CarWashPagingVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onBindViewHolder(holder: CarWashPagingVH, pos: Int, payloads: MutableList<Any>) {
        val data = payloads.firstOrNull()
        if (data == null) super.onBindViewHolder(holder, pos, payloads)
        else if (data is CarWashModel) holder.updateBoxes(data.boxes.free, data.boxes.count.toInt())
    }


    fun updateCarWashItem(data: CarWashBoxModel) {
        for ((pos, local) in snapshot().items.withIndex()) {
            if (local.id == data.id) {
                if (!local.isFreeBoxesEqual(data)) {
                    local.boxes.free = data.freeBoxes
                    notifyItemChanged(pos, local)
                }
                break
            }
        }
//        snapshot().items.find { x -> x.id == data.id }.let { local ->
//            if (local == null) return
//            if (local.freeBoxes.joinToString(",") == data.freeBoxes) return
//
//            local.freeBoxes = data.freeBoxes.split(",")
//            val pos = snapshot().items.indexOf(local)
//            if (pos != -1) notifyItemChanged(pos, local)
//        }
    }


    inner class CarWashPagingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemListCarWashBinding::bind)

        fun bind(model: CarWashModel) {
            with(viewBinding) {
                itemContainer.apply {
                    clipToOutline = true
                    setOnClickListener { onClick.invoke(model.id) }
                }
                tvCarWashName.apply {
                    text = SpannableStringBuilder().apply {
                        append("Автомойка - ")
                        append(getColorizedText(model.name, R.color.yellow))
                    }
                }
                tvCarWashAddress.apply {
                    val address =
                        context.getString(R.string.address_text, model.address.getAddressStreet())
                    text = address
                }
                ivLogo.setImage(model.getBackgroundImage())

                createBoxes(model.boxes.free, model.boxes.count.toInt())
            }
        }

        private fun createBoxes(free: String, count: Int) {
            viewBinding.lnBoxes.apply {
                removeAllViews()
                for (index in 0 until count.toInt()) {
                    val box = (index + 1).toString()
                    val isContains = free.contains(box)
                    addView(BoxView(context).setData(box, isContains))
                }
            }
        }

        fun updateBoxes(free: String, count: Int) {
            if (viewBinding.lnBoxes.childCount <= 0) createBoxes(free, count)
            else viewBinding.lnBoxes.children.forEach {
                if (it is BoxView) {
                    val isContains = free.contains(it.text)
                    it.changeBackground(isContains)
                }
            }
        }
    }


    private object AsyncDiffCallback : DiffUtil.ItemCallback<CarWashModel>() {
        override fun areItemsTheSame(oldItem: CarWashModel, newItem: CarWashModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CarWashModel, newItem: CarWashModel): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        fun CarWashPagingAdapter.withLoadStateAdapters(
            storiesAdapter: RecyclerView.Adapter<*>?,
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addOnPagesUpdatedListener { }
            addLoadStateListener { loadState ->

                header.loadState = if (itemCount > 0) header.notRefresh else loadState.refresh
                footer.loadState = loadState.append

                executePlaceholderLoadState(loadState) { onEmpty.invoke(it) }
            }
            return if (storiesAdapter != null) ConcatAdapter(storiesAdapter, header, this, footer)
            else ConcatAdapter(header, this, footer)
        }
    }

}
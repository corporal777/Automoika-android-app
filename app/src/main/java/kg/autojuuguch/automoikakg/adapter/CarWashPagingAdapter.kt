package kg.autojuuguch.automoikakg.adapter

import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mobile.ads.nativeads.NativeAd
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

class CarWashPagingAdapter(private val onClick: (id: String) -> Unit) :
    PagingDataAdapter<CarWashModel, PagingVH>(AsyncDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingVH {
        val inflater: (Int) -> View = {
            LayoutInflater.from(parent.context).inflate(it, parent, false)
        }
        return when (viewType) {
            0 -> CarWashPagingVH(inflater.invoke(R.layout.item_list_car_wash), onClick)
            1 -> CarWashAdVH(inflater.invoke(R.layout.item_list_ad))
            else -> throw ClassCastException("Error $viewType type of item")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.additionalData == null) 0 else 1
    }

    override fun onBindViewHolder(holder: PagingVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onBindViewHolder(holder: PagingVH, pos: Int, payloads: MutableList<Any>) {
        val data = payloads.firstOrNull()
        if (data == null) super.onBindViewHolder(holder, pos, payloads)
        else if (data is CarWashModel) {
            (holder as CarWashPagingVH).updateBoxes(data.boxes?.free, data.boxes?.count?.toInt())
        }
    }


    fun updateCarWashItem(data: CarWashBoxModel) {
        for ((pos, local) in snapshot().items.withIndex()) {
            if (local.id == data.id) {
                if (!local.isFreeBoxesEqual(data)) {
                    local.boxes?.free = data.freeBoxes
                    notifyItemChanged(pos, local)
                }
                break
            }
        }
    }


    private object AsyncDiffCallback : DiffUtil.ItemCallback<CarWashModel>() {
        override fun areItemsTheSame(old: CarWashModel, new: CarWashModel): Boolean = old.id == new.id
        override fun areContentsTheSame(old: CarWashModel, new: CarWashModel): Boolean = old == new
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
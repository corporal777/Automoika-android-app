package kg.autojuuguch.automoikakg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.ItemListCarWashPlaceholderBinding


class CarWashPlaceholderAdapter(private val count: Int) :
    CustomLoadStateAdapter<CarWashPlaceholderAdapter.CarWashPlaceholderVH>() {


    override fun getViewHolder(view: ViewGroup): CarWashPlaceholderVH {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        val inflater = layoutInflater.inflate(R.layout.item_list_car_wash_placeholder, view, false)
        return CarWashPlaceholderVH(inflater)
    }

    override fun getItemsCount(): Int = count


    override fun onBindViewHolder(holder: CarWashPlaceholderVH, position: Int) {
        holder.bind()
    }


    inner class CarWashPlaceholderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemListCarWashPlaceholderBinding::bind)

        fun bind() {
            with(viewBinding) {}
        }
    }
}
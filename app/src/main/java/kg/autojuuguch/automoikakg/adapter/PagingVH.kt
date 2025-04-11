package kg.autojuuguch.automoikakg.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kg.autojuuguch.automoikakg.data.model.CarWashModel

open class PagingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun bind(model: CarWashModel){}
}
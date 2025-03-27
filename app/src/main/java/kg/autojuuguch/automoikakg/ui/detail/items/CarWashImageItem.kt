package kg.autojuuguch.automoikakg.ui.detail.items

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.xwray.groupie.viewbinding.BindableItem
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.databinding.ItemCarWashImageBinding
import kg.autojuuguch.automoikakg.extensions.getColorizedText
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.extensions.startIntent
import kg.autojuuguch.automoikakg.ui.views.BoxView
import kg.autojuuguch.automoikakg.utils.Utils


class CarWashImageItem(
    private val model: CarWashDetailModel,
) : BindableItem<ItemCarWashImageBinding>(model.id.toLong()) {


    override fun bind(viewBinding: ItemCarWashImageBinding, position: Int) {
        viewBinding.apply {
            tvName.text = SpannableStringBuilder().apply {
                append("Автомойка - ")
                append(tvName.getColorizedText(model.name, R.color.yellow))
            }
            tvAddress.text = model.address.getFullAddress()

            tvDescription.text = model.description

            ivImageDetail.setImage(model.backgroundImage.loadImage())

            btnCall.setOnClickListener { root.context.openCall() }
            lnBoxes.apply {
                removeAllViews()
                for (index in 0 until model.boxes.count.toInt()) {
                    val box = (index + 1).toString()
                    val isContains = model.boxes.free.split(",").contains(box)
                    addView(BoxView(context).setData(box, isContains))
                }
            }
            tvPhone.apply {
                if (model.contacts.phone.isNotBlank()){
                    text = Utils.formatMobilePhone(model.contacts.phone)
                    setOnClickListener { context.openCall() }
                } else text = context.getString(R.string.not_filled)
            }
            tvWhatsApp.apply {
                if (model.contacts.whatsapp.isNotBlank()){
                    text = Utils.formatMobilePhone(model.contacts.whatsapp)
                    setOnClickListener { context.openWhatsapp() }
                } else text = context.getString(R.string.not_filled)
            }
            tvInstagram.apply {
                if (model.contacts.instagram.isNotBlank()){
                    text = model.contacts.instagram
                    setOnClickListener { context.openInstagram() }
                } else text = context.getString(R.string.not_filled)
            }
        }
    }

    private fun Context.openCall() {
        startIntent(Intent.ACTION_DIAL) {
            val phone = model.contacts.phone
            setData(Uri.parse("tel:$phone"))
        }
    }

    private fun Context.openWhatsapp() {
        startIntent(Intent.ACTION_VIEW) {
            val url = "https://wa.me/" + model.contacts.whatsapp
            setData(Uri.parse(url))
        }
    }


    private fun Context.openInstagram() {
        startIntent(Intent.ACTION_VIEW) {
            val url = "https://www.instagram.com/" + model.contacts.instagram + "/"
            setData(Uri.parse(url))
        }
    }

    override fun getLayout(): Int = R.layout.item_car_wash_image
    override fun initializeViewBinding(view: View) = ItemCarWashImageBinding.bind(view)
}
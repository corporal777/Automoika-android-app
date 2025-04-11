package kg.autojuuguch.automoikakg.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import com.yandex.mobile.ads.nativeads.template.SizeConstraint
import com.yandex.mobile.ads.nativeads.template.appearance.BannerAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.ButtonAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.ImageAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.NativeTemplateAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.RatingAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.TextAppearance
import dev.androidbroadcast.vbpd.viewBinding
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.databinding.ItemListAdBinding
import kg.autojuuguch.automoikakg.databinding.ItemListCarWashBinding
import kg.autojuuguch.automoikakg.extensions.getColorizedText
import kg.autojuuguch.automoikakg.extensions.setImage
import kg.autojuuguch.automoikakg.ui.views.BoxView

class CarWashAdVH(private val itemView: View) : PagingVH(itemView) {

    private val viewBinding by viewBinding(ItemListAdBinding::bind)

    override fun bind(model: CarWashModel) {
        with(viewBinding) {
            val bannerAppearance = createNativeBannerViewAppearance(root.context)
            bannerView.applyAppearance(bannerAppearance)
            model.additionalData?.yandexAd?.let { bannerView.setAd(it) }
        }
    }

    private fun createNativeBannerViewAppearance(context: Context): NativeTemplateAppearance {
        return NativeTemplateAppearance.Builder()
            .withBannerAppearance(
                BannerAppearance.Builder()
                    .setBorderColor(Color.WHITE)
                    .setBackgroundColor(Color.WHITE)
                    .build()
            )
            .withCallToActionAppearance(
                ButtonAppearance.Builder()
                    .setTextAppearance(
                        TextAppearance.Builder()
                            .setTextColor(context.getColor(R.color.blue))
                            .setTextSize(14f).build()
                    )

                    .setNormalColor(Color.TRANSPARENT)
                    .setPressedColor(Color.GRAY)
                    .setBorderColor(context.getColor(R.color.blue))
                    .setBorderWidth(2f).build()
            )
            .withImageAppearance(
                ImageAppearance.Builder()
                    .setWidthConstraint(
                        SizeConstraint(
                            SizeConstraint.SizeConstraintType.FIXED,
                            60f
                        )
                    ).build()
            )
            .withAgeAppearance(
                TextAppearance.Builder()
                    .setTextColor(Color.GRAY)
                    .setTextSize(12f).build()
            )
            .withBodyAppearance(
                TextAppearance.Builder()
                    .setTextColor(Color.GRAY)
                    .setTextSize(12f).build()
            )
            .withRatingAppearance(
                RatingAppearance.Builder()
                    .setProgressStarColor(context.getColor(R.color.orange_yellow)).build()
            )
            .withTitleAppearance(
                TextAppearance.Builder()
                    .setTextColor(Color.BLACK)
                    .setTextSize(13f).build()
            )
            .build()
    }
}
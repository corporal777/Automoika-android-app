package kg.autojuuguch.automoikakg.utils

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader

class YandexAdUtils(private val context : Context) {

    //private var nativeAdLoader: NativeBulkAdLoader? = null
    private var nativeAdLoader: NativeAdLoader? = null
    private var nativeYandexAd : NativeAd? = null

    private var onLoaded : (ads : NativeAd) -> Unit = {}

    private val addLoadListener = object : NativeAdLoadListener {
        override fun onAdFailedToLoad(error: AdRequestError) {}
        override fun onAdLoaded(nativeAd: NativeAd) {
            nativeYandexAd = nativeAd
            nativeYandexAd?.setNativeAdEventListener(adEventListener)
            nativeYandexAd?.let { onLoaded.invoke(it) }
        }
    }

    private val adEventListener = object : NativeAdEventListener {
        override fun onAdClicked() {}
        override fun onImpression(impressionData: ImpressionData?) {}
        override fun onLeftApplication() {}
        override fun onReturnedToApplication() {}
    }

    init {
        nativeAdLoader = NativeAdLoader(context)
        //nativeAdLoader?.loadAds(NativeAdRequestConfiguration.Builder("R-M-14957705-1").build(), 3)
        //nativeAdLoader?.loadAd(NativeAdRequestConfiguration.Builder("demo-native-app-yandex").build())
        nativeAdLoader?.loadAd(NativeAdRequestConfiguration.Builder("R-M-14957705-1").build())
        nativeAdLoader?.setNativeAdLoadListener(addLoadListener)
    }

    fun setLoadCallback(block : (ad : NativeAd) -> Unit): YandexAdUtils {
        onLoaded = block
        return this
    }
}
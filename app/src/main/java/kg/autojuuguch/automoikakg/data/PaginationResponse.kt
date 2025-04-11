package kg.autojuuguch.automoikakg.data

import com.yandex.mobile.ads.nativeads.NativeAd

data class PaginationResponse<T>(
    val totalCount: Int,
    val data: List<T>,
    var yandexAd : NativeAd? = null
){
    fun isEmptyData() = data.isEmpty() && totalCount == 0
}

data class PaginationData<T>(
    val list: List<T>,
    val dataSize: Int,
    val totalCount: Int,
)
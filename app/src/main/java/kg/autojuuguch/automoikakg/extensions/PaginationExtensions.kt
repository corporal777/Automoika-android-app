package kg.autojuuguch.automoikakg.extensions

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.rxjava2.flowable
import kg.autojuuguch.automoikakg.utils.pagination.PagingDataSourceFactory
import kg.autojuuguch.automoikakg.utils.pagination.PagingList

fun <K : Any> PagingDataSourceFactory<K>.build(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    distance: Int = 5,
    enablePlaceholders: Boolean = false
): PagingList<K> {
    val config = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = initialSize,
        prefetchDistance = distance,
        enablePlaceholders = enablePlaceholders
    )
    val pager = Pager(config = config, pagingSourceFactory = { this.createDataSource() }).flowable
    return PagingList(pager, this)
}

fun PagingDataAdapter<*, *>.executePlaceholderLoadState(
    loadState: CombinedLoadStates,
    onEmpty: (show: Boolean) -> Unit
) {

    if (loadState.refresh is LoadState.Error || loadState.refresh is LoadState.NotLoading)
        if (this.snapshot().isEmpty()) onEmpty.invoke(true)
        else onEmpty.invoke(false)
    else onEmpty.invoke(false)
}
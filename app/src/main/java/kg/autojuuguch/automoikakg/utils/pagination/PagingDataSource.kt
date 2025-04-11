package kg.autojuuguch.automoikakg.utils.pagination

import android.os.Handler
import android.os.Looper
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import io.reactivex.Maybe
import io.reactivex.Single
import kg.autojuuguch.automoikakg.data.PaginationData
import kg.autojuuguch.automoikakg.data.PaginationResponse
import kg.autojuuguch.automoikakg.di.data.AppData
import org.koin.java.KoinJavaComponent.inject

open class PagingDataSource<I : Any> : RxPagingSource<Int, I>() {

    lateinit var request: (limit: Int, offset: Int) -> Maybe<PaginationData<I>>
    var errorHandler: PagingErrorHandler? = null

    private var loadFromStart = false
    private var lastRequestedDataSize = 0
    private var lastRequestedPage = 0
    private var lastRequestedKey = 0

    private fun getLimit(params: LoadParams<Int>): Int {
        return if (loadFromStart) {
            if (lastRequestedKey == 0) params.loadSize else params.loadSize * lastRequestedKey
        } else params.loadSize
    }

    private fun getOffset(params: LoadParams<Int>, limit: Int): Int {
        return if (loadFromStart) 0 else (params.key ?: 0) * limit
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, I>> {
        return try {
            val position = if (loadFromStart) lastRequestedKey else params.key ?: 0
            val limit = getLimit(params)
            val offset = getOffset(params, limit)

            request.invoke(limit, offset).flatMapSingle {
                val prevKey = null
                val nextKey = if (it.list.isEmpty()) null
                else if (it.dataSize < limit || it.totalCount <= limit) null
                else if (it.dataSize == it.totalCount) null
                else if (loadFromStart) position
                else position + 1

                loadFromStart = false
                Single.just(toLoadResult(it.list, prevKey, nextKey))
            }.doOnError { executeError(it) }.onErrorResumeNext { Single.just(LoadResult.Error(it)) }
        } catch (e: Exception) {
            executeError(e)
            Single.just(LoadResult.Error(e))
        }

    }

    override fun getRefreshKey(state: PagingState<Int, I>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        val anchorNextKey = anchorPage.nextKey ?: 0

        loadFromStart = true

        if (anchorNextKey > 0) {
            lastRequestedKey = anchorPage.nextKey ?: 0
            lastRequestedDataSize = 3 * state.config.pageSize
            lastRequestedPage = 2
        } else {
            lastRequestedDataSize = state.config.pageSize
            lastRequestedPage = 0
        }
        return if (loadFromStart) 0
        else anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
        //return 0
    }

    open fun toLoadResult(data: List<I>, prev: Int?, next: Int?): LoadResult<Int, I> {
        return LoadResult.Page(data = data, prevKey = prev, nextKey = next)
    }

    private fun executeError(t: Throwable) {
        val errorHandler = errorHandler ?: throw t
        Handler(Looper.getMainLooper()).post { errorHandler(t) }
    }

    fun invalidateFromStart() {
        invalidate()
    }
}

typealias PagingErrorHandler = (Throwable) -> Unit
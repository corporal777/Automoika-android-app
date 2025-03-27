package kg.autojuuguch.automoikakg.utils.pagination

import io.reactivex.Maybe
import kg.autojuuguch.automoikakg.data.PaginationResponse

open class PagingDataSourceFactory<I : Any>(
    private val paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
)  {

    var source : PagingDataSource<I>? = null
    var paginationErrorHandler : PagingErrorHandler? = null

    fun createDataSource(): PagingDataSource<I> {
        val source = PagingDataSource<I>().apply {
            this.request = paginationRequest
            this.errorHandler = paginationErrorHandler
        }
        this.source = source
        return source
    }
}
fun <I : Any> PagingDataSourceFactory<I>.applyErrorHandler(handler: PagingErrorHandler): PagingDataSourceFactory<I> {
    paginationErrorHandler = handler
    return this
}
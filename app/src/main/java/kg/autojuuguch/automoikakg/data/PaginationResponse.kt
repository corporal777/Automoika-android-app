package kg.autojuuguch.automoikakg.data

data class PaginationResponse<T>(
    val totalCount: Int,
    val data: List<T>
){
    fun isEmptyData() = data.isEmpty() && totalCount == 0
}
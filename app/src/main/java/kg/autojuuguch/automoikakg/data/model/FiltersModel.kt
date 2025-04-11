package kg.autojuuguch.automoikakg.data.model

data class FiltersModel(
    val district : String? = null,
    val type : String? = null,
    val onlyFree : Boolean = false
){
    fun isHasFilters(): Boolean {
        return !district.isNullOrBlank() || !type.isNullOrBlank() || onlyFree
    }

    fun typeFromServer() : String? {
        if (type == "own-wash") return "Сам мой"
        else if (type == "cleaner-service") return "Услуги мойщика"
        else return null
    }
}
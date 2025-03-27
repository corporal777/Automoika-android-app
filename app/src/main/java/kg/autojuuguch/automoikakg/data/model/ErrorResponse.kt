package kg.autojuuguch.automoikakg.data.model

data class ErrorResponse(val message: String){

    companion object{
        const val USER_ALREADY_EXISTS_ERROR = "User with this email already exists!"
        const val USER_ALREADY_CREATED_ERROR = "User already created!"
    }
}
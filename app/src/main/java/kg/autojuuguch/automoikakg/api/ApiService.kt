package kg.autojuuguch.automoikakg.api

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import kg.autojuuguch.automoikakg.data.model.CarWashModel
import kg.autojuuguch.automoikakg.data.PaginationResponse
import kg.autojuuguch.automoikakg.data.body.CodeBody
import kg.autojuuguch.automoikakg.data.body.GoogleUserBody
import kg.autojuuguch.automoikakg.data.body.LocationBody
import kg.autojuuguch.automoikakg.data.body.LoginBody
import kg.autojuuguch.automoikakg.data.body.PhoneBody
import kg.autojuuguch.automoikakg.data.body.UserRegisterBody
import kg.autojuuguch.automoikakg.data.model.CarWashDetailModel
import kg.autojuuguch.automoikakg.data.model.CarWashReviewModel
import kg.autojuuguch.automoikakg.data.model.CityLocationModel
import kg.autojuuguch.automoikakg.data.model.StoriesModel
import kg.autojuuguch.automoikakg.data.model.UserModel
import kg.autojuuguch.automoikakg.data.model.YandexGeoModel
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    //auth
    @POST("v1/login-user")
    fun login(@Body map: LoginBody): Maybe<UserModel>

    @POST("v1/logout-user/{id}")
    fun logout(@Path("id") id: String): Completable

    @POST("v1/check-phone-exists")
    fun checkPhoneExists(@Body map: Map<String, String>): Maybe<String>

    @POST("v1/send-code")
    fun sendCode(@Body map: PhoneBody): Completable

    @POST("v1/confirm-code")
    fun confirmCode(@Body map: CodeBody): Completable

    //user
    @POST("v1/create-user")
    fun registerUser(@Body body: UserRegisterBody): Maybe<UserModel>

    @POST("v1/create-google-user")
    fun registerGoogleUser(@Body body: GoogleUserBody): Maybe<UserModel>

    @GET("v1/user/{id}")
    fun getUserById(@Path("id") id: String): Maybe<UserModel>

    //car wash
    @GET("v1/car-wash-list")
    fun getCarWashList(@QueryMap map: Map<String, String>): Maybe<PaginationResponse<CarWashModel>>

    @GET("v1/car-wash-detail/{id}")
    fun getCarWashById(
        @Path("id") id: String,
        @Query("binds") binds: String
    ): Maybe<CarWashDetailModel>

    @GET("v1/car-wash-reviews/{id}")
    fun getCarWashReviews(@Path("id") id: String): Maybe<PaginationResponse<CarWashReviewModel>>

    //location
    @POST("v1/check-user-location")
    fun getCityFromLocation(@Body body: LocationBody): Maybe<CityLocationModel>

    @POST("v1/location-info")
    fun getAddressInfoFromLatLng(@Body body: LocationBody): Maybe<YandexGeoModel>

    //stories
    @GET("v1/stories")
    fun getStories(): Maybe<List<StoriesModel>>

}
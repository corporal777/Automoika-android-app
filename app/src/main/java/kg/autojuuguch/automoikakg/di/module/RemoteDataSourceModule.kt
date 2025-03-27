package kg.autojuuguch.automoikakg.di.module

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import kg.autojuuguch.automoikakg.BuildConfig
import kg.autojuuguch.automoikakg.api.ApiService
import kg.autojuuguch.automoikakg.api.AuthInterceptor
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.extensions.isConnectedToNetwork
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

val remoteDataSourceModule = module {

    fun provideGson(): Gson = GsonBuilder().serializeNulls().setLenient().create()
    fun provideAuthInterceptor(appData: AppData) = AuthInterceptor(appData)

    fun provideHttpClient(context: Context, authInterceptor: AuthInterceptor): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        clientBuilder.addInterceptor(authInterceptor)

        val logInterceptor = HttpLoggingInterceptor { message ->
            Log.e("REQUEST INFO", message)
        }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(logInterceptor)

        clientBuilder.addNetworkInterceptor {
            val response = it.proceed(it.request())
            val cacheControl: CacheControl = if (context.isConnectedToNetwork()) {
                CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }

            return@addNetworkInterceptor response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        clientBuilder.addInterceptor {
            var request = it.request()

            if (!context.isConnectedToNetwork()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .cacheControl(cacheControl)
                    .build()
            }

            return@addInterceptor it.proceed(request)
        }

        return clientBuilder.build()
    }

    fun provideApiService(httpClient: OkHttpClient, gson: Gson): ApiService {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(ApiService::class.java)
    }

    single { provideGson() }
    single { provideAuthInterceptor(get()) }
    single { provideHttpClient(androidContext(), get()) }
    single { provideApiService(get(), get()) }
}
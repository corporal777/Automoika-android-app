package kg.autojuuguch.automoikakg

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.mapkit.MapKitFactory
import com.yandex.mobile.ads.common.MobileAds
import kg.autojuuguch.automoikakg.di.module.appDataModule
import kg.autojuuguch.automoikakg.di.module.remoteDataSourceModule
import kg.autojuuguch.automoikakg.di.module.repositoryModule
import kg.autojuuguch.automoikakg.di.module.socketModule
import kg.autojuuguch.automoikakg.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) {}
        MapKitFactory.setApiKey(getString(R.string.yandex_map_api))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(applicationContext)

            modules(appDataModule, repositoryModule, remoteDataSourceModule, viewModelModule, socketModule)
        }
    }
}
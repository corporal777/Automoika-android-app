package kg.autojuuguch.automoikakg.di.module

import android.content.ContentResolver
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kg.autojuuguch.automoikakg.di.data.AppData
import kg.autojuuguch.automoikakg.di.data.AppPrefs
import kg.autojuuguch.automoikakg.di.data.AppPrefsImpl
import kg.autojuuguch.automoikakg.utils.NotificationUtil
import kg.autojuuguch.automoikakg.utils.rxtakephoto.RxTakePhoto
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appDataModule = module {

    fun provideAppPrefs(context: Context): AppPrefs = AppPrefsImpl(context)
    fun provideAppData(appPrefs: AppPrefs, context: Context): AppData = AppData(appPrefs, context)

    fun provideNotificationUtil(context: Context, appData: AppData) = NotificationUtil(context, appData)
    fun provideContentResolver(context: Context) : ContentResolver = context.contentResolver

    single { provideAppPrefs(androidContext()) }
    single { provideAppData(get(), androidContext()) }
    single { provideNotificationUtil(androidContext(), get()) }
    single { provideContentResolver(androidContext()) }

    single<RxTakePhoto> { (view: FragmentActivity) -> RxTakePhoto(view) }
    single<RxPermissions> { (view: FragmentActivity) -> RxPermissions(view) }
}
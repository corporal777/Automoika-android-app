package kg.autojuuguch.automoikakg.di.module

import kg.autojuuguch.automoikakg.ui.auth.authorization.AuthorizationViewModel
import kg.autojuuguch.automoikakg.ui.auth.login.LoginViewModel
import kg.autojuuguch.automoikakg.ui.auth.registerUser.UserRegisterViewModel
import kg.autojuuguch.automoikakg.ui.auth.welcome.WelcomeViewModel
import kg.autojuuguch.automoikakg.ui.city.CityViewModel
import kg.autojuuguch.automoikakg.ui.confirm.ConfirmCodeViewModel
import kg.autojuuguch.automoikakg.ui.detail.CarWashDetailViewModel
import kg.autojuuguch.automoikakg.ui.gallery.GalleryViewModel
import kg.autojuuguch.automoikakg.ui.home.HomeViewModel
import kg.autojuuguch.automoikakg.ui.main.MainViewModel
import kg.autojuuguch.automoikakg.ui.map.MapViewModel
import kg.autojuuguch.automoikakg.ui.map.address.MapAddressViewModel
import kg.autojuuguch.automoikakg.ui.profile.ProfileViewModel
import kg.autojuuguch.automoikakg.ui.splash.SplashViewModel
import kg.autojuuguch.automoikakg.ui.stories.StoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { CarWashDetailViewModel(get(), get()) }
    viewModel { MapViewModel(get(), get(), get()) }
    viewModel { MapAddressViewModel(get(), get()) }
    viewModel { CityViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ConfirmCodeViewModel(get(), get(), get(), get()) }
    viewModel { AuthorizationViewModel(get(), get()) }
    viewModel { GalleryViewModel(get()) }
    viewModel { UserRegisterViewModel(get(), get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { StoriesViewModel(get()) }
}
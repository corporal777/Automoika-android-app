package kg.autojuuguch.automoikakg.di.module

import kg.autojuuguch.automoikakg.di.repository.AuthRepository
import kg.autojuuguch.automoikakg.di.repository.AuthRepositoryImpl
import kg.autojuuguch.automoikakg.di.repository.CarWashRepository
import kg.autojuuguch.automoikakg.di.repository.CarWashRepositoryImpl
import kg.autojuuguch.automoikakg.di.repository.LocationRepository
import kg.autojuuguch.automoikakg.di.repository.LocationRepositoryImpl
import kg.autojuuguch.automoikakg.di.repository.UserRepository
import kg.autojuuguch.automoikakg.di.repository.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<CarWashRepository> { CarWashRepositoryImpl(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}
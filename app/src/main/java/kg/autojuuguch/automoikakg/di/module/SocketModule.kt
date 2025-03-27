package kg.autojuuguch.automoikakg.di.module

import kg.autojuuguch.automoikakg.api.socket.SocketIOManager
import kg.autojuuguch.automoikakg.api.socket.SocketIOManagerImpl
import org.koin.dsl.module

val socketModule = module {
    single<SocketIOManager> { SocketIOManagerImpl(get()) }
}
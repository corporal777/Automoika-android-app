package kg.autojuuguch.automoikakg.api.socket

import io.reactivex.Flowable
import kg.autojuuguch.automoikakg.data.model.CarWashBoxModel

interface SocketIOManager {

    fun connect() : Flowable<SocketConnectionState>
    fun subscribeFreeBoxesMessage() : Flowable<CarWashBoxModel>
    fun disconnect()
}
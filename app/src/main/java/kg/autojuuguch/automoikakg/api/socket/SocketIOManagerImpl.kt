package kg.autojuuguch.automoikakg.api.socket

import com.google.gson.Gson
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import kg.autojuuguch.automoikakg.BuildConfig
import kg.autojuuguch.automoikakg.data.model.CarWashBoxModel
import kg.autojuuguch.automoikakg.di.data.AppData
import org.java_websocket.client.WebSocketClient
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException
import java.nio.ByteBuffer

class SocketIOManagerImpl(private val appData: AppData) : SocketIOManager {

    private var mSocket: WebSocketClient? = null
    private var statusSubject = PublishSubject.create<SocketConnectionState>()
    private var messageSubject = PublishSubject.create<CarWashBoxModel>()


    override fun connect(): Flowable<SocketConnectionState> {
        try {
            val uri = URI(BuildConfig.SOCKET_URL + "car-wash-point")
            mSocket = ClientWebSocket(uri, object : OnSocketClientListener {
                override fun onActionOpen() {
                    statusSubject.onNext(SocketConnectionState.CONNECTED)
                }

                override fun onActionClose() {
                    statusSubject.onNext(SocketConnectionState.DISCONNECT)
                }

                override fun onActionMessage(message: String?) {
                    val data = try {
                        Gson().fromJson(message, CarWashBoxModel::class.java)
                    } catch (e : Exception){
                        null
                    }
                    messageSubject.onNext(data!!)
                }

                override fun onActionMessage(bytes: ByteBuffer?) {
                }

                override fun onActionError() {
                    statusSubject.onNext(SocketConnectionState.ERROR)
                }

            })
            mSocket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return statusSubject.toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun subscribeFreeBoxesMessage(): Flowable<CarWashBoxModel> {
        return messageSubject.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun disconnect() {
        mSocket?.close()
    }

}
package kg.autojuuguch.automoikakg.api.socket

import io.reactivex.subjects.PublishSubject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class ClientWebSocket(
    serverUri: URI,
    val listener: OnSocketClientListener
) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) = listener.onActionOpen()

    override fun onClose(code: Int, reason: String?, remote: Boolean) = listener.onActionClose()

    override fun onMessage(message: String?) = listener.onActionMessage(message)

    override fun onMessage(bytes: ByteBuffer?) = listener.onActionMessage(bytes)

    override fun onError(ex: Exception?) = listener.onActionError()
}

interface OnSocketClientListener {
    fun onActionOpen()
    fun onActionClose()
    fun onActionMessage(message: String?)
    fun onActionMessage(bytes: ByteBuffer?)
    fun onActionError()
}
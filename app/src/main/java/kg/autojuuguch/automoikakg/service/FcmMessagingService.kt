package kg.autojuuguch.automoikakg.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kg.autojuuguch.automoikakg.utils.NotificationUtil
import org.koin.android.ext.android.inject

class FcmMessagingService : FirebaseMessagingService() {

    private val notificationUtil by inject<NotificationUtil>()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        processMessage(remoteMessage)
    }

    private fun processMessage(remoteMessage: RemoteMessage) {
        notificationUtil.createSimpleNotification(
            mapOf(
                DATA_TITLE to remoteMessage.notification?.title,
                DATA_BODY to remoteMessage.notification?.body,
                DATA_SIMPLE_NOTIFICATION_ID to "111"
            )
        )
    }


    companion object {
        private const val DATA_TITLE = "title"
        private const val DATA_BODY = "body"
        private const val DATA_SIMPLE_NOTIFICATION_ID = "code"
        private const val DATA_NOTE_ID = "note_id"

    }
}
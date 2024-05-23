package com.athsoftware.hrm.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.BADGE_ICON_LARGE
import com.athsoftware.hrm.App
import com.athsoftware.hrm.MainActivity
import com.athsoftware.hrm.R
import com.athsoftware.hrm.common.Constants
import com.athsoftware.hrm.helper.PrefHelper
import com.athsoftware.hrm.network.APIClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import java.util.*

class HRMFCMInstanceMessage : FirebaseMessagingService() {

    companion object {
        fun registerToServer(token: String) {
            val deviceId = PrefHelper.shared.getStringValue(Constants.Key.PREF_UNIQUE_ID)
                    ?: UUID.randomUUID().toString()
            App.userId?.let { userId ->
                APIClient.shared.api.registerPushNotification(deviceId, userId, token)
                        .subscribeOn(Schedulers.io())
                        .subscribe({}, {})
            }
        }

        fun unregisterToServer(token: String) {
            val deviceId = PrefHelper.shared.getStringValue(Constants.Key.PREF_UNIQUE_ID)
                    ?: UUID.randomUUID().toString()
            val pos = APIClient.shared.api.unregisterPushNotification(deviceId, App.userId, token)
                    .subscribeOn(Schedulers.io())
                    .subscribe({}, {})
        }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        if (App.shared().auth == null) {
            return
        }
        message?.notification?.body?.let {
            var data: String? = null
            message.data?.let {
                data = Gson().toJson(it)
            }
            sendNotification(message.notification?.title
                    ?: App.shared().getString(R.string.app_name), it, data)
        }
    }

    private fun sendNotification(title: String = App.shared().getString(R.string.app_name), messageBody: String, data: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = Constants.Extra.OPEN_MESSAGE
        data?.let {
            intent.putExtra("data", data)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setBadgeIconType(BADGE_ICON_LARGE)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val channelName = getString(R.string.default_notification_channel_id)
            val chanel = NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT)
            chanel.description = messageBody
            chanel.enableVibration(true)
            chanel.enableLights(true)
            notificationManager.createNotificationChannel(chanel)
        }

        notificationManager.notify((System.currentTimeMillis() % 1000000).toInt() /* ID of notification */, notificationBuilder.build())
    }


    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        token?.let {
            registerToServer(it)
        }
    }
}
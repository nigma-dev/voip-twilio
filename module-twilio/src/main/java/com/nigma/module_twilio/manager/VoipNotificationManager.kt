package com.nigma.module_twilio.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.nigma.module_twilio.R
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.utils.ACTION_VOIP_DISCONNECT_ROOM
import timber.log.Timber


class VoipNotificationManager(
    private val service: VoipService
) {

    private val context: Context
        get() = service.applicationContext

    private val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }


    fun initialForeground() {
        val noti = createNotification("MyanCare", "Voip call service is running ...")
            .setContentIntent(createContentIntent())
            .setSmallIcon(R.drawable.myancare_logo_white)
            .build()
        notificationManager.notify(1, noti)
        service.startForeground(1, noti)
    }

    fun notifyConnectVoipError() {
        val noti = createNotification(
            "MyanCare",
            "Error occur when connecting voip service, Please contact MyanCare !"
        )
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .build()
        notificationManager.notify(1, noti)
    }

    fun notifyHungUp() {
        Timber.v("notifyTalkingCall")
        /*val noti = createNotification(opponentUser.name, "call ended")
            .setSmallIcon(R.drawable.ic_end_call)
            .build()
        notificationManager.notify(1, noti)*/
    }

    fun notifyIncomingCall() {
        Timber.v("notifyIncomingCall")
        /*val hangUpIntent = createActionIntent(ACTION_CALL_HUNGUP)
        val pickUpIntent = createActionIntent(ACTION_CALL_PICKUP)
        val noti = createNotification(opponentUser.name, "is calling ..")
            .setSmallIcon(R.drawable.ic_answer_call)
            .setContentIntent(createContentIntent())
            .addAction(R.drawable.ic_answer_call, "answer", pickUpIntent)
            .addAction(R.drawable.ic_end_call, "cancel", hangUpIntent)
            .setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                AudioManager.STREAM_VOICE_CALL
            )
            .build()
        notificationManager.notify(1, noti)
        service.startForeground(1, noti)*/
    }

    fun notifyOutgoingCall() {
        Timber.v("notifyOutgoingCall")
        /*val noti = createNotification(opponentUser.name, "calling ...")
            .setContentIntent(createContentIntent())
            .setSmallIcon(R.drawable.ic_answer_call)
            .addAction(R.drawable.ic_end_call, "end", createActionIntent(ACTION_CALL_CANCEL))
            .setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.outgoing_tone}"))
            .build()
        notificationManager.notify(1, noti)
        service.startForeground(1, noti)*/
    }

    fun notifyTalkingCall(name: String) {
        Timber.v("notifyTalkingCall")
        val noti = createNotification(name, "is talking with you")
            .setSmallIcon(R.drawable.ic_answer_call)
            .setContentIntent(createContentIntent())
            .setWhen(System.currentTimeMillis())  // the time stamp, you will probably use System.currentTimeMillis() for most scenarios
            .setUsesChronometer(true)
            .addAction(
                R.drawable.ic_end_call,
                "end",
                createActionIntent(ACTION_VOIP_DISCONNECT_ROOM)
            )
            .build()
        notificationManager.notify(1, noti)
        service.startForeground(1, noti)
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(context, VoipService::class.java)
            .apply {
                this.action = action
            }
        return PendingIntent.getService(context, 0, intent, 0)
    }

    private fun createContentIntent(): PendingIntent {
        val intent = with(Intent(context, VoipActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return@with this
        }
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun createNotification(
        title: String,
        content: String
    ): NotificationCompat.Builder {
        Timber.v("createNotification")
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            NotificationCompat.Builder(context, VOIP_CALL_CHANNEL)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(context)
        }
        builder.priority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_MAX
        } else {
            @Suppress("DEPRECATION")
            Notification.PRIORITY_MAX
        }
        return builder
            .setContentTitle(title)
            .setContentText(content)
            .setCategory(NotificationCompat.CATEGORY_CALL)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        Timber.v("createNotificationChannel")
        val channel = NotificationChannel(
            VOIP_CALL_CHANNEL,
            VOIP_CALL_CHANNEL,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

}

const val VOIP_CALL_CHANNEL = "VOIP-CALL-CHANNEL"
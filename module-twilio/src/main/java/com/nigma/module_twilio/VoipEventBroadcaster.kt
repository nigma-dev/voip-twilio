package com.nigma.module_twilio

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber
import java.lang.ref.WeakReference

class VoipEventBroadcaster(
    private val reference: WeakReference<Context>
) {
    private val localBroadcastManager: LocalBroadcastManager?
        get() {
            return reference.get()?.let { LocalBroadcastManager.getInstance(it.applicationContext) }
        }

    fun broadcastServiceStopAction() = sendBroadcast(ACTION_SERVICE_STOP)

    fun broadcastAudioPermission() = sendBroadcast(ACTION_REQUIRE_AUDIO_PERMISSION)

    fun broadcastCameraPermission() = sendBroadcast(ACTION_REQUIRE_CAM_PERMISSION)

    private fun sendBroadcast(action: String) {
        Timber.i("sendBroadcast : $action | $localBroadcastManager")
        localBroadcastManager
            ?.sendBroadcast(
                Intent(action)
            )
    }
}

const val ACTION_SERVICE_STOP = "broadcast:ACTION_SERVICE_STOP"
const val ACTION_REQUIRE_AUDIO_PERMISSION = "broadcast:ACTION_REQUIRE_AUDIO_PERMISSION"
const val ACTION_REQUIRE_CAM_PERMISSION = "broadcast:ACTION_REQUIRE_CAM_PERMISSION"
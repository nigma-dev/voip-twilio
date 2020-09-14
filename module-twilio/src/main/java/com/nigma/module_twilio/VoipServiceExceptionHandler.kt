package com.nigma.module_twilio

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nigma.module_twilio.exception.AudioPermissionException
import com.nigma.module_twilio.exception.CameraPermissionException
import timber.log.Timber

class VoipServiceExceptionHandler(
    private val voipService: VoipService
) : Thread.UncaughtExceptionHandler {


    private val broadcaster
        get() = LocalBroadcastManager.getInstance(voipService.applicationContext)

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Timber.e(p1, p0.name)
        voipService.stopSelf()
    }

    fun handleException(e: Exception) {
        Timber.e(e)
        when (e) {
            is AudioPermissionException -> {
                broadcaster.sendBroadcast(Intent("audio"))
            }
            is CameraPermissionException -> {
                broadcaster.sendBroadcast(Intent("camera"))
            }
        }
        voipService.stopSelf()
    }
}
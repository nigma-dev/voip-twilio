package com.nigma.module_twilio

import com.nigma.module_twilio.exception.AudioPermissionException
import com.nigma.module_twilio.exception.CameraPermissionException
import timber.log.Timber

class VoipServiceExceptionHandler(
    private val voipService: VoipService
) : Thread.UncaughtExceptionHandler {


    private val broadcaster
        get() = voipService.broadcaster


    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Timber.e(p1)
        voipService.stopSelf()
    }

    fun handleException(e: Exception) {
        Timber.e(e)
        when (e) {
            is AudioPermissionException -> {
                broadcaster.broadcastAudioPermission()
            }
            is CameraPermissionException -> {
                broadcaster.broadcastCameraPermission()
            }
        }
        voipService.stopForeground(true)
        voipService.stopSelf()
    }
}
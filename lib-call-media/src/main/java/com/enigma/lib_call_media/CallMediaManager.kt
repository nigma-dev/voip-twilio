package com.enigma.lib_call_media

import android.content.Context
import timber.log.Timber
import java.lang.ref.WeakReference

class CallMediaManager(
    reference: WeakReference<Context>
) {
    private val vibrateManager by lazy {
        reference.get()?.let { context ->
            VibrateManager(context)
        }
    }
    private val wakeLockManager by lazy {
        reference.get()?.let { context ->
            WakeLockManager(context)
        }
    }

    private val toneManager by lazy {
        reference.get()?.let { context ->
            ToneManager(context)
        }
    }

    fun callConnecting() {
        Timber.i("callConnecting")
        toneManager?.playConnectingTone()
        wakeLockManager?.wakeUp()
    }

    fun callConnected() {
        Timber.i("callConnected")
        toneManager?.playConnectedTone()
        vibrateManager?.start()
        vibrateManager?.stop()
    }

    fun callReconnected() {
        callConnected()
    }

    fun callParticipantConnected() {
        Timber.i("callParticipantConnected")
        callConnected()
    }

    fun release() {
        Timber.i("release")
        vibrateManager?.stop()
        wakeLockManager?.release()
        toneManager?.release()
    }
}
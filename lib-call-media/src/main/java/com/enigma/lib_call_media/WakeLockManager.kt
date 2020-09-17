package com.enigma.lib_call_media

import android.content.Context
import android.os.PowerManager

class WakeLockManager(private val context: Context) {

    private lateinit var pm: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    fun keepBrightScreen() {
        initWakeLocker()
        with(wakeLock) {
            if (!::wakeLock.isInitialized) {
                wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, ":voip")
            }
            if (isHeld) release()
            acquire(60 * 1000L)
        }
    }

    fun release() {
        if (!::wakeLock.isInitialized) return
        if (!wakeLock.isHeld) return
        wakeLock.release()
    }

    fun wakeUp() {
        initWakeLocker()
        with(pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":voip")) {
            if (!::wakeLock.isInitialized) {
                wakeLock = this
            }
            if (isHeld) release()
            acquire(60 * 1000L)
        }
    }

    private fun initWakeLocker() {
        if (!::pm.isInitialized) {
            pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        }
    }
}
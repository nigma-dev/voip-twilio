package com.enigma.lib_call_media

import android.content.Context
import android.os.Vibrator

class VibrateManager(
    private val context: Context
) {

    private val mVibratePattern = longArrayOf(0, 400, 0, 600)
    private val vibrator by lazy { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    fun start() {
        try {
            @Suppress("DEPRECATION")
            vibrator.vibrate(mVibratePattern, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.nigma.lib_audio_router.new

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager

class ARWiredHeadsetManager(
    private val am: AudioManager,
    val callback: () -> Unit
) {

    var hasBluetoothHeadset = false
        @Synchronized
        get() {
            return isAudioJackConnected()
        }
        @Synchronized
        private set(value) {
            callback()
            field = value
        }

    private val receiver by lazy { AudioJackReceiver() }


    fun start(context: Context) {
        context
            .registerReceiver(
                receiver,
                IntentFilter(
                    Intent.ACTION_HEADSET_PLUG
                )
            )
    }

    fun stop(context: Context) {
        context
            .unregisterReceiver(
                receiver
            )
    }


    /**
     * This wrapper function to check whether audio jack was connected or not.
     * @see AudioManager.isWiredHeadsetOn
     * @see AudioManager.getDevices
     */
    @Suppress("DEPRECATION")
    fun isAudioJackConnected(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (device in am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
                return device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
            }
            return false
        } else {
            return am.isWiredHeadsetOn
        }
    }


    inner class AudioJackReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            hasBluetoothHeadset = isAudioJackConnected()
        }
    }
}
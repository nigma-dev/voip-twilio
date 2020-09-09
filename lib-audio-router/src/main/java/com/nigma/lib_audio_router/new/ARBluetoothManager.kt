package com.nigma.lib_audio_router.new

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import timber.log.Timber

class ARBluetoothManager(
    private val adapter: BluetoothAdapter,
    val callback: () -> Unit
) {

    var hasBluetoothHeadset = false
        @Synchronized
        get
        @Synchronized
        private set(value) {
            callback()
            field = value
        }

    private val listener by lazy { BluetoothHeadsetListener() }
    private val receiver by lazy { BluetoothHeadsetReceiver() }


    fun start(context: Context) {
        register(context)
    }


    fun stop(context: Context) {
        unregister(context)
    }

    /**
     * This wrapper function to check whether bluetooth was connected or not.
     * @see AudioManager.isBluetoothScoOn
     * @see AudioManager.getDevices
     */
    @Suppress("DEPRECATION")
    fun isBluetoothConnected(am: AudioManager): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (device in am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
                return device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            }
            return false
        } else {
            return isBluetoothHeadsetConnected()
        }
    }


    private fun isBluetoothHeadsetConnected(): Boolean {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        return mBluetoothAdapter.isEnabled && mBluetoothAdapter
            .getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED
    }


    private fun register(context: Context) {
        context
            .registerReceiver(
                receiver,
                IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
            )

        adapter
            .getProfileProxy(
                context.applicationContext,
                listener,
                BluetoothProfile.HEADSET
            )
    }


    private fun unregister(context: Context) {
        context.unregisterReceiver(receiver)
    }


    inner class BluetoothHeadsetListener : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(p0: Int, proxy: BluetoothProfile?) {
            Timber.d("onServiceConnected : bluetooth profile $proxy")
            if (proxy == null) return
            hasBluetoothHeadset = proxy.connectedDevices.size > 0
        }

        override fun onServiceDisconnected(p0: Int) {
            Timber.d("onServiceDisconnected :  $p0")
            hasBluetoothHeadset = false
        }
    }


    inner class BluetoothHeadsetReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent == null) {
                Timber.v("onReceive intent was null")
                return
            }
            hasBluetoothHeadset = (intent.getIntExtra(
                BluetoothAdapter.EXTRA_CONNECTION_STATE,
                -100
            ) == BluetoothAdapter.STATE_CONNECTED)
        }
    }
}

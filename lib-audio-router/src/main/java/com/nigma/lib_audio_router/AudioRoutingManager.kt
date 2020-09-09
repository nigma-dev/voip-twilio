package com.nigma.lib_audio_router

import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import com.nigma.lib_audio_router.callback.AudioRoutingChangesListener
import com.nigma.lib_audio_router.model.AudioDevice
import timber.log.Timber

/**
 * @author enigma
 */
class AudioRoutingManager
private constructor(
    private val context: Context,
    private val callback: AudioRoutingChangesListener
) {

    private val availableDevice = mutableListOf<AudioDevice>()

    private val defaultDevice: AudioDevice
        get() {
            return getSystemCurrentRoutedDevice()
        }

    private val androidAudioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val bluetoothAdapter by lazy {
        (context
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .adapter
    }

    private val arBlueManager by lazy {
        ARBluetoothManager(bluetoothAdapter)
        { connected ->
            /**
             * method will be invoked when
             * bluetooth headset {connected, disconnected}
             */
            /**
             * method will be invoked when
             * bluetooth headset {connected, disconnected}
             */
            /**
             * method will be invoked when
             * bluetooth headset {connected, disconnected}
             */

            /**
             * method will be invoked when
             * bluetooth headset {connected, disconnected}
             */
            setDevice(
                if (connected)
                    AudioDevice.BLUETOOTH
                else
                    defaultDevice
            )
            Timber.d("onBluetoothDeviceStateChange connected plugged -> %s", connected)
        }
    }

    private val arAudioJackManager by lazy {
        ARWiredHeadsetManager(androidAudioManager)
        { plugged ->
            /**
             * method will be invoked when
             * audio jack {plugged, unplugged}
             */
            /**
             * method will be invoked when
             * audio jack {plugged, unplugged}
             */
            /**
             * method will be invoked when
             * audio jack {plugged, unplugged}
             */

            /**
             * method will be invoked when
             * audio jack {plugged, unplugged}
             */
            setDevice(
                if (plugged)
                    AudioDevice.AUDIO_JACK
                else
                    defaultDevice
            )
            Timber.d("onBluetoothDeviceStateChange plugged -> %s", plugged)
        }
    }


    fun start() {
        Timber.d("start")
        arBlueManager.start(context)
        arAudioJackManager.start(context)
        Timber.d("start : currentDevice %s",defaultDevice)
        updateAvailableDevices()
        setDevice(defaultDevice)
    }


    fun release() {
        arBlueManager.stop(context)
        arAudioJackManager.stop(context)
    }

    @Throws(IllegalArgumentException::class)
    fun selectDevice(device: AudioDevice) {
        updateAvailableDevices()
        if (!availableDevice.contains(device)) {
            throw IllegalArgumentException("selected audio devices not available, name : ${device.name}")
        }
        setDevice(device)
    }

    private fun setDevice(device: AudioDevice) {
        Timber.d("setDevice : selected device -> %s %s",device,defaultDevice)
        when (device) {
            AudioDevice.SPEAKER -> enableSpeakerState()
            AudioDevice.EARPIECE -> androidAudioManager.isSpeakerphoneOn = false
            AudioDevice.BLUETOOTH -> enableBluetoothState()
            AudioDevice.AUDIO_JACK -> enableWireHeadsetState()
        }
        callback.onAudioRoutedDeviceChanged(device, availableDevice)
    }

    private fun enableSpeakerState() {
        androidAudioManager.isSpeakerphoneOn = true
    }

    private fun enableBluetoothState() {
        androidAudioManager.isBluetoothScoOn = true
    }

    private fun enableWireHeadsetState() {
        androidAudioManager.isWiredHeadsetOn = true
    }

    private fun updateAvailableDevices() {
        availableDevice.clear()
        if (isAudioJackConnected()) {
            availableDevice.add(AudioDevice.AUDIO_JACK)
        }
        if (isBluetoothConnected()) {
            availableDevice.add(AudioDevice.BLUETOOTH)
        }
        if (isSpeaker()) {
            availableDevice.add(AudioDevice.SPEAKER)
        }
        availableDevice.add(AudioDevice.EARPIECE)
    }

    /**
     * method will return current system routed audio device
     * @return{AudioDevice}
     */
    private fun getSystemCurrentRoutedDevice(): AudioDevice {
        Timber.d("getSystemCurrentRoutedDevice")
        return when {
            isAudioJackConnected() -> AudioDevice.AUDIO_JACK
            isBluetoothConnected() -> AudioDevice.BLUETOOTH
            isSpeaker() -> AudioDevice.SPEAKER
            else -> AudioDevice.EARPIECE
        }
    }

    private fun isBluetoothConnected() = arBlueManager.isBluetoothConnected(androidAudioManager)

    private fun isAudioJackConnected() = arAudioJackManager.isAudioJackConnected()

    private fun isSpeaker(): Boolean = androidAudioManager.isSpeakerphoneOn


    companion object {
        private lateinit var instance: AudioRoutingManager
        fun getInstance(
            context: Context,
            callback: AudioRoutingChangesListener
        ): AudioRoutingManager {
            if (!Companion::instance.isInitialized) {
                instance = AudioRoutingManager(context, callback)
            }
            return instance
        }
    }

}
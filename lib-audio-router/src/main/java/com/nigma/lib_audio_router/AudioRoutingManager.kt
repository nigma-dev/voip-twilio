package com.nigma.lib_audio_router

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.nigma.lib_audio_router.callback.AudioRoutingChangesListener
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.lib_audio_router.new.ARBluetoothManager
import com.nigma.lib_audio_router.new.ARWiredHeadsetManager


/**
 * @author enigma
 */
class AudioRoutingManager(
    private val context: Context,
    private val am: AudioManager,
    private val callback: AudioRoutingChangesListener
) {

    private val availableDevice = mutableListOf<AudioDevice>()

    private val currentDevice = getSystemCurrentRoutedDevice()

    private val bluetoothAdapter by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private val arBlueManager by lazy {
        ARBluetoothManager(bluetoothAdapter) { onBluetoothStatusChange() }
    }

    private val arAudioJackManager by lazy {
        ARWiredHeadsetManager(am) { onAudioJackStatusChange() }
    }


    /**
     *
     */
    private fun onBluetoothStatusChange() {

    }


    /**
     *
     */
    private fun onAudioJackStatusChange() {

    }


    fun start() {
        arBlueManager.start(context)
        updateAvailableDevices()
    }


    fun setDevice(device: AudioDevice) {
        callback.onAudioRoutedDeviceChanged(device, availableDevice)
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
        return when {
            isAudioJackConnected() -> AudioDevice.AUDIO_JACK
            isBluetoothConnected() -> AudioDevice.BLUETOOTH
            isSpeaker() -> AudioDevice.SPEAKER
            else -> AudioDevice.EARPIECE
        }
    }

    private fun isBluetoothConnected() = arBlueManager.isBluetoothConnected(am)

    private fun isAudioJackConnected() = arAudioJackManager.isAudioJackConnected()

    private fun isSpeaker(): Boolean = am.isSpeakerphoneOn
}
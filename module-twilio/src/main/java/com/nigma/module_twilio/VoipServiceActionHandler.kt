package com.nigma.module_twilio

import android.content.Intent
import com.nigma.lib_audio_router.callback.AudioRoutingChangesListener
import com.nigma.lib_audio_router.model.AudioDevice

class VoipServiceActionHandler(
    private val binder: VoipServiceBinder
) : AudioRoutingChangesListener {

    private val mediaManager
        get() = binder.mediaManager

    private val audioManger
        get() = binder.audioManager

    private val mediaStateCallback
        get() = binder.voipLocalMediaStateCallback


    fun onOffMic() {
        mediaStateCallback?.onMicrophoneStateChanged(
            mediaManager.handleMicrophone()
        )
    }

    fun onOffCamera() {
        mediaStateCallback?.onCameraStateChanged(
            mediaManager.handleCameraOnOff()
        )
    }

    fun flipCamera() {
        mediaStateCallback?.onCameraStateFlipped(
            mediaManager.handleCameraFlip()
        )
    }

    fun routeAudio() {
        with(audioManger) {
            if (isAuxiliaryAudioDevice) {
                mediaStateCallback?.onAudioDevicesListAvailable(availableDevice)
            } else {
                switchDevice()
            }
        }
    }

    fun selectAudioDevice(intent: Intent) {
        val device = intent.getSerializableExtra("selected_device") as AudioDevice
        audioManger.selectDevice(device)
    }

    override fun onAudioRoutedDeviceChanged(
        selectedDevice: AudioDevice,
        availableDevice: List<AudioDevice>
    ) {
        mediaStateCallback?.onAudioRoutedDeviceChanged(
            selectedDevice,
            audioManger.isAuxiliaryAudioDevice
        )
//        toast(selectedDevice.deviceName)
    }
}
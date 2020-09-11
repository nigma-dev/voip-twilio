package com.nigma.module_twilio.callbacks

import com.nigma.lib_audio_router.model.AudioDevice
import com.twilio.video.CameraCapturer

interface VoipMediaActionStateCallback {

    fun onAudioRoutedDeviceChanged(selectedDevice: AudioDevice, availableHeadset: Boolean)

    fun onAudioDevicesListAvailable(devices: List<AudioDevice>)

    fun onMicrophoneStateChanged(enable: Boolean)

    fun onCameraStateChanged(enable: Boolean)

    fun onCameraStateFlipped(source: CameraCapturer.CameraSource)

    fun onConnectionStateChange(connectionState: String)
}
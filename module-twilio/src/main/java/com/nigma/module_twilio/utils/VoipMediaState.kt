package com.nigma.module_twilio.utils

data class VoipMediaState(
    var isSpeakerEnable: Boolean = true,
    var isMicEnable: Boolean = true,
    var isCameraEnable: Boolean = true,
    var cameraState: CameraState = CameraState.BACK_CAMERA
)

enum class CameraState {
    FRONT_CAMERA,
    BACK_CAMERA
}
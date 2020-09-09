package com.nigma.module_twilio.model

import com.nigma.lib_audio_router.AppRTCAudioManager

data class AudioDeviceModel(
    val name: String,
    val icon: Int,
    val id: AppRTCAudioManager.AudioDevice
)
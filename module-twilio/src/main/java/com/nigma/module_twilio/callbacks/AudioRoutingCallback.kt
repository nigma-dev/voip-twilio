package com.nigma.module_twilio.callbacks

import androidx.annotation.DrawableRes
import com.nigma.module_twilio.model.AudioDeviceModel

interface AudioRoutingCallback {

    /**
     *
     */
    fun onAudioOutputDeviceChange(@DrawableRes resource: Int, earpiece: Boolean)

    /**
     *
     */
    fun onAudioDevicesListAvailable(devices: List<AudioDeviceModel>)

    /**
     *
     */
    fun onAudioInputDeviceStateChange(mute: Boolean)

}
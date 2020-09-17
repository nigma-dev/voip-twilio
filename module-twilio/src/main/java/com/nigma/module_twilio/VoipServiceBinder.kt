package com.nigma.module_twilio

import android.os.Binder
import com.nigma.lib_audio_router.AudioRoutingManager
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.module_twilio.callbacks.VoipLocalMediaStateCallback
import com.nigma.module_twilio.callbacks.VoipParticipantStateContract
import com.nigma.module_twilio.twilio.TwilioLocalMediaManager
import com.nigma.module_twilio.ui.VoipFragment
import com.nigma.module_twilio.utils.User
import com.twilio.video.LocalVideoTrack
import timber.log.Timber

class VoipServiceBinder(
    private val service: VoipService
) : Binder() {

    val user: User?
        get() = service.user

    var voipLocalMediaStateCallback: VoipLocalMediaStateCallback? = null

    var participantCallback: VoipParticipantStateContract? = null
        set(value) {
            Timber.d("VoipParticipantStateContract")
            service.twilioManager.participantCallback = value
            field = value
        }

    val selectedDevice: AudioDevice?
        get() = audioManager.selectedDevice

    val auxiliaryAvailable: Boolean
        get() = audioManager.isAuxiliaryAudioDevice

    val localVideoTrack: LocalVideoTrack?
        get() = mediaManager.localVideoTrack

    val cameraState: Boolean
        get() = mediaManager.getVideoCameraState()

    val microphoneState: Boolean
        get() = mediaManager.getMicrophoneState()

    val mediaManager: TwilioLocalMediaManager
        get() = service.mediaManager

    val audioManager: AudioRoutingManager
        get() = service.audioRouteManager

    fun initCallback(
        callback: VoipFragment,
    ) {
        Timber.d("initCallback")
        this.participantCallback = callback
        this.voipLocalMediaStateCallback = callback
    }

    fun releaseCallback() {
        Timber.d("releaseCallback")
        this.participantCallback = null
        this.voipLocalMediaStateCallback = null
    }
}
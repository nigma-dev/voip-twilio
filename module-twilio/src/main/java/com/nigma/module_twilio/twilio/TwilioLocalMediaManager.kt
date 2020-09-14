package com.nigma.module_twilio.twilio

import com.twilio.video.CameraCapturer
import com.twilio.video.LocalVideoTrack
import timber.log.Timber
import tvi.webrtc.voiceengine.WebRtcAudioUtils

class TwilioLocalMediaManager(
    private val twilioUseCase: TwilioUseCase
) {

    val localParticipant
        get() = twilioUseCase.localParticipant

    val localVideoTrack: LocalVideoTrack?
        get() = twilioUseCase.localVideoTrack


    fun publishLocalMediaTrack() {
        twilioUseCase.publishLocalTrack()
    }

    fun releaseLocalMediaTrack() {
        twilioUseCase.releaseTracks()
    }

    fun handleMicrophone(): Boolean {
        return twilioUseCase.handleMicrophone(!twilioUseCase.localAudioTrack.isEnabled)
    }

    fun handleCameraOnOff(): Boolean {
        with(twilioUseCase) {
            this.handleVideoStream(!localVideoTrack.isEnabled)
            Timber.i("handleCameraOnOff ${localVideoTrack.isEnabled}")
            return localVideoTrack.isEnabled
        }
    }

    fun handleCameraFlip(): CameraCapturer.CameraSource {
        return twilioUseCase.switchCamera()
    }

    fun getMicrophoneState(): Boolean {
        return twilioUseCase.localAudioTrack.isEnabled
    }

    fun getVideoCameraState(): Boolean {
        return twilioUseCase.localVideoTrack.isEnabled
    }

    companion object {
        fun suppressNoiseAndEcho() {
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)
        }
    }
}
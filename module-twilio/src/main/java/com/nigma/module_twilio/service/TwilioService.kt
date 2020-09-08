package com.nigma.module_twilio.service

import com.nigma.module_twilio.usecase.TwilioUseCase
import com.twilio.video.Room
import timber.log.Timber
import kotlin.jvm.Throws

class TwilioService(
    val useCase: TwilioUseCase,
    private val listener: Room.Listener
) {

    @Throws(IllegalArgumentException::class)
    fun connectRoom(roomName: String?, accessToken: String?) {
        roomName ?: throw IllegalArgumentException("roomName was null")
        accessToken ?: throw IllegalArgumentException("accessToken was null")
        useCase.connectToRoom(roomName, accessToken, listener)
    }

    @Throws(IllegalArgumentException::class)
    fun disconnectRoom() {
        useCase.disconnectFromRoom()
    }


    fun publishLocalTrack(isVideoCall: Boolean) {
        useCase.publishLocalTrack(isVideoCall)
    }


    fun handleMicrophone() {
        Timber.i("handleMicrophone ${useCase.localAudioTrack.isEnabled}")
        useCase.localAudioTrack.enable(!useCase.localAudioTrack.isEnabled)
    }


    fun handleCameraFlip() {
        useCase.switchCamera()
    }


    fun handleAudioOutput() {

    }

    fun handleCameraOnOff() {
        if (useCase.localVideoTrack.isEnabled) {
            useCase.disableVideoLocalTrack()
        } else {
            useCase.enableVideoLocalTrack()
        }
    }
}
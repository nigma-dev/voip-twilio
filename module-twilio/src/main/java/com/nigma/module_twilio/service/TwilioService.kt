package com.nigma.module_twilio.service

import com.nigma.module_twilio.usecase.TwilioUseCase
import com.twilio.video.Room
import kotlin.jvm.Throws

class TwilioService(
    private val useCase: TwilioUseCase,
    private val listener: Room.Listener
) {

    @Throws(IllegalArgumentException::class)
    fun connectRoom(roomName: String?, accessToken: String?) {
        roomName ?: throw IllegalArgumentException("roomName was null")
        accessToken ?: throw IllegalArgumentException("accessToken was null")
        useCase.connectToRoom(roomName, accessToken, listener)
    }

    @Throws(IllegalArgumentException::class)
    fun disconnectRoom(roomName: String?) {
        roomName ?: throw IllegalArgumentException("roomName was null")
        useCase.disconnectFromRoom(roomName)
    }


    fun publishLocalTrack(isVideoCall : Boolean) {
        useCase.publishLocalTrack(isVideoCall)
    }


    fun handleMicrophone() {

    }


    fun handleCameraFlip() {

    }


    fun handleAudioOutput() {

    }

    fun handleCameraOnOff() {

    }
}
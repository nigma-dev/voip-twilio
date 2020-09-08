package com.nigma.module_twilio.usecase

import com.twilio.video.Room

interface ITwilioUseCase {

    fun connectToRoom(roomIdName: String, accessToken: String, roomListener: Room.Listener)

    fun disconnectFromRoom()

    fun enableMicrophone()

    fun disableMicrophone()

    fun enableVideoLocalTrack()

    fun disableVideoLocalTrack()

    fun switchCamera()

    fun publishLocalTrack(isVideoCall: Boolean)

    /**
     * need to release resource when voip
     * call finished
     */
    fun releaseRoomResource()


}
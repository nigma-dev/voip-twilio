package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManagerContract
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException

class RoomListener(
    private val contract: TwilioManagerContract
) : Room.Listener {

    override fun onConnected(room: Room) {
        contract.onRoomConnected(room)
    }

    override fun onConnectFailure(room: Room, twilioException: TwilioException) {
        contract.onRoomConnectStateChange(room, twilioException)
    }

    override fun onReconnecting(room: Room, twilioException: TwilioException) {
        contract.onRoomConnectStateChange(room, twilioException)
    }

    override fun onReconnected(room: Room) {
        contract.onRoomConnectStateChange(room)
    }

    override fun onDisconnected(room: Room, twilioException: TwilioException?) {
        contract.onRoomDisconnected(room, twilioException)
    }

    override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
        contract.onParticipantConnected(room, remoteParticipant)
    }

    override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
        contract.onParticipantDisconnected(room, remoteParticipant)
    }

    override fun onRecordingStarted(room: Room) {}

    override fun onRecordingStopped(room: Room) {}


}
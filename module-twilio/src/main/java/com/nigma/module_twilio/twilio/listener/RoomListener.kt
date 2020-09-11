package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManager
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException
import timber.log.Timber

class RoomListener(
    private val twilioManager: TwilioManager
) : Room.Listener {

    override fun onConnected(room: Room) {
        Timber.i("onConnected")
        twilioManager.publishLocalTrack(room)
        for (participant in room.remoteParticipants) {
            twilioManager.addParticipantAndSubscribe(participant)
        }
    }

    override fun onConnectFailure(room: Room, twilioException: TwilioException) {
        Timber.i("onConnectFailure")
    }

    override fun onReconnecting(room: Room, twilioException: TwilioException) {
        Timber.i("onReconnecting")
    }

    override fun onReconnected(room: Room) {
        Timber.i("onReconnected")
    }

    override fun onDisconnected(room: Room, twilioException: TwilioException?) {
        Timber.i("onDisconnected")
        twilioManager.onDisconnected()
    }

    override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
        Timber.i("onParticipantConnected")
        twilioManager.addParticipantAndSubscribe(remoteParticipant)
    }

    override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
        Timber.i("onParticipantDisconnected")
        twilioManager.removeParticipantAndSubscribe(remoteParticipant)
    }

    override fun onRecordingStarted(room: Room) {
        Timber.i("onRecordingStarted")
    }

    override fun onRecordingStopped(room: Room) {
        Timber.i("onRecordingStopped")
    }


}
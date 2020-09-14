package com.nigma.module_twilio

import com.twilio.video.*

interface VoipServiceContract {

    fun onRoomConnected(room: Room)

    fun onCommunicationCommand(message: String)

    fun onRoomConnectStateChange(
        room: Room,
        twilioException: TwilioException? = null
    )

    fun onRoomConnectFailure(
        room: Room,
        twilioException: TwilioException
    )

    fun onTrackSubscriptionFailed(
        participant: Participant,
        publication: TrackPublication,
        twilioException: TwilioException
    )

    fun onTrackPublicationFailed(
        participant: Participant,
        track: Track,
        twilioException: TwilioException
    )

    fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    )

    fun onParticipantDisconnected(
        room: Room,
        remoteParticipant: RemoteParticipant
    )
}
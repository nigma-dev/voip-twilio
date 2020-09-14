package com.nigma.module_twilio.twilio

import com.twilio.video.*

interface TwilioManagerContract {

    /**
     * method will be invoked when connecting to room was a failure
     */
    fun onRoomConnectFailure(
        room: Room,
        twilioException: TwilioException
    )

    fun onRoomConnected(room: Room)

    fun onRoomDisconnected(
        room: Room,
        twilioException: TwilioException?
    )

    fun onParticipantConnected(
        room: Room,
        remoteParticipant: RemoteParticipant
    )

    fun onParticipantDisconnected(
        room: Room,
        remoteParticipant: RemoteParticipant
    )

    fun onMessage(
        remoteDataTrack: RemoteDataTrack,
        message: String
    )

    /**
     * method will be invoked when track subscription
     * was a failure including video , audio , and data tracks and
     * for both local and remote
     */
    fun onTrackSubscriptionFailed(
        participant: Participant,
        trackPublication: TrackPublication,
        twilioException: TwilioException
    )

    fun onTrackPublicationFailed(
        participant: Participant,
        track: Track,
        twilioException: TwilioException
    )


    /*region published*//*
    fun onTrackPublished(
        participant: Participant,
        publication: TrackPublication
    )

    fun onTrackUnpublished(
        participant: Participant,
        publication: TrackPublication
    )
    *//*endregion*//*
*/
    /*region subscribe and unsubscribe */
    /**
     *
     */
    fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrack: RemoteDataTrack
    )

    /*fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrack: RemoteAudioTrack
    )
*/
    fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrack: RemoteVideoTrack
    )

    /*fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrack: RemoteAudioTrack
    )

    fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrack: RemoteVideoTrack
    )*/

    /*endregion*/

    fun onVideoTrackStateChange(
        remoteParticipant: RemoteParticipant,
        enable: Boolean
    )

    fun onAudioTrackStateChange(
        remoteParticipant: RemoteParticipant,
        enable: Boolean
    )

    fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    )

    fun onRoomConnectStateChange(
        room: Room,
        twilioException: TwilioException? = null
    )

}
package com.nigma.module_twilio.twilio

import com.twilio.video.*
import timber.log.Timber

interface TwilioManagerContract {

    /**
     * method will be invoked when connecting to room was a failure
     */
    fun onRoomConnectFailure(room: Room, twilioException: TwilioException)


    /**
     * method will be invoked when track subscription was a failure
     * including video , audio , and data tracks and
     * for both local and remote
     */
    fun onTrackSubscriptionFailed(
        participant: RemoteParticipant,
        trackPublication: TrackPublication,
        twilioException: TwilioException
    )

    fun onDataTrackPublished(
        participant: Participant,
        dataTrackPublication: DataTrackPublication
    )

    fun onAudioTrackPublished(
        participant: Participant,
        dataTrackPublication: DataTrackPublication
    )

    fun onVideoTrackPublished(){

    }

}
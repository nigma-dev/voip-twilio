package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManagerContract
import com.twilio.video.*

class LocalParticipantListener(
    private val contract: TwilioManagerContract
) : LocalParticipant.Listener {

    override fun onAudioTrackPublished(
        localParticipant: LocalParticipant,
        localAudioTrackPublication: LocalAudioTrackPublication
    ) {
//        contract.onTrackPublished(localParticipant, localAudioTrackPublication)
    }

    override fun onAudioTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localAudioTrack: LocalAudioTrack,
        twilioException: TwilioException
    ) {
        contract.onTrackPublicationFailed(localParticipant, localAudioTrack, twilioException)
    }

    override fun onVideoTrackPublished(
        localParticipant: LocalParticipant,
        localVideoTrackPublication: LocalVideoTrackPublication
    ) {
//        contract.onTrackPublished(localParticipant, localVideoTrackPublication)
    }

    override fun onVideoTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localVideoTrack: LocalVideoTrack,
        twilioException: TwilioException
    ) {
        contract.onTrackPublicationFailed(localParticipant, localVideoTrack, twilioException)
    }

    override fun onDataTrackPublished(
        localParticipant: LocalParticipant,
        localDataTrackPublication: LocalDataTrackPublication
    ) {
//        contract.onTrackPublished(localParticipant, localDataTrackPublication)
    }

    override fun onDataTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localDataTrack: LocalDataTrack,
        twilioException: TwilioException
    ) {
        contract.onTrackPublicationFailed(localParticipant, localDataTrack, twilioException)
    }
}
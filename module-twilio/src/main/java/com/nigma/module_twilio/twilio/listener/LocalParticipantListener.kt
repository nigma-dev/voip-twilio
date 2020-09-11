package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManager
import com.twilio.video.*
import timber.log.Timber

class LocalParticipantListener(
    private val twilioManager: TwilioManager
): LocalParticipant.Listener{

    override fun onAudioTrackPublished(
        localParticipant: LocalParticipant,
        localAudioTrackPublication: LocalAudioTrackPublication
    ) {
        Timber.i("onAudioTrackPublished")
    }

    override fun onAudioTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localAudioTrack: LocalAudioTrack,
        twilioException: TwilioException
    ) {
        Timber.i("onAudioTrackPublicationFailed")
    }

    override fun onVideoTrackPublished(
        localParticipant: LocalParticipant,
        localVideoTrackPublication: LocalVideoTrackPublication
    ) {
        Timber.i("onVideoTrackPublished")
    }

    override fun onVideoTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localVideoTrack: LocalVideoTrack,
        twilioException: TwilioException
    ) {
        Timber.i("onVideoTrackPublicationFailed")
    }

    override fun onDataTrackPublished(
        localParticipant: LocalParticipant,
        localDataTrackPublication: LocalDataTrackPublication
    ) {
        Timber.i("onDataTrackPublished")
    }

    override fun onDataTrackPublicationFailed(
        localParticipant: LocalParticipant,
        localDataTrack: LocalDataTrack,
        twilioException: TwilioException
    ) {
        Timber.i("onDataTrackPublicationFailed")
    }
}
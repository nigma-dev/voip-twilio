package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManagerContract
import com.twilio.video.*
import timber.log.Timber

class RemoteParticipantListener(
    private val contract: TwilioManagerContract
) : RemoteParticipant.Listener {

    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
//        contract.onTrackPublished(remoteParticipant, remoteAudioTrackPublication)
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
//        contract.onTrackUnpublished(remoteParticipant, remoteAudioTrackPublication)
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
//        contract.onAudioTrackSubscribed(remoteParticipant, remoteAudioTrack)
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        contract.onTrackSubscriptionFailed(
            remoteParticipant,
            remoteAudioTrackPublication,
            twilioException
        )
    }

    override fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
//        contract.onAudioTrackUnsubscribed(remoteParticipant, remoteAudioTrack)
    }

    override fun onAudioTrackPublishPriorityChanged(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        trackPriority: TrackPriority
    ) {
        Timber.i("onAudioTrackPublishPriorityChanged")
    }

    override fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
//        contract.onTrackPublished(remoteParticipant, remoteVideoTrackPublication)
        Timber.i("onVideoTrackPublished")
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
//        contract.onTrackUnpublished(remoteParticipant, remoteVideoTrackPublication)
        Timber.i("onVideoTrackUnpublished")
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        contract.onVideoTrackSubscribed(remoteParticipant, remoteVideoTrack)
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        contract.onTrackSubscriptionFailed(
            remoteParticipant,
            remoteVideoTrackPublication,
            twilioException
        )
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
//        contract.onVideoTrackUnsubscribed(remoteParticipant, remoteVideoTrack)
    }

    override fun onVideoTrackPublishPriorityChanged(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        trackPriority: TrackPriority
    ) {
        Timber.i("onVideoTrackPublishPriorityChanged")
    }

    override fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
//        contract.onTrackPublished(remoteParticipant,remoteDataTrackPublication)
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
//        contract.onTrackUnpublished(remoteParticipant,remoteDataTrackPublication)
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        contract.onDataTrackSubscribed(remoteParticipant, remoteDataTrack)
    }

    override fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onDataTrackSubscriptionFailed")
    }

    override fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i("onDataTrackUnsubscribed")
    }

    override fun onDataTrackPublishPriorityChanged(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        trackPriority: TrackPriority
    ) {
        Timber.i("onDataTrackPublishPriorityChanged")
    }

    override fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        contract.onAudioTrackStateChange(
            remoteParticipant,
            remoteAudioTrackPublication.isTrackEnabled
        )
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        contract.onAudioTrackStateChange(
            remoteParticipant,
            remoteAudioTrackPublication.isTrackEnabled
        )
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        contract.onVideoTrackStateChange(
            remoteParticipant,
            remoteVideoTrackPublication.isTrackEnabled
        )
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        contract.onVideoTrackStateChange(
            remoteParticipant,
            remoteVideoTrackPublication.isTrackEnabled
        )
    }

    override fun onVideoTrackSwitchedOn(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackSwitchedOn")
    }

    override fun onVideoTrackSwitchedOff(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackSwitchedOff")
    }

    override fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    ) {
        contract.onNetworkQualityLevelChanged(
            remoteParticipant,
            networkQualityLevel
        )
    }
}
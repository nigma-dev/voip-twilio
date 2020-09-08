package com.nigma.module_twilio.seg_interface

import com.twilio.video.*
import timber.log.Timber


interface RemoteParticipantEvent : RemoteParticipant.Listener {

    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackPublished")
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackUnpublished")
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        Timber.i("onAudioTrackSubscribed")
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onAudioTrackSubscriptionFailed")
        onAudioTrackFailedError(
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
        Timber.i("onAudioTrackUnsubscribed")
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
        Timber.i("onVideoTrackPublished")
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackUnpublished")
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackSubscribed")
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onVideoTrackSubscriptionFailed")
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackUnsubscribed")
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
        Timber.i("onDataTrackPublished")
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Timber.i("onDataTrackUnpublished")
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i("onDataTrackSubscribed")
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
        Timber.i("onAudioTrackEnabled")
        onRemoteAudioStateChange(remoteParticipant, remoteAudioTrackPublication)
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackDisabled")
        onRemoteAudioStateChange(remoteParticipant, remoteAudioTrackPublication)
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackEnabled")
        onRemoteVideoStateChange(remoteParticipant, remoteVideoTrackPublication)
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackDisabled")
        onRemoteVideoStateChange(remoteParticipant, remoteVideoTrackPublication)
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
        Timber.i("onNetworkQualityLevelChanged")
    }


    fun onRemoteAudioStateChange(
        participant: RemoteParticipant,
        publication: RemoteAudioTrackPublication
    )

    fun onRemoteVideoStateChange(
        participant: RemoteParticipant,
        publication: RemoteVideoTrackPublication
    )

    fun onAudioTrackFailedError(
        remoteParticipant: RemoteParticipant,
        remotePublication: TrackPublication,
        twilioException: TwilioException
    )
}
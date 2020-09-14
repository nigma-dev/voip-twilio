package com.nigma.module_twilio.base

import android.widget.Toast
import com.nigma.module_twilio.callbacks.VoipParticipantStateContract
import com.twilio.video.*

abstract class BaseVoipFragment : BaseVoipActionHookFragment(), VoipParticipantStateContract {

    override fun onParticipantVideoTrackAvailable(
        participant: RemoteParticipant,
        track: RemoteVideoTrack
    ) {

    }

    override fun onParticipantNetworkStateChange(
        participant: Participant,
        level: NetworkQualityLevel
    ) {
        Toast.makeText(context, "onNetworkStateChange $level", Toast.LENGTH_SHORT).show()
    }

    override fun onParticipantNewConnected(remoteParticipant: RemoteParticipant) {}

    override fun onParticipantAudioTrackStateChange(
        participant: RemoteParticipant,
        enable: Boolean
    ) {
    }

    override fun onParticipantVideoTrackStateChange(
        participant: RemoteParticipant,
        enable: Boolean
    ) {
    }


    override fun onCameraStateChanged(enable: Boolean) {}

    override fun onCameraStateFlipped(source: CameraCapturer.CameraSource) {}

    override fun onLocalVideoTrackAvailable(track: LocalVideoTrack) {}

    override fun onConnectionStateChange(connectionState: String) {
        statusTv?.text = connectionState
    }
}
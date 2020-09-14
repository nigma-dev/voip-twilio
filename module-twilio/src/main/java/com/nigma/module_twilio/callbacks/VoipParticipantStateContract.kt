package com.nigma.module_twilio.callbacks

import com.twilio.video.*

interface VoipParticipantStateContract {
    /**
     * method is used to when remote participant's audio state change
     * such as 1.{audio on state}, 2.{audio off state}
     */
    fun onParticipantAudioTrackStateChange(participant: RemoteParticipant, enable: Boolean)


    /**
     * method is used to when remote participant's video state change
     * such as 1.{video cam on state}, 2.{video cam off state}
     */
    fun onParticipantVideoTrackStateChange(participant: RemoteParticipant, enable: Boolean)


    /**
     * method will invoke when remote participant's video track
     * is available
     */
    fun onParticipantVideoTrackAvailable(participant: RemoteParticipant, track: RemoteVideoTrack)

    /**
     * method is used to when participant's network state change
     * @see NetworkQualityLevel
     */
    fun onParticipantNetworkStateChange(participant: Participant, level: NetworkQualityLevel)


    fun onParticipantNewConnected(remoteParticipant: RemoteParticipant)
}
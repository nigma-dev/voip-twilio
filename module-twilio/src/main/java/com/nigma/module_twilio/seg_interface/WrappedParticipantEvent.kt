package com.nigma.module_twilio.seg_interface

import com.twilio.video.NetworkQualityLevel
import com.twilio.video.Participant
import com.twilio.video.RemoteVideoTrack

interface WrappedParticipantEvent {
    /**
     * method is used to when remote participant's audio state change
     * such as 1.{audio on state}, 2.{audio off state}
     */
    fun onAudioTrackStateChange(enable: Boolean)


    /**
     * method is used to when remote participant's video state change
     * such as 1.{video cam on state}, 2.{video cam off state}
     */
    fun onVideoTrackStateChange(enable: Boolean)


    /**
     * method will invoke when remote participant's video track
     * is available
     */
    fun onVideoTrackAvailable(videoTrack: RemoteVideoTrack)

    /**
     * method is used to when participant's network state change
     * @see NetworkQualityLevel
     */
    fun onNetworkStateChange(participant: Participant, level: NetworkQualityLevel)

}
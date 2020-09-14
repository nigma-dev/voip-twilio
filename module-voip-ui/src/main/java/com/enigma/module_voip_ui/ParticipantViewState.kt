package com.enigma.module_voip_ui

import com.twilio.video.NetworkQualityLevel
import com.twilio.video.NetworkQualityLevel.*
import com.twilio.video.Participant

data class ParticipantViewState(
    val sid: String,
    val name: String?,
    val imageUrl: String?,
    val networkState: NetworkState,
    val micState: MicState,
    val videoState: VideoState
)

fun buildParticipantViewState(participant: Participant): ParticipantViewState {
    return ParticipantViewState(
        participant.sid,
        participant.identity,
        participant.identity,
        mapNetworkState(participant.networkQualityLevel),
        mapMicState(participant),
        mapVideoState(participant)
    )
}

fun mapVideoState(participant: Participant): VideoState {
    val enable = participant
        .videoTracks
        .firstOrNull()
        ?.videoTrack
        ?.isEnabled
        ?: false
    return if (enable) VideoState.VIDEO else VideoState.PAUSE_VIDEO
}

fun mapNetworkState(level: NetworkQualityLevel): NetworkState {
    return when (level) {
        NETWORK_QUALITY_LEVEL_UNKNOWN -> NetworkState.LEVEL_0
        NETWORK_QUALITY_LEVEL_ZERO -> NetworkState.LEVEL_1
        NETWORK_QUALITY_LEVEL_ONE -> NetworkState.LEVEL_2
        NETWORK_QUALITY_LEVEL_TWO -> NetworkState.LEVEL_3
        NETWORK_QUALITY_LEVEL_THREE -> NetworkState.LEVEL_4
        NETWORK_QUALITY_LEVEL_FOUR -> NetworkState.LEVEL_5
        NETWORK_QUALITY_LEVEL_FIVE -> NetworkState.LEVEL_6
    }
}

fun mapMicState(participant: Participant): MicState {
    val enable = participant
        .audioTracks
        .firstOrNull()
        ?.isTrackEnabled
        ?: false
    return if (enable) MicState.ON else MicState.OFF
}
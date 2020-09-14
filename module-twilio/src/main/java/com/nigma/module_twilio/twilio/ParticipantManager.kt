package com.nigma.module_twilio.twilio

import com.nigma.module_twilio.twilio.listener.DataTrackListener
import com.nigma.module_twilio.twilio.listener.RemoteParticipantListener
import com.twilio.video.RemoteDataTrack
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import java.util.*

class ParticipantManager(
    private val contract: TwilioManagerContract
) {

    val participants = mutableListOf<RemoteParticipant>()

    private val dataTrackMap: HashMap<RemoteParticipant, RemoteDataTrack> = hashMapOf()

    fun addAllParticipantAndSubscribe(room: Room) {
        for (participant in room.remoteParticipants) {
            addParticipantAndSubscribe(participant)
        }
    }

    fun addParticipantAndSubscribe(remoteParticipant: RemoteParticipant) {
        remoteParticipant.setListener(RemoteParticipantListener(contract))

        for (publication in remoteParticipant.remoteDataTracks) {
            val track = publication.remoteDataTrack
            if (publication.isTrackSubscribed && track != null) {
                addRemoteDataTrack(remoteParticipant, track)
            }
        }
        participants.add(remoteParticipant)
    }

    fun removeParticipantAndSubscribe(remoteParticipant: RemoteParticipant) {
        for (publication in remoteParticipant.remoteDataTracks) {
            val track = publication.remoteDataTrack
            if (publication.isTrackSubscribed && track != null) {
                removeRemoteDataTrack(remoteParticipant, track)
            }
        }
        participants.remove(remoteParticipant)
    }


    fun addRemoteDataTrack(participant: RemoteParticipant, dataTrack: RemoteDataTrack) {
        dataTrackMap[participant] = dataTrack
        dataTrack.setListener(DataTrackListener(contract))
    }


    private fun removeRemoteDataTrack(participant: RemoteParticipant, dataTrack: RemoteDataTrack) {
        dataTrackMap.remove(participant)
        dataTrack.setListener(null)
    }
}
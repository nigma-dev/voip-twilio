package com.nigma.module_twilio.twilio

import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.callbacks.WrappedParticipantEvent
import com.nigma.module_twilio.twilio.listener.DataTrackListener
import com.nigma.module_twilio.twilio.listener.LocalParticipantListener
import com.nigma.module_twilio.twilio.listener.RemoteParticipantListener
import com.nigma.module_twilio.twilio.listener.RoomListener
import com.twilio.video.*
import timber.log.Timber
import tvi.webrtc.voiceengine.WebRtcAudioUtils
import java.util.*
import kotlin.collections.HashMap


class TwilioManager(
    private val twilioUseCase: TwilioUseCase,
    private val voipService: VoipService
) {

    var participantCallback: WrappedParticipantEvent? = null

    var isVideoCommunication = false

    val localVideoTrack: LocalVideoTrack?
        get() {
            return twilioUseCase.localVideoTrack
        }

    private val roomListener = RoomListener(this@TwilioManager)

    private val dataTrackMap: HashMap<RemoteParticipant, RemoteDataTrack> = hashMapOf()


    @Throws(Exception::class)
    fun connectRoom(accessToken: String?) {
        accessToken ?: throw Exception("token is require to connect to the room")
        Timber.i("connectRoom")
        with(twilioUseCase) {
            val connectOptionsBuilder = ConnectOptions
                .Builder(accessToken)
                .audioTracks(Collections.singletonList(localAudioTrack))
                .dataTracks(Collections.singletonList(localDataTrack))
                .enableAutomaticSubscription(true)
                .enableNetworkQuality(true)

            if (isVideoCommunication) connectOptionsBuilder
                .videoTracks(
                    Collections.singletonList(
                        twilioUseCase
                            .localVideoTrack
                    )
                )
            connectToRoom(
                connectOptionsBuilder.build(),
                roomListener
            )
        }
    }

    fun disconnectRoom() {
        Timber.i("disconnectRoom")
        twilioUseCase.disconnectFromRoom()
    }

    fun handleMicrophone(): Boolean {
        with(twilioUseCase) {
            this.handleMicrophone(!localAudioTrack.isEnabled)
            Timber.i("handleMicrophone ${localAudioTrack.isEnabled}")
            return localAudioTrack.isEnabled
        }
    }

    fun setDataTrack() {
        twilioUseCase.localDataTrack.send("hey i am ")
    }

    fun handleCameraOnOff(): Boolean {
        with(twilioUseCase) {
            this.handleVideoStream(!localVideoTrack.isEnabled)
            timber.log.Timber.i("handleCameraOnOff ${localVideoTrack.isEnabled}")
            return localVideoTrack.isEnabled
        }
    }

    fun handleCameraFlip(): CameraCapturer.CameraSource {
        return twilioUseCase.switchCamera()
    }

    fun getMicrophoneState(): Boolean {
        return twilioUseCase.localAudioTrack.isEnabled
    }

    fun getVideoCameraState(): Boolean {
        return twilioUseCase.localVideoTrack.isEnabled
    }

    fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        addRemoteDataTrack(remoteParticipant, remoteDataTrack)
    }


    fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        participantCallback?.onVideoTrackAvailable(remoteVideoTrack)
    }


    fun onRemoteAudioStateChange(
        participant: RemoteParticipant,
        publication: RemoteAudioTrackPublication
    ) {
        participantCallback
            ?.onAudioTrackStateChange(publication.isTrackEnabled)
    }

    fun onRemoteVideoStateChange(
        participant: RemoteParticipant,
        publication: RemoteVideoTrackPublication
    ) {
        participantCallback
            ?.onVideoTrackStateChange(publication.isTrackEnabled)
    }


    fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    ) {
        participantCallback
            ?.onNetworkStateChange(remoteParticipant, networkQualityLevel)
    }

    fun publishLocalTrack(room: Room) {
        room.localParticipant?.setListener(LocalParticipantListener(this))
        twilioUseCase.publishLocalTrack(isVideoCommunication)
    }


    fun addParticipantAndSubscribe(remoteParticipant: RemoteParticipant) {
        remoteParticipant.setListener(RemoteParticipantListener(this))
        subscribeDataTrack(remoteParticipant)
    }

    fun removeParticipantAndSubscribe(remoteParticipant: RemoteParticipant) {
        unsubscribeDataTrack(remoteParticipant)
    }


    fun suppressNoiseAndEcho() {
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)
    }


    private fun subscribeDataTrack(participant: RemoteParticipant) {
        for (publication in participant.remoteDataTracks) {
            val track = publication.remoteDataTrack
            if (publication.isTrackSubscribed && track != null) {
                addRemoteDataTrack(participant, track)
            }
        }
    }

    private fun addRemoteDataTrack(participant: RemoteParticipant, dataTrack: RemoteDataTrack) {
        dataTrackMap[participant] = dataTrack
        dataTrack.setListener(DataTrackListener(this))
    }


    private fun unsubscribeDataTrack(participant: RemoteParticipant) {
        for (publication in participant.remoteDataTracks) {
            val track = publication.remoteDataTrack
            if (publication.isTrackSubscribed && track != null) {
                removeRemoteDataTrack(participant, track)
            }
        }
    }

    private fun removeRemoteDataTrack(participant: RemoteParticipant, dataTrack: RemoteDataTrack) {
        dataTrackMap.remove(participant)
        dataTrack.setListener(null)
    }

    fun onDisconnected() {
        voipService.stopSelf()
    }


}
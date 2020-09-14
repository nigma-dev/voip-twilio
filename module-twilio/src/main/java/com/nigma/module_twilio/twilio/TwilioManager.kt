package com.nigma.module_twilio.twilio

import android.content.Context
import com.nigma.module_twilio.VoipServiceContract
import com.nigma.module_twilio.callbacks.VoipParticipantStateContract
import com.nigma.module_twilio.twilio.listener.LocalParticipantListener
import com.nigma.module_twilio.twilio.listener.RoomListener
import com.twilio.video.*


class TwilioManager(
    private val contract: VoipServiceContract,
    capturer: CameraCapturer
) : TwilioManagerContract {

    var participantCallback: VoipParticipantStateContract? = null

    val localMediaManager by lazy {
        TwilioLocalMediaManager(twilioUseCase)
    }

    private val twilioUseCase = TwilioUseCase(capturer)

    private val roomListener = RoomListener(this@TwilioManager)

    private val participantManager = ParticipantManager(this)

    @Throws(Exception::class)
    fun connectRoom(
        context: Context,
        accessToken: String?,
        audioTrack: LocalAudioTrack,
        dataTrack: LocalDataTrack,
        videoTrack: LocalVideoTrack? = null
    ) {
        accessToken ?: throw Exception("token is require to connect to the room")
        TwilioLocalMediaManager.suppressNoiseAndEcho()
        twilioUseCase.connectToRoom(
            context,
            accessToken,
            audioTrack,
            dataTrack,
            roomListener,
            videoTrack
        )
    }

    fun disconnectRoom() {
        twilioUseCase.disconnectFromRoom()
    }

    override fun onRoomConnectFailure(room: Room, twilioException: TwilioException) {
        contract.onRoomConnectFailure(room, twilioException)
    }

    @Throws(Exception::class)
    override fun onRoomConnected(room: Room) {
        val participant = room.localParticipant
            ?: throw Exception("LocalParticipant was null on room connected")
        twilioUseCase.localParticipant = participant
        participant.setListener(LocalParticipantListener(this))
        localMediaManager.publishLocalMediaTrack()
        participantManager.addAllParticipantAndSubscribe(room)
        contract.onRoomConnected(room)
    }

    override fun onRoomDisconnected(room: Room, twilioException: TwilioException?) {
        contract.onRoomConnectStateChange(room, twilioException)
    }

    override fun onRoomConnectStateChange(room: Room, twilioException: TwilioException?) {
        contract.onRoomConnectStateChange(room, twilioException)
    }

    override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
        participantManager.addParticipantAndSubscribe(remoteParticipant)
        participantCallback?.onParticipantNewConnected(remoteParticipant)
    }

    override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
        participantManager.removeParticipantAndSubscribe(remoteParticipant)
        contract.onParticipantDisconnected(room, remoteParticipant)
    }

    override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
        contract.onCommunicationCommand(message)
    }

    override fun onTrackSubscriptionFailed(
        participant: Participant,
        trackPublication: TrackPublication,
        twilioException: TwilioException
    ) {
        contract.onTrackSubscriptionFailed(
            participant,
            trackPublication,
            twilioException
        )
    }

    override fun onTrackPublicationFailed(
        participant: Participant,
        track: Track,
        twilioException: TwilioException
    ) {
        contract.onTrackPublicationFailed(
            participant,
            track,
            twilioException
        )
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrack: RemoteDataTrack
    ) {
        participantManager.addRemoteDataTrack(remoteParticipant, remoteDataTrack)
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        participantCallback?.onParticipantVideoTrackAvailable(remoteParticipant, remoteVideoTrack)
    }

    override fun onVideoTrackStateChange(remoteParticipant: RemoteParticipant, enable: Boolean) {
        participantCallback?.onParticipantVideoTrackStateChange(remoteParticipant, enable)
    }

    override fun onAudioTrackStateChange(remoteParticipant: RemoteParticipant, enable: Boolean) {
        participantCallback?.onParticipantAudioTrackStateChange(remoteParticipant, enable)
    }

    override fun onNetworkQualityLevelChanged(
        remoteParticipant: RemoteParticipant,
        networkQualityLevel: NetworkQualityLevel
    ) {
        contract.onNetworkQualityLevelChanged(remoteParticipant, networkQualityLevel)
    }
}
package com.nigma.module_twilio.usecase

import android.content.Context
import com.nigma.module_twilio.utils.CameraState
import com.nigma.module_twilio.utils.VoipMediaState
import com.twilio.video.*
import com.twilio.video.CameraCapturer
import com.twilio.video.CameraCapturer.CameraSource
import java.util.*


class TwilioUseCase(
    private val context: Context,
    private val voipMediaState: VoipMediaState,
) : ITwilioUseCase {

    private lateinit var room: Room
    private lateinit var localAudioTrack: LocalAudioTrack
    private lateinit var localVideoTrack: LocalVideoTrack
    private lateinit var localDataTrack: LocalDataTrack

    init {
        createTracks()
    }

    override fun connectToRoom(
        roomIdName: String,
        accessToken: String,
        roomListener: Room.Listener
    ) {
        val connectOptions = ConnectOptions.Builder(accessToken)
            .roomName(roomIdName)
            .audioTracks(Collections.singletonList(localAudioTrack))
            .videoTracks(Collections.singletonList(localVideoTrack))
            .dataTracks(Collections.singletonList(localDataTrack))
            .build()
        room = Video.connect(context.applicationContext, connectOptions, roomListener)
    }

    override fun disconnectFromRoom(roomIdName: String) {
        room.disconnect()
    }

    override fun enableMicrophone() {
        localAudioTrack.enable(true)
    }

    override fun disableMicrophone() {
        localAudioTrack.enable(false)
    }

    override fun enableVideoLocalTrack() {
        localVideoTrack.enable(true)
    }

    override fun disableVideoLocalTrack() {
        localVideoTrack.enable(false)
    }

    override fun turnFrontCamera() {

    }

    override fun turnBackCamera() {

    }

    override fun publishLocalTrack(isVideoCall: Boolean) {
        room.localParticipant
            ?.let { participant ->
                with(participant) {
                    if (isVideoCall) {
                        publishTrack(localVideoTrack)
                    }
                    publishTrack(localAudioTrack)
                    publishTrack(localDataTrack)
                }
            }
    }

    override fun releaseRoomResource() {
        localAudioTrack.release()
        localVideoTrack.release()
        localDataTrack.release()
    }


    private fun createTracks() {
        createDataLocalTrack()
        createVideoLocalTrack()
        createAudioLocalTrack()
    }


    @Throws(Exception::class)
    private fun createAudioLocalTrack() {
        localAudioTrack = LocalAudioTrack
            .create(
                context,
                voipMediaState.isMicEnable
            )
            ?: throw Exception("can't create local audio track , please check permission")
    }

    @Throws(Exception::class)
    private fun createVideoLocalTrack() {
        val cameraSource = if (voipMediaState.cameraState == CameraState.BACK_CAMERA) {
            CameraSource.FRONT_CAMERA
        } else {
            CameraSource.FRONT_CAMERA
        }
        val cameraCapturer = CameraCapturer(context, cameraSource)
        localVideoTrack = LocalVideoTrack
            .create(
                context,
                voipMediaState.isCameraEnable,
                cameraCapturer
            ) ?: throw Exception("can't create local video track, please check permission")
    }

    @Throws(Exception::class)
    private fun createDataLocalTrack() {
        localDataTrack = LocalDataTrack.create(context.applicationContext)
            ?: throw Exception("can't create local data track")

    }
}
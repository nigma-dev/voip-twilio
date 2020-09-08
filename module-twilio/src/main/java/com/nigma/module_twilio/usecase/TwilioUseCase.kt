package com.nigma.module_twilio.usecase

import android.content.Context
import android.media.AudioManager
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

    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    lateinit var localAudioTrack: LocalAudioTrack
    lateinit var localVideoTrack: LocalVideoTrack

    private lateinit var room: Room
    private lateinit var localDataTrack: LocalDataTrack
    private lateinit var capturer: CameraCapturer

    init {
        createTracks()
    }

    override fun connectToRoom(
        roomIdName: String,
        accessToken: String,
        roomListener: Room.Listener
    ) {
        val connectOptions = ConnectOptions
            .Builder(accessToken)
            .roomName(roomIdName)
            .audioTracks(Collections.singletonList(localAudioTrack))
            .videoTracks(Collections.singletonList(localVideoTrack))
            .dataTracks(Collections.singletonList(localDataTrack))
            .build()
        room = Video.connect(context.applicationContext, connectOptions, roomListener)

    }

    override fun disconnectFromRoom() {
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

    override fun switchCamera() {
        capturer.switchCamera()
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
    fun createAudioLocalTrack(): LocalAudioTrack {
        localAudioTrack = LocalAudioTrack
            .create(
                context,
                voipMediaState.isMicEnable
            )
            ?: throw Exception("can't create local audio track , please check permission")
        return localAudioTrack
    }

    @Throws(Exception::class)
    fun createVideoLocalTrack(): LocalVideoTrack {
        if (!::localVideoTrack.isInitialized) {
            val cameraSource = if (voipMediaState.cameraState == CameraState.BACK_CAMERA) {
                CameraSource.FRONT_CAMERA
            } else {
                CameraSource.FRONT_CAMERA
            }
            capturer = CameraCapturer(context, cameraSource)
            localVideoTrack = LocalVideoTrack
                .create(
                    context,
                    voipMediaState.isCameraEnable,
                    capturer
                ) ?: throw Exception("can't create local video track, please check permission")
        }
        return localVideoTrack
    }

    @Throws(Exception::class)
    private fun createDataLocalTrack() {
        localDataTrack = LocalDataTrack.create(context.applicationContext)
            ?: throw Exception("can't create local data track")

    }
}
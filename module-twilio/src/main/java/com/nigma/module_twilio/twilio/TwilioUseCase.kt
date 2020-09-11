package com.nigma.module_twilio.twilio

import android.content.Context
import com.twilio.video.*
import com.twilio.video.CameraCapturer.CameraSource
import timber.log.Timber


class TwilioUseCase(private val context: Context) {

    private lateinit var room: Room
    private lateinit var capturer: CameraCapturer

    val localAudioTrack: LocalAudioTrack by lazy { createAudioLocalTrack() }

    val localDataTrack: LocalDataTrack by lazy { createDataLocalTrack() }

    val localVideoTrack: LocalVideoTrack by lazy { createVideoLocalTrack() }


    private val localParticipant: LocalParticipant?
        get() {
            return room.localParticipant
        }


    fun connectToRoom(connectOptions: ConnectOptions, roomListener: Room.Listener) {
        Timber.i("connectToRoom")
        room = Video.connect(context.applicationContext, connectOptions, roomListener)
    }

    fun disconnectFromRoom() {
        Timber.i("disconnectFromRoom")
        releaseTracks()
        room.disconnect()
    }

    fun handleMicrophone(enable: Boolean) {
        Timber.i("handleMicrophone $enable ")
        localAudioTrack.enable(enable)
    }

    fun handleVideoStream(enable: Boolean) {
        Timber.i("handleVideoStream $enable ")
        localVideoTrack.enable(enable)
    }

    fun switchCamera(): CameraSource {
        Timber.i("handleVideoStream ${capturer.cameraSource.name}")
        capturer.switchCamera()
        return capturer.cameraSource
    }


    fun publishLocalTrack(isVideoCall: Boolean) {
        Timber.i("publishLocalTrack $isVideoCall")
        Timber.i("publishLocalTrack audio ${localParticipant?.publishTrack(localAudioTrack)}")
        Timber.i("publishLocalTrack data ${localParticipant?.publishTrack(localDataTrack)}")
        if (isVideoCall)
            Timber.i("publishLocalTrack video ${localParticipant?.publishTrack(localVideoTrack)}")
    }

    private fun releaseTracks() {
        Timber.i("releaseTracks")
        with(localParticipant) {
            this?.let {
                val vTrack = it.localVideoTracks
                if (vTrack.isNotEmpty()) {
                    Timber.i("releaseTracks video release ${vTrack.size}")
                    for (track in vTrack) {
                        track.localVideoTrack.release()
                    }
                }

                val aTrack = it.localAudioTracks
                if (aTrack.isNotEmpty()) {
                    Timber.i("releaseTracks audio release ${vTrack.size}")
                    for (track in aTrack) {
                        track.localAudioTrack.release()
                    }
                }

                val dTrack = it.localDataTracks
                if (dTrack.isNotEmpty()) {
                    Timber.i("releaseTracks data release ${vTrack.size}")
                    for (track in dTrack) {
                        track.localDataTrack.release()
                    }
                }
            }
        }
    }


    @Throws(Exception::class)
    private fun createAudioLocalTrack(): LocalAudioTrack {
        return LocalAudioTrack
            .create(
                context,
                true
            )
            ?: throw Exception("failed to create audio track , please check permission")
    }

    @Throws(Exception::class)
    private fun createVideoLocalTrack(): LocalVideoTrack {
        capturer = CameraCapturer(context, CameraSource.FRONT_CAMERA)
        return LocalVideoTrack
            .create(
                context,
                true,
                capturer
            )
            ?: throw Exception("failed to create video track , please check permission")
    }

    @Throws(Exception::class)
    private fun createDataLocalTrack(): LocalDataTrack {
        Timber.i("createDataLocalTrack")
        return LocalDataTrack
            .create(context.applicationContext)
            ?: throw Exception("failed to create data track , please check permission")
    }
}
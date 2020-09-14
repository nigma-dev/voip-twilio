package com.nigma.module_twilio.twilio

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.twilio.video.*
import com.twilio.video.CameraCapturer.CameraSource
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*


class TwilioUseCase(private val captureSource: CameraCapturer) {

    private lateinit var room: Room


    var localParticipant: LocalParticipant? = null

    lateinit var localAudioTrack: LocalAudioTrack
    lateinit var localDataTrack: LocalDataTrack
    lateinit var localVideoTrack: LocalVideoTrack

    @Throws(Exception::class)
    fun connectToRoom(
        context: Context,
        accessToken: String,
        audioTrack: LocalAudioTrack,
        dataTrack: LocalDataTrack,
        roomListener: Room.Listener,
        videoTrack: LocalVideoTrack? = null
    ) {
        val connectOptionsBuilder = ConnectOptions
            .Builder(accessToken)
            .audioTracks(Collections.singletonList(audioTrack))
            .dataTracks(Collections.singletonList(dataTrack))
            .enableAutomaticSubscription(true)
            .enableNetworkQuality(true)

        videoTrack?.let {
            connectOptionsBuilder
                .videoTracks(
                    Collections.singletonList(it)
                )
        }
        localAudioTrack = audioTrack
        localDataTrack = dataTrack
        if (videoTrack != null) {
            localVideoTrack = videoTrack
        }
        room = Video.connect(
            context.applicationContext,
            connectOptionsBuilder.build(),
            roomListener
        )
    }

    fun disconnectFromRoom() {
        Timber.i("disconnectFromRoom")
        if (room.remoteParticipants.size == 1){
            localDataTrack.send("call ended by bla")
        }
        releaseTracks()
        room.disconnect()
    }

    fun handleMicrophone(enable: Boolean): Boolean {
        Timber.i("handleMicrophone $enable ")
        localAudioTrack.enable(enable)
        return localAudioTrack.isEnabled
    }

    fun handleVideoStream(enable: Boolean): Boolean {
        Timber.i("handleVideoStream $enable ")
        if (!::localVideoTrack.isInitialized) return false
        localVideoTrack.enable(enable)
        return localVideoTrack.isEnabled
    }

    fun switchCamera(): CameraSource {
        Timber.i("handleVideoStream ${captureSource.cameraSource.name}")
        captureSource.switchCamera()
        return captureSource.cameraSource
    }


    fun publishLocalTrack() {
        Timber.i("publishLocalTrack}")
        localParticipant?.let {
            with(it) {
                publishTrack(localDataTrack)
                publishTrack(localAudioTrack)

                if (::localVideoTrack.isInitialized) {
                    publishTrack(localVideoTrack)
                }
            }
        }

    }

    fun releaseTracks() {
        Timber.i("releaseTracks")
        localParticipant?.let { participant ->
            with(participant) {
                val vTrack = localVideoTracks
                if (vTrack.isNotEmpty()) {
                    Timber.i("releaseTracks video release ${vTrack.size}")
                    for (track in vTrack) {
                        track.localVideoTrack.release()
                    }
                }

                val aTrack = localAudioTracks
                if (aTrack.isNotEmpty()) {
                    Timber.i("releaseTracks audio release ${vTrack.size}")
                    for (track in aTrack) {
                        track.localAudioTrack.release()
                    }
                }

                val dTrack = localDataTracks
                if (dTrack.isNotEmpty()) {
                    Timber.i("releaseTracks data release ${vTrack.size}")
                    for (track in dTrack) {
                        track.localDataTrack.release()
                    }
                }
            }
        }
    }

}


private val audioOptions: AudioOptions
    get() = AudioOptions.Builder()
        .audioJitterBufferFastAccelerate(true)
        .autoGainControl(true)
        .echoCancellation(true)
        .noiseSuppression(true)
        .stereoSwapping(true)
        .highpassFilter(true)
        .build()

private val dataTrackOptions: DataTrackOptions
    get() = DataTrackOptions.Builder()
        .ordered(true)
        .build()

@RequiresPermission(Manifest.permission.RECORD_AUDIO)
fun createAudioTrack(
    reference: WeakReference<Context>,
    enable: Boolean = true,
    options: AudioOptions = audioOptions
): LocalAudioTrack? {
    val context = reference.get()?.applicationContext ?: return null
    return LocalAudioTrack.create(context, enable, options)
}

@RequiresPermission(Manifest.permission.CAMERA)
fun createVideoTrack(
    reference: WeakReference<Context>,
    capturer: CameraCapturer,
    enable: Boolean = true
): LocalVideoTrack? {
    val context = reference.get()?.applicationContext ?: return null
    return LocalVideoTrack.create(context, enable, capturer)
}


fun createDataTrack(
    reference: WeakReference<Context>,
    options: DataTrackOptions = dataTrackOptions
): LocalDataTrack? {
    val context = reference.get()?.applicationContext ?: return null
    return LocalDataTrack.create(context, options)
}
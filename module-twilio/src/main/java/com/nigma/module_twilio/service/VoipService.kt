package com.nigma.module_twilio.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.nigma.module_twilio.seg_interface.RemoteParticipantEvent
import com.nigma.module_twilio.seg_interface.RoomEvent
import com.nigma.module_twilio.seg_interface.WrappedParticipantEvent
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.usecase.TwilioUseCase
import com.nigma.module_twilio.utils.VoipMediaState
import com.twilio.video.*
import timber.log.Timber

class VoipService : Service(), RoomEvent, RemoteParticipantEvent {

    private val voipMediaState by lazy { VoipMediaState() }

    private val mediaState = MutableLiveData<VoipMediaState>()

    private var isVideoCall = false

    private val serviceBinder = ServiceBinder()

    private val twilioService by lazy {
        TwilioService(
            TwilioUseCase(
                applicationContext,
                voipMediaState
            ),
            this
        )
    }

    override fun onBind(intent: Intent): IBinder? {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("onCreate")
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        super.onDestroy()
    }

    @Throws(Exception::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            intent ?: return super.onStartCommand(intent, flags, startId)
            with(twilioService) {
                when (intent.action) {
                    ACTION_VOIP_CONNECT_ROOM -> {
                        try {
                            with(Intent(applicationContext, VoipActivity::class.java)) {
                                putExtras(intent)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                        with(intent) {
                            isVideoCall = getBooleanExtra(KEY_IS_VOIP_VIDEO_TYPE, false)
                            connectRoom(
                                getStringExtra(KEY_ROOM_NAME),
                                getStringExtra(KEY_ACCESS_TOKEN)
                            )
                        }
                    }
                    ACTION_VOIP_DISCONNECT_ROOM -> {
                        disconnectRoom()
                        stopSelf()
                    }

                    ACTION_VOIP_HANDLE_MIC -> handleMicrophone()

                    ACTION_VOIP_HANDLE_AUDIO_OUTPUT -> handleAudioOutput()

                    ACTION_VOIP_HANDLE_CAMERA -> handleCameraOnOff()

                    ACTION_VOIP_HANDLE_CAMERA_FLIP -> handleCameraFlip()
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /*region Voip Room Related Callback*/
    override fun onConnected(room: Room) {
        Timber.i("onConnected ${room.sid}")
        twilioService.publishLocalTrack(true)
        if (room.remoteParticipants.isNotEmpty()) {
            room.remoteParticipants[0].setListener(this)
        }
    }

    override fun onConnectFailure(room: Room, twilioException: TwilioException) {
        Timber.i(twilioException, "onConnectFailure ${room.sid} ")
    }

    override fun onReconnecting(room: Room, twilioException: TwilioException) {
        Timber.i(twilioException, "onReconnecting ${room.sid} ")
    }

    override fun onReconnected(room: Room) {
        Timber.i("onReconnected ${room.sid} ")
    }

    override fun onDisconnected(room: Room, twilioException: TwilioException?) {
        Timber.i(twilioException, "onDisconnected ${room.sid} ")
    }

    override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
        remoteParticipant.setListener(this)
        Timber.i("onParticipantConnected ${room.sid} | remoteParticipant -> ${remoteParticipant.identity}  ")
    }

    override fun onParticipantDisconnected(
        room: Room,
        remoteParticipant: RemoteParticipant
    ) {
        Timber.i("onParticipantDisconnected ${room.sid} | remoteParticipant -> ${remoteParticipant.identity}  ")
        stopSelf()
    }
    /*endregion*/

    /*region Voip Media State Related Callback*/

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        serviceBinder
            .listener
            ?.onVideoTrackAvailable(remoteVideoTrack)
    }

    override fun onRemoteAudioStateChange(
        participant: RemoteParticipant,
        publication: RemoteAudioTrackPublication
    ) {
        Timber.i("onRemoteAudioStateChange : user-> ${participant.identity} | enable -> ${publication.isTrackEnabled}")
        serviceBinder.listener?.onAudioTrackStateChange(publication.isTrackEnabled)
    }

    override fun onRemoteVideoStateChange(
        participant: RemoteParticipant,
        publication: RemoteVideoTrackPublication
    ) {
        Timber.i("onRemoteVideoStateChange : user-> ${participant.identity} | enable -> ${publication.isTrackEnabled}")
        serviceBinder.listener?.onAudioTrackStateChange(publication.isTrackEnabled)
    }

    override fun onAudioTrackFailedError(
        remoteParticipant: RemoteParticipant,
        remotePublication: TrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onAudioTrackFailedError ${remoteParticipant.identity}")
    }

    /*endregion*/

    inner class ServiceBinder : Binder() {

        private val service by lazy { this@VoipService }

        val isVideoCall by lazy { service.isVideoCall }

        val mediaStateLiveData by lazy { service.mediaState }

        var listener: WrappedParticipantEvent? = null
            set(value) {
                Timber.i("set Listener")
                field = value
            }

        val localVideoTrack: LocalVideoTrack
            get() {
                return service
                    .twilioService
                    .useCase
                    .createVideoLocalTrack()
            }

        fun clickHungUp() = twilioService.disconnectRoom()

        fun clickMicrophone() = twilioService.handleMicrophone()

        fun clickAudioOutput() = twilioService.handleAudioOutput()

        fun clickCamera() = twilioService.handleCameraOnOff()

        fun clickCameraFlip() = twilioService.handleCameraFlip()
    }


    companion object {
        const val ACTION_VOIP_CONNECT_ROOM = "action:VOIP-ROOM-CONNECT"
        const val ACTION_VOIP_DISCONNECT_ROOM = "action:VOIP-ROOM-DISCONNECT"

        const val ACTION_VOIP_HANDLE_MIC = "action:ACTION_VOIP_HANDLE_MIC"
        const val ACTION_VOIP_HANDLE_AUDIO_OUTPUT = "action:ACTION_VOIP_HANDLE_AUDIO_OUTPUT"
        const val ACTION_VOIP_HANDLE_CAMERA = "action:ACTION_VOIP_HANDLE_CAMERA"
        const val ACTION_VOIP_HANDLE_CAMERA_FLIP = "action:ACTION_VOIP_MEDIA_HANDLE_CAMERA_FLIP"

        const val EVENT_VOIP_HANGUP = "event:_VOIP_HANGUP"

        const val KEY_ROOM_NAME = "key:_ROOM_NAME"
        const val KEY_ACCESS_TOKEN = "key:_ACCESS_TOKEN"
        const val KEY_IS_VOIP_VIDEO_TYPE = "key:_VOIP_TYPE"
        const val KEY_USER_OBJECT = "key:_USER_OBJECT"
    }
}

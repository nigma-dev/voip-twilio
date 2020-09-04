package com.nigma.module_twilio.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.nigma.module_twilio.segregated_interface.RoomEvent
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.usecase.TwilioUseCase
import com.nigma.module_twilio.utils.VoipMediaState
import com.twilio.video.*
import timber.log.Timber

class VoipService : Service(), RoomEvent, RemoteParticipant.Listener {


    private val voipMediaState by lazy { VoipMediaState() }

    private val mediaState = MutableLiveData<VoipMediaState>()

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
        try {
            with(Intent(applicationContext, VoipActivity::class.java)) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
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
                        with(intent) {
                            connectRoom(
                                getStringExtra(KEY_ROOM_NAME),
                                getStringExtra(KEY_ACCESS_TOKEN)
                            )
                        }
                    }
                    ACTION_VOIP_DISCONNECT_ROOM -> {
                        disconnectRoom(intent.getStringExtra(KEY_ROOM_NAME))
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

    override fun onConnected(room: Room) {
        Timber.i("onConnected ${room.sid}")
        twilioService.publishLocalTrack(true)
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
    }

    inner class ServiceBinder : Binder() {
        fun getService() = this@VoipService

        fun getMediaStateLiveData() = this@VoipService.mediaState
    }


    companion object {
        const val ACTION_VOIP_CONNECT_ROOM = "action:VOIP-ROOM-CONNECT"
        const val ACTION_VOIP_DISCONNECT_ROOM = "action:VOIP-ROOM-DISCONNECT"

        const val ACTION_VOIP_HANDLE_MIC = "action:ACTION_VOIP_HANDLE_MIC"
        const val ACTION_VOIP_HANDLE_AUDIO_OUTPUT = "action:ACTION_VOIP_HANDLE_AUDIO_OUTPUT"
        const val ACTION_VOIP_HANDLE_CAMERA = "action:ACTION_VOIP_HANDLE_CAMERA"
        const val ACTION_VOIP_HANDLE_CAMERA_FLIP = "action:ACTION_VOIP_MEDIA_HANDLE_CAMERA_FLIP"


        const val KEY_ROOM_NAME = "key:_ROOM_NAME"
        const val KEY_ACCESS_TOKEN = "key:_ACCESS_TOKEN"
    }

    override fun onAudioTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackPublished ${remoteParticipant.identity}")
    }

    override fun onAudioTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackUnpublished ${remoteParticipant.identity}")
    }

    override fun onAudioTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        Timber.i("onAudioTrackSubscribed ${remoteParticipant.identity}")
    }

    override fun onAudioTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onAudioTrackSubscriptionFailed ${remoteParticipant.identity}")
    }

    override fun onAudioTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication,
        remoteAudioTrack: RemoteAudioTrack
    ) {
        Timber.i("onAudioTrackUnsubscribed ${remoteParticipant.identity}")
    }

    override fun onVideoTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackPublished ${remoteParticipant.identity}")
    }

    override fun onVideoTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackUnpublished ${remoteParticipant.identity}")
    }

    override fun onVideoTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackSubscribed ${remoteParticipant.identity}")
    }

    override fun onVideoTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onVideoTrackSubscriptionFailed ${remoteParticipant.identity}")
    }

    override fun onVideoTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication,
        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i("onVideoTrackUnsubscribed ${remoteParticipant.identity}")
    }

    override fun onDataTrackPublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Timber.i("onDataTrackPublished ${remoteParticipant.identity}")
    }

    override fun onDataTrackUnpublished(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Timber.i("onDataTrackUnpublished ${remoteParticipant.identity}")
    }

    override fun onDataTrackSubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i("onDataTrackSubscribed ${remoteParticipant.identity}")
    }

    override fun onDataTrackSubscriptionFailed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onDataTrackSubscriptionFailed ${remoteParticipant.identity}")
    }

    override fun onDataTrackUnsubscribed(
        remoteParticipant: RemoteParticipant,
        remoteDataTrackPublication: RemoteDataTrackPublication,
        remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i("onDataTrackUnsubscribed ${remoteParticipant.identity}")
    }

    override fun onAudioTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackEnabled ${remoteParticipant.identity}")
    }

    override fun onAudioTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i("onAudioTrackDisabled ${remoteParticipant.identity}")
    }

    override fun onVideoTrackEnabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackEnabled ${remoteParticipant.identity}")
    }

    override fun onVideoTrackDisabled(
        remoteParticipant: RemoteParticipant,
        remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i("onVideoTrackDisabled ${remoteParticipant.identity}")
    }
}

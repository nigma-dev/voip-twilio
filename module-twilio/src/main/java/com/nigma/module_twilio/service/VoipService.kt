package com.nigma.module_twilio.service

import android.app.Service
import android.content.Intent
import android.media.AudioRouting
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.nigma.lib_audio_router.AppRTCAudioManager
import com.nigma.lib_audio_router.AppRTCAudioManager.AudioDevice
import com.nigma.lib_audio_router.AudioRoutingManager
import com.nigma.module_twilio.R
import com.nigma.module_twilio.model.AudioDeviceModel
import com.nigma.module_twilio.callbacks.AudioRoutingCallback
import com.nigma.module_twilio.callbacks.RemoteParticipantEvent
import com.nigma.module_twilio.callbacks.RoomEvent
import com.nigma.module_twilio.callbacks.WrappedParticipantEvent
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.usecase.TwilioUseCase
import com.nigma.module_twilio.utils.VoipMediaState
import com.twilio.video.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class VoipService : Service(), RoomEvent, RemoteParticipantEvent,
    AppRTCAudioManager.AudioManagerEvents, AppRTCAudioManager.OnWiredHeadsetStateListener {

    private val voipMediaState by lazy { VoipMediaState() }

    private val audioRouter by lazy { AppRTCAudioManager.create(applicationContext) }

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
        with(audioRouter) {
            /*setDefaultAudioDevice(
                AudioRoutingManager().getSystemCurrentRoutedAudioDevice(
                    audioRouter.androidAudioManager
                )
            )*/
            setManageBluetoothByDefault(true)
            setManageHeadsetByDefault(true)
            setManageSpeakerPhoneByProximity(false)
            setOnWiredHeadsetStateListener(this@VoipService)
            start(this@VoipService)
        }
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        audioRouter.stop()
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

                    ACTION_VOIP_HANDLE_AUDIO_OUTPUT -> handleAudioRouting()

                    ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE -> {
                        val device = intent.getSerializableExtra("audio-device") as AudioDevice
                        audioRouter.selectAudioDevice(device)
                    }

                    ACTION_VOIP_HANDLE_CAMERA -> handleCameraOnOff()

                    ACTION_VOIP_HANDLE_CAMERA_FLIP -> handleCameraFlip()
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleAudioRouting() {
        val devices = audioRouter.getAudioDevices()
        val selectedDevice = audioRouter.getSelectedAudioDevice() ?: return
        if (devices.size <= 2) {
            val device = if (selectedDevice.name == AudioDevice.SPEAKER_PHONE.name) {
                AudioDevice.EARPIECE
            } else {
                AudioDevice.SPEAKER_PHONE
            }
            audioRouter.selectAudioDevice(device)
        } else {
            mapAudioDevices(devices)
        }
    }

    private fun mapAudioDevices(availableDevices: Set<AudioDevice?>) {
        val devices = mutableListOf<AudioDeviceModel>()
        for (device in availableDevices) {
            device ?: continue
            val model = when (device) {
                AudioDevice.BLUETOOTH -> {
                    AudioDeviceModel(
                        "bluetooth",
                        R.drawable.ic_bluetooth,
                        AudioDevice.BLUETOOTH
                    )
                }
                AudioDevice.EARPIECE -> {
                    AudioDeviceModel(
                        "earpiece",
                        R.drawable.ic_baseline_phone_24,
                        AudioDevice.EARPIECE
                    )
                }
                AudioDevice.WIRED_HEADSET -> {
                    AudioDeviceModel(
                        "headset",
                        R.drawable.ic_headset,
                        AudioDevice.WIRED_HEADSET
                    )
                }
                else -> {
                    AudioDeviceModel(
                        "speaker",
                        R.drawable.ic_speaker,
                        AudioDevice.SPEAKER_PHONE
                    )
                }
            }
            devices.add(model)
        }
        serviceBinder
            .audioRoutingCallback
            ?.onAudioDevicesListAvailable(devices)
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
            .participantCallback
            ?.onVideoTrackAvailable(remoteVideoTrack)
    }

    override fun onRemoteAudioStateChange(
        participant: RemoteParticipant,
        publication: RemoteAudioTrackPublication
    ) {
        Timber.i("onRemoteAudioStateChange : user-> ${participant.identity} | enable -> ${publication.isTrackEnabled}")
        serviceBinder.participantCallback?.onAudioTrackStateChange(publication.isTrackEnabled)
    }

    override fun onRemoteVideoStateChange(
        participant: RemoteParticipant,
        publication: RemoteVideoTrackPublication
    ) {
        Timber.i("onRemoteVideoStateChange : user-> ${participant.identity} | enable -> ${publication.isTrackEnabled}")
        serviceBinder.participantCallback?.onAudioTrackStateChange(publication.isTrackEnabled)
    }

    override fun onAudioTrackFailedError(
        remoteParticipant: RemoteParticipant,
        remotePublication: TrackPublication,
        twilioException: TwilioException
    ) {
        Timber.i("onAudioTrackFailedError ${remoteParticipant.identity}")
    }

    /*endregion*/

    override fun onAudioDeviceChanged(
        var1: AudioDevice,
        var2: Set<AudioDevice?>
    ) {
        Timber.i("onAudioDeviceChanged ${var1.name}")
        mapAudioDeviceToUi(var1, var2)
    }

    private fun mapAudioDeviceToUi(
        currentDevice: AudioDevice,
        deviceList: Set<AudioDevice?>
    ) {
        val icon = if (deviceList.size > 2) {
            when (currentDevice) {
                AudioDevice.BLUETOOTH -> {
                    R.drawable.ic_bluetooth_arrow
                }
                AudioDevice.WIRED_HEADSET -> {
                    R.drawable.ic_headset_arrow
                }
                else -> {
                    R.drawable.ic_speaker_arrow
                }
            }
        } else {
            R.drawable.ic_speaker
        }
        serviceBinder
            .audioRoutingCallback
            ?.onAudioOutputDeviceChange(icon, currentDevice == AudioDevice.EARPIECE)

        Timber.i("mapAudioDeviceToUi ${icon}")
    }

    inner class ServiceBinder : Binder() {

        private val service by lazy { this@VoipService }

        val isVideoCall by lazy { service.isVideoCall }

        var participantCallback: WrappedParticipantEvent? = null
            set(value) {
                Timber.i("set Listener")
                field = value
            }

        var audioRoutingCallback: AudioRoutingCallback? = null

        val localVideoTrack: LocalVideoTrack
            get() {
                return service
                    .twilioService
                    .useCase
                    .createVideoLocalTrack()
            }

        fun initCallback(
            participantEvent: WrappedParticipantEvent,
            audioRoutingCallback: AudioRoutingCallback
        ) {
            this.participantCallback = participantEvent
            this.audioRoutingCallback = audioRoutingCallback
        }
    }


    companion object {
        const val ACTION_VOIP_CONNECT_ROOM = "action:VOIP-ROOM-CONNECT"
        const val ACTION_VOIP_DISCONNECT_ROOM = "action:VOIP-ROOM-DISCONNECT"

        const val ACTION_VOIP_HANDLE_MIC = "action:ACTION_VOIP_HANDLE_MIC"
        const val ACTION_VOIP_HANDLE_AUDIO_OUTPUT = "action:ACTION_VOIP_HANDLE_AUDIO_OUTPUT"
        const val ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE = "ACTION_CALL_MEDIA_CHANGE_AUDIO_DEVICE"
        const val ACTION_VOIP_HANDLE_CAMERA = "action:ACTION_VOIP_HANDLE_CAMERA"
        const val ACTION_VOIP_HANDLE_CAMERA_FLIP = "action:ACTION_VOIP_MEDIA_HANDLE_CAMERA_FLIP"

        const val EVENT_VOIP_HANGUP = "event:_VOIP_HANGUP"

        const val KEY_ROOM_NAME = "key:_ROOM_NAME"
        const val KEY_ACCESS_TOKEN = "key:_ACCESS_TOKEN"
        const val KEY_IS_VOIP_VIDEO_TYPE = "key:_VOIP_TYPE"
        const val KEY_USER_OBJECT = "key:_USER_OBJECT"
    }

    override fun onWiredHeadsetStateChanged(var1: Boolean, var2: Boolean) {
        Timber.i("onWiredHeadsetStateChanged $var1 | $var2")
    }
}

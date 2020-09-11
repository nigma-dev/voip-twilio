package com.nigma.module_twilio

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.nigma.lib_audio_router.AudioRoutingManager
import com.nigma.lib_audio_router.callback.AudioRoutingChangesListener
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.lib_audio_router.model.AudioDevice.EARPIECE
import com.nigma.lib_audio_router.model.AudioDevice.SPEAKER
import com.nigma.module_twilio.callbacks.VoipMediaActionStateCallback
import com.nigma.module_twilio.callbacks.WrappedParticipantEvent
import com.nigma.module_twilio.twilio.TwilioManager
import com.nigma.module_twilio.twilio.TwilioUseCase
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.utils.toast
import com.twilio.video.LocalVideoTrack
import timber.log.Timber

class VoipService : Service(), AudioRoutingChangesListener {


    private val audioRouteManager by lazy {
        AudioRoutingManager(applicationContext, this)
    }
    private val twilioManager by lazy {
        TwilioManager(TwilioUseCase(applicationContext), this)
    }

    private val serviceBinder = ServiceBinder()

    private var isVideoCall = false
        set(value) {
            twilioManager.isVideoCommunication = value
            field = value
        }

    private var participantCallback: WrappedParticipantEvent? = null
        set(value) {
            twilioManager.participantCallback = value
            field = value
        }

    private var voipMediaActionStateCallback: VoipMediaActionStateCallback? = null

    override fun onBind(intent: Intent): IBinder? {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("onCreate")
        audioRouteManager.start()
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        audioRouteManager.release()
        super.onDestroy()
    }

    @Throws(Exception::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("onStartCommand action -> ${intent?.action}")
        try {
            intent ?: return super.onStartCommand(intent, flags, startId)
            with(twilioManager) {
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
                            audioRouteManager.userDefaultDevice =
                                (if (isVideoCall) SPEAKER else EARPIECE)
                            connectRoom(
//                                getStringExtra(KEY_ROOM_NAME),
                                getStringExtra(KEY_ACCESS_TOKEN),
                            )
                        }
                    }

                    ACTION_VOIP_DISCONNECT_ROOM -> disconnectRoom()

                    ACTION_VOIP_HANDLE_AUDIO_OUTPUT -> handleAudioRouting()

                    ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE -> {
                        val device = intent.getSerializableExtra("selected_device") as AudioDevice
                        audioRouteManager.selectDevice(device)
                    }

                    ACTION_VOIP_HANDLE_MIC -> {
                        val result = handleMicrophone()
                        voipMediaActionStateCallback?.onMicrophoneStateChanged(result)
                    }

                    ACTION_VOIP_HANDLE_CAMERA -> {
                        val result = handleCameraOnOff()
                        voipMediaActionStateCallback?.onCameraStateChanged(result)
                    }

                    ACTION_VOIP_HANDLE_CAMERA_FLIP -> {
                        val result = handleCameraFlip()
                        voipMediaActionStateCallback?.onCameraStateFlipped(result)
                    }
                    else -> {
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun handleAudioRouting() {
        twilioManager.setDataTrack()
        with(audioRouteManager) {
            if (isAuxiliaryAudioDevice) {
                voipMediaActionStateCallback?.onAudioDevicesListAvailable(availableDevice)
            } else {
                switchDevice()
            }
        }
    }

    override fun onAudioRoutedDeviceChanged(
        selectedDevice: AudioDevice,
        availableDevice: List<AudioDevice>
    ) {

        voipMediaActionStateCallback
            ?.onAudioRoutedDeviceChanged(
                selectedDevice,
                audioRouteManager.isAuxiliaryAudioDevice
            )
        Timber.i(
            "onAudioRoutedDeviceChanged audio callback -> ${this.voipMediaActionStateCallback.hashCode()}"
        )
        toast(selectedDevice.deviceName)
    }

    inner class ServiceBinder : Binder() {

        private val service: VoipService
            get() {
                return this@VoipService
            }

        val isVideoCall: Boolean
            get() {
                return service.isVideoCall
            }

        val selectedDevice: AudioDevice?
            get() {
                return audioRouteManager.selectedDevice
            }

        val auxiliaryAvailable: Boolean
            get() {
                return audioRouteManager.isAuxiliaryAudioDevice
            }

        val localVideoTrack: LocalVideoTrack?
            get() {
                return service
                    .twilioManager
                    .localVideoTrack
            }

        val cameraState: Boolean
            get() {
                return service
                    .twilioManager
                    .getVideoCameraState()
            }

        val microphoneState: Boolean
            get() {
                return service
                    .twilioManager
                    .getMicrophoneState()
            }

        fun initCallback(
            participantEvent: WrappedParticipantEvent,
            voipMediaActionStateCallback: VoipMediaActionStateCallback
        ) {
            service.participantCallback = participantEvent
            service.voipMediaActionStateCallback = voipMediaActionStateCallback
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

        const val KEY_ROOM_NAME = "key:_ROOM_NAME"
        const val KEY_ACCESS_TOKEN = "key:_ACCESS_TOKEN"
        const val KEY_IS_VOIP_VIDEO_TYPE = "key:_VOIP_TYPE"
        const val KEY_USER_OBJECT = "key:_USER_OBJECT"
    }
}

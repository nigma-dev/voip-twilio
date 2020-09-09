package com.nigma.lib_audio_router

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build.VERSION
import com.nigma.lib_audio_router.AppRTCBluetoothManager.State
import timber.log.Timber
import java.util.*


class AppRTCAudioManager private constructor(context: Context) {
    private var wiredHeadsetStateListener: OnWiredHeadsetStateListener? = null
    private var bluetoothAudioDeviceStateListener: BluetoothAudioDeviceStateListener? = null
    private var manageHeadsetByDefault = true
    private var manageBluetoothByDefault = true
    private var manageSpeakerPhoneByProximity = false
    private val apprtcContext: Context
     val androidAudioManager: AudioManager
    private var audioManagerEvents: AudioManagerEvents? = null
    private var amState: AudioManagerState
    private var savedAudioMode = AudioManager.MODE_IN_CALL
    private var savedIsSpeakerPhoneOn = false
    private var savedIsMicrophoneMute = false
    private var hasWiredHeadset = false
    private var defaultAudioDevice: AudioDevice
    private var selectedAudioDevice: AudioDevice? = null
    private var userSelectedAudioDevice: AudioDevice? = null

    //    private var proximitySensor: AppRTCProximitySensor?
    private val bluetoothManager: AppRTCBluetoothManager
    private var audioDevices: MutableSet<AudioDevice?>
    private val wiredHeadsetReceiver: BroadcastReceiver
    private var audioFocusChangeListener: OnAudioFocusChangeListener? = null


    fun start(audioManagerEvents: AudioManagerEvents?) {
        Timber.d("start")
        checkIsOnMainThread()
        if (amState == AudioManagerState.RUNNING) {
            Timber.e("AudioManager is already active")
        } else {
            Timber.d("AudioManager starts...")
            this.audioManagerEvents = audioManagerEvents
            amState = AudioManagerState.RUNNING
            savedAudioMode = androidAudioManager.mode
            savedIsSpeakerPhoneOn = androidAudioManager.isSpeakerphoneOn
            savedIsMicrophoneMute = androidAudioManager.isMicrophoneMute
            hasWiredHeadset = hasWiredHeadset()
            audioFocusChangeListener = OnAudioFocusChangeListener { focusChange: Int ->
                val typeOfChange: String = when (focusChange) {
                    -3 -> "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                    -2 -> "AUDIOFOCUS_LOSS_TRANSIENT"
                    -1 -> "AUDIOFOCUS_LOSS"
                    0 -> "AUDIOFOCUS_INVALID"
                    1 -> "AUDIOFOCUS_GAIN"
                    2 -> "AUDIOFOCUS_GAIN_TRANSIENT"
                    3 -> "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK"
                    4 -> "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE"
                    else -> "AUDIOFOCUS_INVALID"
                }
                Timber.d(

                    "onAudioFocusChange: $typeOfChange"
                )
            }
            val result = androidAudioManager.requestAudioFocus(audioFocusChangeListener, 0, 2)
            if (result == 1) {
                Timber.d("Audio focus request granted for VOICE_CALL streams")
            } else {
                Timber.e("Audio focus request failed")
            }
            androidAudioManager.mode = AudioManager.MODE_IN_CALL
            setMicrophoneMute(false)
            userSelectedAudioDevice = AudioDevice.NONE
            selectedAudioDevice = AudioDevice.NONE
            audioDevices.clear()
            bluetoothManager.start()
            updateAudioDeviceState()
            registerReceiver(
                wiredHeadsetReceiver,
                IntentFilter("android.intent.action.HEADSET_PLUG")
            )
            Timber.d("AudioManager started")
        }
    }

    fun setManageHeadsetByDefault(manageHeadsetByDefault: Boolean) {
        this.manageHeadsetByDefault = manageHeadsetByDefault
    }

    fun setOnWiredHeadsetStateListener(wiredHeadsetStateListener: OnWiredHeadsetStateListener?) {
        this.wiredHeadsetStateListener = wiredHeadsetStateListener
    }

    private fun notifyWiredHeadsetListener(plugged: Boolean, hasMicrophone: Boolean) {
        if (wiredHeadsetStateListener != null) {
            wiredHeadsetStateListener!!.onWiredHeadsetStateChanged(plugged, hasMicrophone)
        }
    }

    fun setManageSpeakerPhoneByProximity(manageSpeakerPhoneByProximity: Boolean) {
        this.manageSpeakerPhoneByProximity = manageSpeakerPhoneByProximity
    }

    fun setManageBluetoothByDefault(manageBluetoothByDefault: Boolean) {
        this.manageBluetoothByDefault = manageBluetoothByDefault
    }

    fun setBluetoothAudioDeviceStateListener(bluetoothAudioDeviceStateListener: BluetoothAudioDeviceStateListener?) {
        this.bluetoothAudioDeviceStateListener = bluetoothAudioDeviceStateListener
    }

    private fun notifyBluetoothAudioDeviceStateListener(connected: Boolean) {
        if (bluetoothAudioDeviceStateListener != null) {
            bluetoothAudioDeviceStateListener!!.onStateChanged(connected)
        }
    }

    fun stop() {
        Timber.d("stop")
        checkIsOnMainThread()
        if (amState != AudioManagerState.RUNNING) {
            Timber.e("Trying to stop AudioManager in incorrect state: %s", amState)
        } else {
            amState = AudioManagerState.UNINITIALIZED
            unregisterReceiver(wiredHeadsetReceiver)
            bluetoothManager.stop()
            setSpeakerphoneOn(savedIsSpeakerPhoneOn)
            setMicrophoneMute(savedIsMicrophoneMute)
            androidAudioManager.mode = savedAudioMode
            androidAudioManager.abandonAudioFocus(audioFocusChangeListener)
            audioFocusChangeListener = null
            Timber.d("Abandoned audio focus for VOICE_CALL streams")
            /*if (proximitySensor != null) {
                proximitySensor.stop()
                proximitySensor = null
            }*/
            audioManagerEvents = null
            Timber.d("AudioManager stopped")
        }
    }

    fun getDefaultAudioDevice(): AudioDevice {
        return defaultAudioDevice
    }

    private fun setAudioDeviceInternal(device: AudioDevice?) {
        Timber.d(

            "setAudioDeviceInternal(device=$device)"
        )
        if (!audioDevices.contains(device)) {
            Timber.e("Invalid audio device selection")
        } else {
            when (device) {
                AudioDevice.SPEAKER_PHONE -> setSpeakerphoneOn(true)
                AudioDevice.EARPIECE -> setSpeakerphoneOn(false)
                AudioDevice.WIRED_HEADSET -> setSpeakerphoneOn(false)
                AudioDevice.BLUETOOTH -> setSpeakerphoneOn(false)
                else -> Timber.e("Invalid audio device selection")
            }
            if (AudioDevice.EARPIECE == device && hasWiredHeadset) {
                selectedAudioDevice = AudioDevice.WIRED_HEADSET
            } else {
                selectedAudioDevice = device
            }
        }
    }

    fun setDefaultAudioDevice(defaultDevice: AudioDevice?) {
        checkIsOnMainThread()
        when (defaultDevice) {
            AudioDevice.SPEAKER_PHONE -> defaultAudioDevice = defaultDevice
            AudioDevice.EARPIECE -> if (hasEarpiece()) {
                defaultAudioDevice = defaultDevice
            } else {
                defaultAudioDevice = AudioDevice.SPEAKER_PHONE
            }
            else -> Timber.e("Invalid default audio device selection")
        }
        Timber.d("setDefaultAudioDevice(device=" + defaultAudioDevice + ")")
        updateAudioDeviceState()
    }

    fun selectAudioDevice(device: AudioDevice) {
        checkIsOnMainThread()
        if (!audioDevices.contains(device)) {
            Timber.e("Can not select " + device + " from available " + audioDevices)
        } else {
            userSelectedAudioDevice = device
            updateAudioDeviceState()
        }
    }

    fun getAudioDevices(): Set<AudioDevice?> {
        checkIsOnMainThread()
        return Collections.unmodifiableSet(HashSet<AudioDevice?>(audioDevices))
    }

    fun getSelectedAudioDevice(): AudioDevice? {
        checkIsOnMainThread()
        return selectedAudioDevice
    }

    private fun registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
        apprtcContext.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver) {
        apprtcContext.unregisterReceiver(receiver)
    }

    private fun setSpeakerphoneOn(on: Boolean) {
        val wasOn = androidAudioManager.isSpeakerphoneOn
        if (wasOn != on) {
            androidAudioManager.isSpeakerphoneOn = on
        }
    }

    private fun setMicrophoneMute(on: Boolean) {
        val wasMuted = androidAudioManager.isMicrophoneMute
        if (wasMuted != on) {
            androidAudioManager.isMicrophoneMute = on
        }
    }

    private fun hasEarpiece(): Boolean {
        return apprtcContext.packageManager.hasSystemFeature("android.hardware.telephony")
    }

    @Deprecated("")
    private fun hasWiredHeadset(): Boolean {
        return if (VERSION.SDK_INT < 23) {
            androidAudioManager.isWiredHeadsetOn
        } else {
            val devices = androidAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val var3 = devices.size
            for (var4 in 0 until var3) {
                val device = devices[var4]
                val type = device.type
                if (type == 3) {
                    Timber.d("hasWiredHeadset: found wired headset")
                    return true
                }
                if (type == 11) {
                    Timber.d("hasWiredHeadset: found USB audio device")
                    return true
                }
            }
            false
        }
    }

    fun updateAudioDeviceState() {
        checkIsOnMainThread()
        Timber.d("--- updateAudioDeviceState: wired headset=" + hasWiredHeadset + ", BT state=" + bluetoothManager.state)
        Timber.d(
            "Device status: available=" + audioDevices + ", selected=" + selectedAudioDevice + ", user selected=" + userSelectedAudioDevice
        )
        if (bluetoothManager.state === State.HEADSET_AVAILABLE || bluetoothManager.state ===
            State.HEADSET_UNAVAILABLE || bluetoothManager.state === State.SCO_DISCONNECTING
        ) {
            bluetoothManager.updateDevice()
        }
        val newAudioDevices: MutableSet<AudioDevice?> = HashSet<AudioDevice?>()
        if (bluetoothManager.state === State.SCO_CONNECTED || bluetoothManager.state === State.SCO_CONNECTING || bluetoothManager.state === State.HEADSET_AVAILABLE) {
            newAudioDevices.add(AudioDevice.BLUETOOTH)
            if (!audioDevices.isEmpty() && !audioDevices.contains(AudioDevice.BLUETOOTH)) {
                if (manageBluetoothByDefault) {
                    userSelectedAudioDevice = AudioDevice.BLUETOOTH
                }
                notifyBluetoothAudioDeviceStateListener(true)
            }
        }
        if (hasWiredHeadset) {
            newAudioDevices.add(AudioDevice.WIRED_HEADSET)
        }
        newAudioDevices.add(AudioDevice.SPEAKER_PHONE)
        if (hasEarpiece()) {
            newAudioDevices.add(AudioDevice.EARPIECE)
        }
        var audioDeviceSetUpdated = audioDevices != newAudioDevices
        audioDevices = newAudioDevices
        if (bluetoothManager.state === State.HEADSET_UNAVAILABLE && userSelectedAudioDevice == AudioDevice.BLUETOOTH) {
            userSelectedAudioDevice = AudioDevice.NONE
        }
        if (!hasWiredHeadset && userSelectedAudioDevice == AudioDevice.WIRED_HEADSET) {
            userSelectedAudioDevice = AudioDevice.NONE
        }
        val needBluetoothAudioStart =
            bluetoothManager.state === State.HEADSET_AVAILABLE && (userSelectedAudioDevice == AudioDevice.NONE || userSelectedAudioDevice == AudioDevice.BLUETOOTH)
        val needBluetoothAudioStop =
            (bluetoothManager.state === State.SCO_CONNECTED || bluetoothManager.state === State.SCO_CONNECTING) && userSelectedAudioDevice != AudioDevice.NONE && userSelectedAudioDevice != AudioDevice.BLUETOOTH
        if (bluetoothManager.state === State.HEADSET_AVAILABLE || bluetoothManager.state === State.SCO_CONNECTING || bluetoothManager.state === State.SCO_CONNECTED) {
            Timber.d("Need BT audio: start=$needBluetoothAudioStart, stop=$needBluetoothAudioStop BT state= ${bluetoothManager.state}")
        }
        if (needBluetoothAudioStop) {
            bluetoothManager.stopScoAudio()
            bluetoothManager.updateDevice()
        }
        if (needBluetoothAudioStart && !needBluetoothAudioStop && !bluetoothManager.startScoAudio()) {
            audioDevices.remove(AudioDevice.BLUETOOTH)
            notifyBluetoothAudioDeviceStateListener(false)
            audioDeviceSetUpdated = true
        }
        val newAudioDevice: AudioDevice? = if (userSelectedAudioDevice != AudioDevice.NONE) {
            userSelectedAudioDevice
        } else {
            defaultAudioDevice
        }
        selectedAudioDevice?.let { audioDevice ->
            if (newAudioDevice != audioDevice || audioDeviceSetUpdated) {
                setAudioDeviceInternal(newAudioDevice)
                Timber.d("New device status: available=$audioDevices, selected= $selectedAudioDevice")
                audioManagerEvents?.onAudioDeviceChanged(audioDevice, audioDevices)
            }
        }
        Timber.d("--- updateAudioDeviceState done")
    }

    companion object {
        fun create(context: Context): AppRTCAudioManager {
            return AppRTCAudioManager(context)
        }
    }

    interface BluetoothAudioDeviceStateListener {
        fun onStateChanged(var1: Boolean)
    }

    interface AudioManagerEvents {
        fun onAudioDeviceChanged(var1: AudioDevice, var2: Set<AudioDevice?>)
    }

    interface OnWiredHeadsetStateListener {
        fun onWiredHeadsetStateChanged(var1: Boolean, var2: Boolean)
    }

    private inner class WiredHeadsetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra("state", 0)
            val microphone = intent.getIntExtra("microphone", 0)
            val name = intent.getStringExtra("name")
            Timber.d("WiredHeadsetReceiver.onReceive${getThreadInfo()}: a= ${intent.action}, s=" + (if (state == 0) "unplugged" else "plugged").toString() + ", m=" + (if (microphone == 1) "mic" else "no mic").toString() + ", n=" + name.toString() + ", sb=" + this.isInitialStickyBroadcast)
            hasWiredHeadset = state == 1
            notifyWiredHeadsetListener(state == 1, microphone == 1)
            if (manageHeadsetByDefault) {
                if (hasWiredHeadset) {
                    userSelectedAudioDevice = AudioDevice.WIRED_HEADSET
                }
                updateAudioDeviceState()
            }
        }

        /*companion object {
            private const val STATE_UNPLUGGED = 0
            private const val STATE_PLUGGED = 1
            private const val HAS_NO_MIC = 0
            private const val HAS_MIC = 1
        }*/
    }

    enum class AudioManagerState {
        UNINITIALIZED, PREINITIALIZED, RUNNING
    }

    enum class AudioDevice {
        SPEAKER_PHONE, WIRED_HEADSET, EARPIECE, BLUETOOTH, NONE
    }

    init {
        defaultAudioDevice = AudioDevice.SPEAKER_PHONE
        audioDevices = HashSet<AudioDevice?>()
        checkIsOnMainThread()
        apprtcContext = context.applicationContext
        androidAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        bluetoothManager = AppRTCBluetoothManager.create(context, this)
        wiredHeadsetReceiver = WiredHeadsetReceiver()
        amState = AudioManagerState.UNINITIALIZED
        Timber.d("defaultAudioDevice: %s", defaultAudioDevice)
    }
}

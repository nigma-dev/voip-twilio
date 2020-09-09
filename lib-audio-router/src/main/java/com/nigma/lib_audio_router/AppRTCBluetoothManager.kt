package com.nigma.lib_audio_router

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.Process
import timber.log.Timber

class AppRTCBluetoothManager
private constructor(
    context: Context,
    audioManager: AppRTCAudioManager
) {
    private val apprtcContext: Context
    private val apprtcAudioManager: AppRTCAudioManager
    private val audioManager: AudioManager
    private val handler: Handler
    var scoConnectionAttempts = 0
    private var bluetoothState: State
    private val bluetoothServiceListener: ServiceListener
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothHeadset: BluetoothHeadset? = null
    private var bluetoothDevice: BluetoothDevice? = null
    private val bluetoothHeadsetReceiver: BroadcastReceiver
    private val bluetoothTimeoutRunnable = Runnable { bluetoothTimeout() }

    val state: State
        get() {
            checkIsOnMainThread()
            return bluetoothState
        }

    @SuppressLint("MissingPermission")
    fun start() {
        checkIsOnMainThread()
        Timber.d("start")
        if (!hasPermission(apprtcContext, "android.permission.BLUETOOTH")) {
            Timber.w("Process (pid=" + Process.myPid() + ") lacks BLUETOOTH permission")
        } else if (bluetoothState != State.UNINITIALIZED) {
            Timber.w("Invalid BT state")
        } else {
            bluetoothHeadset = null
            bluetoothDevice = null
            scoConnectionAttempts = 0
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Timber.w("Device does not support Bluetooth")
            } else if (!audioManager.isBluetoothScoAvailableOffCall) {
                Timber.e("Bluetooth SCO audio is not available off call")
            } else {
                logBluetoothAdapterInfo(bluetoothAdapter)
                if (!getBluetoothProfileProxy(apprtcContext, bluetoothServiceListener, 1)) {
                    Timber.e("BluetoothAdapter.getProfileProxy(HEADSET) failed")
                } else {
                    val bluetoothHeadsetFilter = IntentFilter()
                    bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")
                    bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")
                    registerReceiver(bluetoothHeadsetReceiver, bluetoothHeadsetFilter)
                    Timber.d(
                        "HEADSET profile state: " + stateToString(
                            bluetoothAdapter!!.getProfileConnectionState(1)
                        )
                    )
                    Timber.d("Bluetooth proxy for headset profile has started")
                    bluetoothState = State.HEADSET_UNAVAILABLE
                    Timber.d("start done: BT state=" + bluetoothState)
                }
            }
        }
    }

    fun stop() {
        checkIsOnMainThread()
        Timber.d("stop: BT state=" + bluetoothState)
        if (bluetoothAdapter != null) {
            stopScoAudio()
            if (bluetoothState != State.UNINITIALIZED) {
                unregisterReceiver(bluetoothHeadsetReceiver)
                cancelTimer()
                if (bluetoothHeadset != null) {
                    bluetoothAdapter!!.closeProfileProxy(1, bluetoothHeadset)
                    bluetoothHeadset = null
                }
                bluetoothAdapter = null
                bluetoothDevice = null
                bluetoothState = State.UNINITIALIZED
                Timber.d("stop done: BT state=" + bluetoothState)
            }
        }
    }

    fun startScoAudio(): Boolean {
        checkIsOnMainThread()
        Timber.d("startSco: BT state=" + bluetoothState + ", attempts: " + scoConnectionAttempts + ", SCO is on: ${isScoOn}")
        return if (scoConnectionAttempts >= 2) {
            Timber.e("BT SCO connection fails - no more attempts")
            false
        } else if (bluetoothState != State.HEADSET_AVAILABLE) {
            Timber.e("BT SCO connection fails - no headset available")
            false
        } else {
            Timber.d("Starting Bluetooth SCO and waits for ACTION_AUDIO_STATE_CHANGED...")
            bluetoothState = State.SCO_CONNECTING
            audioManager.startBluetoothSco()
            audioManager.isBluetoothScoOn = true
            ++scoConnectionAttempts
            startTimer()
            Timber.d(
                "startScoAudio done: BT state=" + bluetoothState + ", SCO is on: " + isScoOn
            )
            true
        }
    }

    fun stopScoAudio() {
        checkIsOnMainThread()
        Timber.d("stopScoAudio: BT state=" + bluetoothState + ", SCO is on: " + isScoOn)
        if (bluetoothState == State.SCO_CONNECTING || bluetoothState == State.SCO_CONNECTED) {
            cancelTimer()
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false
            bluetoothState = State.SCO_DISCONNECTING
            Timber.d(

                "stopScoAudio done: BT state=" + bluetoothState + ", SCO is on: " + isScoOn
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun updateDevice() {
        if (bluetoothState != State.UNINITIALIZED && bluetoothHeadset != null) {
            Timber.d("updateDevice")
            val devices = bluetoothHeadset!!.connectedDevices
            if (devices.isEmpty()) {
                bluetoothDevice = null
                bluetoothState = State.HEADSET_UNAVAILABLE
                Timber.d("No connected bluetooth headset")
            } else {
                bluetoothDevice = devices[0] as BluetoothDevice
                bluetoothState = State.HEADSET_AVAILABLE
                Timber.d(

                    "Connected bluetooth headset: name=" + bluetoothDevice!!.name + ", state=" + stateToString(
                        bluetoothHeadset!!.getConnectionState(bluetoothDevice)
                    ) + ", SCO audio=" + bluetoothHeadset!!.isAudioConnected(
                        bluetoothDevice
                    )
                )
            }
            Timber.d("updateDevice done: BT state=" + bluetoothState)
        }
    }

    private fun getAudioManager(context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
        apprtcContext.registerReceiver(receiver, filter)
    }

    private fun unregisterReceiver(receiver: BroadcastReceiver) {
        apprtcContext.unregisterReceiver(receiver)
    }

    private fun getBluetoothProfileProxy(
        context: Context,
        listener: ServiceListener,
        profile: Int
    ): Boolean {
        return bluetoothAdapter!!.getProfileProxy(context, listener, profile)
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return apprtcContext.checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun logBluetoothAdapterInfo(localAdapter: BluetoothAdapter?) {
        Timber.d(

            "BluetoothAdapter: enabled=" + localAdapter!!.isEnabled + ", state=" + stateToString(
                localAdapter.state
            ) + ", name=" + localAdapter.name + ", address=" + localAdapter.address
        )
        val pairedDevices = localAdapter.bondedDevices
        if (!pairedDevices.isEmpty()) {
            Timber.d("paired devices:")
            val var3: Iterator<*> = pairedDevices.iterator()
            while (var3.hasNext()) {
                val device = var3.next() as BluetoothDevice
                Timber.d(" name=" + device.name + ", address=" + device.address)
            }
        }
    }

    private fun updateAudioDeviceState() {
        checkIsOnMainThread()
        Timber.d("updateAudioDeviceState")
        apprtcAudioManager.updateAudioDeviceState()
    }

    private fun startTimer() {
        checkIsOnMainThread()
        Timber.d("startTimer")
        handler.postDelayed(bluetoothTimeoutRunnable, 4000L)
    }

    private fun cancelTimer() {
        checkIsOnMainThread()
        Timber.d("cancelTimer")
        handler.removeCallbacks(bluetoothTimeoutRunnable)
    }

    @SuppressLint("MissingPermission")
    private fun bluetoothTimeout() {
        checkIsOnMainThread()
        if (bluetoothState != State.UNINITIALIZED && bluetoothHeadset != null) {
            Timber.d(

                "bluetoothTimeout: BT state=" + bluetoothState + ", attempts: " + scoConnectionAttempts + ", SCO is on: " + isScoOn
            )
            if (bluetoothState == State.SCO_CONNECTING) {
                var scoConnected = false
                val devices = bluetoothHeadset!!.connectedDevices
                if (devices.size > 0) {
                    bluetoothDevice = devices[0] as BluetoothDevice
                    if (bluetoothHeadset!!.isAudioConnected(bluetoothDevice)) {
                        Timber.d("SCO connected with " + bluetoothDevice!!.name)
                        scoConnected = true
                    } else {
                        Timber.d("SCO is not connected with " + bluetoothDevice!!.name)
                    }
                }
                if (scoConnected) {
                    bluetoothState = State.SCO_CONNECTED
                    scoConnectionAttempts = 0
                } else {
                    Timber.w("BT failed to connect after timeout")
                    stopScoAudio()
                }
                updateAudioDeviceState()
                Timber.d("bluetoothTimeout done: BT state=" + bluetoothState)
            }
        }
    }

    private val isScoOn: Boolean
        private get() = audioManager.isBluetoothScoOn

    private fun stateToString(state: Int): String {
        return when (state) {
            0 -> "DISCONNECTED"
            1 -> "CONNECTING"
            2 -> "CONNECTED"
            3 -> "DISCONNECTING"
            4, 5, 6, 7, 8, 9 -> "INVALID"
            10 -> "OFF"
            11 -> "TURNING_ON"
            12 -> "ON"
            13 -> "TURNING_OFF"
            else -> "INVALID"
        }
    }

    companion object {
        private val TAG = AppRTCBluetoothManager::class.java.simpleName
        private const val BLUETOOTH_SCO_TIMEOUT_MS = 4000
        private const val MAX_SCO_CONNECTION_ATTEMPTS = 2
        fun create(context: Context, audioManager: AppRTCAudioManager): AppRTCBluetoothManager {
            Timber.d("create ${getThreadInfo()}")
            return AppRTCBluetoothManager(context, audioManager)
        }

    }

    private inner class BluetoothHeadsetBroadcastReceiver() :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (bluetoothState != State.UNINITIALIZED) {
                val action = intent.action
                val state: Int
                if (action == "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED") {
                    state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0)
                    Timber.d(

                        "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=" + stateToString(
                            state
                        ) + ", sb=" + this.isInitialStickyBroadcast + ", BT state: " + bluetoothState
                    )
                    if (state == 2) {
                        scoConnectionAttempts = 0
                        updateAudioDeviceState()
                    } else if (state != 1 && state != 3 && state == 0) {
                        stopScoAudio()
                        updateAudioDeviceState()
                    }
                } else if (action == "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED") {
                    state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10)
                    Timber.d(

                        "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=" + stateToString(
                            state
                        ) + ", sb=" + this.isInitialStickyBroadcast + ", BT state: " + bluetoothState
                    )
                    if (state == 12) {
                        cancelTimer()
                        if (bluetoothState == State.SCO_CONNECTING) {
                            Timber.d("+++ Bluetooth audio SCO is now connected")
                            bluetoothState = State.SCO_CONNECTED
                            scoConnectionAttempts = 0
                            updateAudioDeviceState()
                        } else {
                            Timber.w("Unexpected state BluetoothHeadset.STATE_AUDIO_CONNECTED")
                        }
                    } else if (state == 11) {
                        Timber.d("+++ Bluetooth audio SCO is now connecting...")
                    } else if (state == 10) {
                        Timber.d("+++ Bluetooth audio SCO is now disconnected")
                        if (this.isInitialStickyBroadcast) {
                            Timber.d(

                                "Ignore STATE_AUDIO_DISCONNECTED initial sticky broadcast."
                            )
                            return
                        }
                        updateAudioDeviceState()
                    }
                }
                Timber.d("onReceive done: BT state=" + bluetoothState)
            }
        }
    }

    private inner class BluetoothServiceListener() : ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == 1 && bluetoothState != State.UNINITIALIZED) {
                Timber.d(

                    "BluetoothServiceListener.onServiceConnected: BT state=$bluetoothState"
                )
                bluetoothHeadset = proxy as BluetoothHeadset
                updateAudioDeviceState()
                Timber.d("onServiceConnected done: BT state=$bluetoothState")
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == 1 && bluetoothState != State.UNINITIALIZED) {
                Timber.d(

                    "BluetoothServiceListener.onServiceDisconnected: BT state=$bluetoothState"
                )
                stopScoAudio()
                bluetoothHeadset = null
                bluetoothDevice = null
                bluetoothState = State.HEADSET_UNAVAILABLE
                updateAudioDeviceState()
                Timber.d("onServiceDisconnected done: BT state=$bluetoothState")
            }
        }
    }

    enum class State {
        UNINITIALIZED, ERROR, HEADSET_UNAVAILABLE, HEADSET_AVAILABLE, SCO_DISCONNECTING, SCO_CONNECTING, SCO_CONNECTED
    }

    init {
        checkIsOnMainThread()
        apprtcContext = context
        apprtcAudioManager = audioManager
        this.audioManager = getAudioManager(context)
        bluetoothState = State.UNINITIALIZED
        bluetoothServiceListener = BluetoothServiceListener()
        bluetoothHeadsetReceiver = BluetoothHeadsetBroadcastReceiver()
        handler = Handler(Looper.getMainLooper())
    }
}

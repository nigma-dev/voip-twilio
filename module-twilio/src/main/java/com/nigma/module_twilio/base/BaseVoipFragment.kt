package com.nigma.module_twilio.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.module_twilio.R
import com.nigma.module_twilio.callbacks.VoipActionStateContract
import com.nigma.module_twilio.callbacks.VoipMediaActionStateCallback
import com.nigma.module_twilio.callbacks.WrappedParticipantEvent
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.ui.auxiliary_ui.AudioOutputDevicesSheet
import com.nigma.module_twilio.utils.User
import com.nigma.module_twilio.utils.srcGreenBgWhite
import com.nigma.module_twilio.utils.srcWhiteBgGreen
import com.twilio.video.CameraCapturer
import com.twilio.video.NetworkQualityLevel
import com.twilio.video.Participant
import com.twilio.video.RemoteVideoTrack
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

abstract class BaseVoipFragment(
    protected val binder: VoipService.ServiceBinder
) : Fragment(), WrappedParticipantEvent, VoipMediaActionStateCallback, VoipActionStateContract {

    private lateinit var voipActivity: VoipActivity

    val participantCallback: WrappedParticipantEvent
        get() = this

    val voipMediaActionStateCallback: VoipMediaActionStateCallback
        get() = this

    private val audioBtn: FloatingActionButton?
        get() = btnAudio()

    private val micBtn: FloatingActionButton?
        get() = btnMic()

    private val hungUpBtn: FloatingActionButton?
        get() = btnHungUp()

    private val statusTv: TextView?
        get() = tvStatus()

    private val usernameTv: TextView?
        get() = tvUsername()

    private val profileCiv: CircleImageView?
        get() = ivUserProfile()

    private val user: User
        get() {
            return voipActivity.user
        }

    private val voipServiceIntent: Intent
        get() = Intent(context, VoipService::class.java)

    @Throws(IllegalStateException::class)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("onAttach")
        if (context !is VoipActivity) throw  IllegalStateException("please attach form VoipActivity")
        voipActivity = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated")
        initUi()
    }

    override fun onDestroyView() {
        Timber.i("onDestroyView")
        super.onDestroyView()
    }


    protected fun inflateUi(@LayoutRes layout: Int): View? {
        return layoutInflater.inflate(layout, null, false)
    }

    protected fun commandTheService(action: String) =
        with(voipServiceIntent) {
            this.action = action
            activity?.startService(this)
            Timber.v("commandTheService : action -> $action")
        }

    /*region voip related callback*/
    override fun onVideoTrackAvailable(videoTrack: RemoteVideoTrack) {}

    override fun onAudioTrackStateChange(enable: Boolean) {
        Toast.makeText(context, "onAudioTrackStateChange $enable", Toast.LENGTH_SHORT).show()
    }

    override fun onVideoTrackStateChange(enable: Boolean) {
        Toast.makeText(context, "onVideoTrackStateChange $enable", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkStateChange(participant: Participant, level: NetworkQualityLevel) {
        Toast.makeText(context, "onNetworkStateChange $level", Toast.LENGTH_SHORT).show()
    }
    /*endregion*/

    /*region audio output related callback*/
    override fun onAudioRoutedDeviceChanged(
        selectedDevice: AudioDevice,
        availableHeadset: Boolean
    ) {
        handleAudioOutputBtn(selectedDevice, availableHeadset)
    }

    override fun onAudioDevicesListAvailable(devices: List<AudioDevice>) {
        AudioOutputDevicesSheet(devices) { device ->
            with(voipServiceIntent) {
                action = VoipService.ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE
                putExtra("selected_device", device)
                voipActivity.startService(this)
                Timber.v("change audio $device")
            }
        }.show(voipActivity.supportFragmentManager, "tag")
    }

    /*override fun onAudioInputDeviceStateChange(mute: Boolean) {
        Toast.makeText(voipActivity, "muted $mute", Toast.LENGTH_SHORT).show()
    }*/
    /*endregion*/

    override fun onMicrophoneStateChanged(enable: Boolean) = handleMicrophoneBtn(enable)

    override fun onCameraStateChanged(enable: Boolean) {}

    override fun onCameraStateFlipped(source: CameraCapturer.CameraSource) {}

    override fun onConnectionStateChange(connectionState: String) {
        Toast.makeText(context, connectionState, Toast.LENGTH_SHORT).show()
    }

    /*region UI related LOGIC*/

    private fun initUi() {
        setupActionClick()
        usernameTv?.text = user.name
        loadProfileImageView(profileCiv)
        with(binder.selectedDevice) {
            this?.let { device ->
                onAudioRoutedDeviceChanged(device, binder.auxiliaryAvailable)
            }
        }
        handleMicrophoneBtn(binder.microphoneState)
    }

    private fun setupActionClick() {
        audioBtn?.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_HANDLE_AUDIO_OUTPUT)
        }
        micBtn?.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_HANDLE_MIC)
        }
        hungUpBtn?.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_DISCONNECT_ROOM)
            voipActivity.clickHungUp()
        }
        Timber.i("setupActionClick | ${::voipActivity.isInitialized}")
    }

    private fun handleMicrophoneBtn(enable: Boolean) {
        micBtn?.let { btnMic ->
            if (enable) {
                srcGreenBgWhite(btnMic, R.drawable.ic_mic)
            } else {
                srcWhiteBgGreen(btnMic, R.drawable.ic_mic_off)
            }
        }
    }

    private fun handleAudioOutputBtn(
        selectedDevice: AudioDevice,
        availableHeadset: Boolean
    ) {
        audioBtn?.let { btnAudio ->
            val drawable = if (availableHeadset) {
                when (selectedDevice) {
                    AudioDevice.AUDIO_JACK -> R.drawable.ic_headset_arrow
                    AudioDevice.BLUETOOTH -> R.drawable.ic_bluetooth_arrow
                    else -> R.drawable.ic_speaker_arrow
                }
            } else {
                R.drawable.ic_speaker
            }
            val actionTint = selectedDevice == AudioDevice.EARPIECE
            if (actionTint) {
                srcWhiteBgGreen(btnAudio, drawable)
            } else {
                srcGreenBgWhite(btnAudio, drawable)
            }
        }
    }

    private fun loadProfileImageView(iv: CircleImageView?) {
        iv?.let {
            Glide
                .with(this)
                .load(user)
                .centerCrop()
                .placeholder(getPlaceholder())
                .into(it)
        }
    }

    private fun getPlaceholder(): Int {
        return if (voipActivity.packageName.contains("doctor")) {
            R.drawable.ic_user
        } else {
            R.drawable.ic_doctor
        }
    }
    /*endregion*/
}
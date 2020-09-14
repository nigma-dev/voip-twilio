package com.nigma.module_twilio.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.module_twilio.R
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.callbacks.VoipActionHookContract
import com.nigma.module_twilio.callbacks.VoipLocalMediaStateCallback
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.ui.auxiliary_ui.AudioOutputDevicesSheet
import com.nigma.module_twilio.utils.*
import de.hdodenhof.circleimageview.CircleImageView

abstract class BaseVoipActionHookFragment : Fragment(), VoipActionHookContract,
    VoipLocalMediaStateCallback {

    private lateinit var voipActivity: VoipActivity

    private val audioBtn: FloatingActionButton?
        get() = btnAudio()

    private val micBtn: FloatingActionButton?
        get() = btnMic()

    private val hungUpBtn: FloatingActionButton?
        get() = btnHungUp()

    protected val statusTv: TextView?
        get() = tvStatus()

    protected val usernameTv: TextView?
        get() = tvUsername()

    protected val profileCiv: CircleImageView?
        get() = ivUserProfile()

    protected val user: MutableLiveData<User>
        get() = voipActivity.user

    private val voipServiceIntent: Intent
        get() = Intent(context, VoipService::class.java)

    @Throws(IllegalStateException::class)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is VoipActivity) throw  IllegalStateException("please attach form VoipActivity")
        voipActivity = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionClick()
    }

    protected fun inflateUi(@LayoutRes layout: Int): View? {
        return layoutInflater.inflate(layout, null, false)
    }

    protected fun commandTheService(action: String) =
        with(voipServiceIntent) {
            this.action = action
            activity?.startService(this)
        }

    private fun showSelectAudioDeviceSheet(devices: List<AudioDevice>) {
        AudioOutputDevicesSheet(devices) {
            with(voipServiceIntent) {
                action = ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE
                voipActivity.startService(this)
            }
        }.show(voipActivity.supportFragmentManager, "tag")
    }

    private fun setupActionClick() {
        audioBtn?.setOnClickListener {
            commandTheService(ACTION_VOIP_HANDLE_AUDIO_OUTPUT)
        }
        micBtn?.setOnClickListener {
            commandTheService(ACTION_VOIP_HANDLE_MIC)
        }
        hungUpBtn?.setOnClickListener {
            commandTheService(ACTION_VOIP_DISCONNECT_ROOM)
            voipActivity.clickHungUp()
        }
    }

    protected fun handleMicrophoneBtn(enable: Boolean) {
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


    override fun onAudioRoutedDeviceChanged(
        selectedDevice: AudioDevice,
        availableHeadset: Boolean
    ) {
        handleAudioOutputBtn(selectedDevice, availableHeadset)
    }

    override fun onAudioDevicesListAvailable(devices: List<AudioDevice>) {
        showSelectAudioDeviceSheet(devices)
    }

    override fun onMicrophoneStateChanged(enable: Boolean) = handleMicrophoneBtn(enable)

    /*endregion*/
}
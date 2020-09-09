package com.nigma.module_twilio.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.model.AudioDeviceModel
import com.nigma.module_twilio.callbacks.AudioRoutingCallback
import com.nigma.module_twilio.callbacks.WrappedParticipantEvent
import com.nigma.module_twilio.service.VoipService
import com.nigma.module_twilio.ui.AudioOutputDevicesSheet
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.utils.srcGreenBgWhite
import com.nigma.module_twilio.utils.srcWhiteBgGreen
import com.twilio.video.NetworkQualityLevel
import com.twilio.video.Participant
import com.twilio.video.RemoteVideoTrack
import kotlinx.android.synthetic.main.layout_profile.*
import timber.log.Timber

abstract class BaseVoipFragment(
    protected val binder: VoipService.ServiceBinder
) : Fragment(), WrappedParticipantEvent, AudioRoutingCallback {

    private lateinit var voipActivity: VoipActivity

    val participantCallback: WrappedParticipantEvent
        get() {
            return this
        }

    val audioRoutingCallback: AudioRoutingCallback
        get() {
            return this
        }

    private val voipServiceIntent by lazy { Intent(context, VoipService::class.java) }

    @Throws(IllegalStateException::class)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is VoipActivity) throw  IllegalStateException("please attach form VoipActivity")
        voipActivity = context
    }

    protected fun inflateUi(@LayoutRes layout: Int): View? {
        return layoutInflater.inflate(layout, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionClick(btnAudio(), btnMic(), btnHungUp())
        val placeholder = if (voipActivity.packageName.contains("doctor")) {
            R.drawable.ic_user
        } else {
            R.drawable.ic_doctor
        }
        voipActivity
            .userLiveData
            .observe(viewLifecycleOwner, { user ->
                tv_name.text = user.name
                Glide
                    .with(this)
                    .load(user.imageUrl)
                    .centerCrop()
                    .placeholder(placeholder)
                    .into(imgv_image);
            })
    }

    private fun setupActionClick(
        audio: FloatingActionButton,
        mic: FloatingActionButton,
        hungUp: FloatingActionButton
    ) {
        audio.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_HANDLE_AUDIO_OUTPUT)
        }
        mic.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_HANDLE_MIC)
        }
        hungUp.setOnClickListener {
            commandTheService(VoipService.ACTION_VOIP_DISCONNECT_ROOM)
            voipActivity.clickHungUp()
        }
        Timber.i("setupActionClick | ${::voipActivity.isInitialized}")
    }

    fun commandTheService(action: String) =
        with(voipServiceIntent) {
            this.action = action
            activity?.startService(this)
            Timber.v("commandTheService : action -> $action")
        }

    /*region common callback*/
    abstract fun btnAudio(): FloatingActionButton

    abstract fun btnMic(): FloatingActionButton

    abstract fun btnHungUp(): FloatingActionButton
    /*endregion*/

    /*region voip related callback*/
    override fun onVideoTrackAvailable(videoTrack: RemoteVideoTrack) {}

    override fun onAudioTrackStateChange(enable: Boolean) {}

    override fun onVideoTrackStateChange(enable: Boolean) {}

    override fun onNetworkStateChange(participant: Participant, level: NetworkQualityLevel) {}
    /*endregion*/

    /*region audio output related callback*/
    override fun onAudioOutputDeviceChange(resource: Int, normal: Boolean) {
        if (normal) {
            srcWhiteBgGreen(btnAudio(), resource)
        } else {
            srcGreenBgWhite(btnAudio(), resource)
        }
    }

    override fun onAudioDevicesListAvailable(devices: List<AudioDeviceModel>) {
        AudioOutputDevicesSheet(devices) { device ->
            with(voipServiceIntent) {
                action = VoipService.ACTION_VOIP_HANDLE_CHANGE_AUDIO_DEVICE
                putExtra("audio-device", device.id)
                voipActivity.startService(this)
                Timber.v("change audio ${device.id.name}")
            }
        }.show(voipActivity.supportFragmentManager, "tag")
    }

    override fun onAudioInputDeviceStateChange(mute: Boolean) {
        Toast.makeText(voipActivity, "muted $mute", Toast.LENGTH_SHORT).show()
    }
    /*endregion*/

}
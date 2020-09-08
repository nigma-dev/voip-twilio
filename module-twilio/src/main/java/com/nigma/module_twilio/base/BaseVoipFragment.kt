package com.nigma.module_twilio.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.seg_interface.WrappedParticipantEvent
import com.nigma.module_twilio.service.VoipService
import com.nigma.module_twilio.ui.VoipActivity
import com.twilio.video.NetworkQualityLevel
import com.twilio.video.Participant
import com.twilio.video.RemoteVideoTrack
import kotlinx.android.synthetic.main.layout_profile.*
import timber.log.Timber

abstract class BaseVoipFragment(
    protected val binder: VoipService.ServiceBinder
) : Fragment(), WrappedParticipantEvent {

    private lateinit var voipActivity: VoipActivity

    val participantListener: WrappedParticipantEvent
        get() {
            return this
        }

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
        with(binder) {
            audio.setOnClickListener { clickAudioOutput() }
            mic.setOnClickListener {
                clickMicrophone()
            }
            hungUp.setOnClickListener {
                clickHungUp()
                voipActivity.clickHungUp()
            }
            Timber.i("setupActionClick | ${::voipActivity.isInitialized}")
        }
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

}
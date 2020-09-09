package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.base.BaseVoipFragment
import com.nigma.module_twilio.service.VoipService
import com.nigma.module_twilio.service.VoipService.ServiceBinder
import com.twilio.video.RemoteVideoTrack
import kotlinx.android.synthetic.main.fragment_voip_audio.*
import kotlinx.android.synthetic.main.fragment_voip_video.*
import kotlinx.android.synthetic.main.layout_video_buttons.*
import timber.log.Timber

class VideoVoipFragment(binder: ServiceBinder) : BaseVoipFragment(binder) {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("onCreateView")
        return inflateUi(R.layout.fragment_voip_video)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated")
        binder
            .localVideoTrack
            .addRenderer(vv_secondary)

        fab_camera_handle
            .setOnClickListener {
                commandTheService(VoipService.ACTION_VOIP_HANDLE_CAMERA)
            }

        iv_btn_flip_cam
            .setOnClickListener {
                commandTheService(VoipService.ACTION_VOIP_HANDLE_CAMERA_FLIP)
            }
    }

    override fun onVideoTrackAvailable(videoTrack: RemoteVideoTrack) {
        with(binder.localVideoTrack) {
            removeRenderer(vv_secondary)
            addRenderer(vv_primary)
        }
        videoTrack.addRenderer(vv_secondary)
        iv_btn_flip_cam.visibility = View.VISIBLE
    }

    override fun onVideoTrackStateChange(enable: Boolean) {
        vv_secondary.visibility = if (enable) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun btnAudio(): FloatingActionButton = fab_audio_output

    override fun btnMic(): FloatingActionButton = fab_mic_handle

    override fun btnHungUp(): FloatingActionButton = fab_call_end
}
package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.base.BaseVoipFragment
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.VoipService.ServiceBinder
import com.nigma.module_twilio.utils.srcGreenBgWhite
import com.nigma.module_twilio.utils.srcWhiteBgGreen
import com.twilio.video.CameraCapturer
import com.twilio.video.RemoteVideoTrack
import de.hdodenhof.circleimageview.CircleImageView
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
            ?.addRenderer(vv_secondary)

        fab_camera_handle
            .setOnClickListener {
                commandTheService(VoipService.ACTION_VOIP_HANDLE_CAMERA)
            }

        iv_btn_flip_cam
            .setOnClickListener {
                commandTheService(VoipService.ACTION_VOIP_HANDLE_CAMERA_FLIP)
            }
        handleCamera(binder.cameraState)
    }

    override fun onDestroyView() {
        Timber.i("onDestroyView")
        super.onDestroyView()
    }

    override fun onVideoTrackAvailable(videoTrack: RemoteVideoTrack) {
        with(binder.localVideoTrack) {
            this?.let {
                removeRenderer(vv_secondary)
                addRenderer(vv_primary)
            }
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

    override fun btnAudio(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_audio_output)
    }

    override fun btnMic(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_mic_handle)
    }

    override fun btnHungUp(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_call_end)
    }

    override fun tvStatus(): TextView? {
        return view?.findViewById(R.id.tv_status)
    }

    override fun tvUsername(): TextView? {
        return view?.findViewById(R.id.tv_name)
    }

    override fun ivUserProfile(): CircleImageView? {
        return view?.findViewById(R.id.civ_profile)
    }

    override fun onCameraStateChanged(
        enable: Boolean
    ) = handleCamera(enable)

    override fun onCameraStateFlipped(
        source: CameraCapturer.CameraSource
    ) = handleCameraFlip(source)

    private fun handleCamera(enable: Boolean) {
        group_local.visibility =
            if (enable) {
                srcGreenBgWhite(fab_camera_handle, R.drawable.ic_video_black)
                View.VISIBLE
            } else {
                srcWhiteBgGreen(fab_camera_handle, R.drawable.ic_video_off)
                View.GONE
            }
    }


    private fun handleCameraFlip(source: CameraCapturer.CameraSource) {
        if (source == CameraCapturer.CameraSource.FRONT_CAMERA) {

        } else {

        }
    }
}
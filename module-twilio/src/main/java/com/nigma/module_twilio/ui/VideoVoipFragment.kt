package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.VoipServiceBinder
import com.nigma.module_twilio.utils.ACTION_VOIP_HANDLE_CAMERA
import com.nigma.module_twilio.utils.ACTION_VOIP_HANDLE_CAMERA_FLIP
import com.nigma.module_twilio.utils.srcGreenBgWhite
import com.nigma.module_twilio.utils.srcWhiteBgGreen
import com.twilio.video.CameraCapturer
import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrack
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_voip_video.*
import kotlinx.android.synthetic.main.layout_video_buttons.*
import timber.log.Timber

class VideoVoipFragment(binder: VoipServiceBinder) : VoipFragment(binder) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflateUi(R.layout.fragment_voip_video)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vv_primary.setZOrderMediaOverlay(true)
        vv_primary.setZOrderOnTop(true)
        binder
            .localVideoTrack
            ?.addRenderer(vv_secondary)

        fab_camera_handle
            .setOnClickListener {
                commandTheService(ACTION_VOIP_HANDLE_CAMERA)
            }

        iv_btn_flip_cam
            .setOnClickListener {
                commandTheService(ACTION_VOIP_HANDLE_CAMERA_FLIP)
            }
        handleCamera(binder.cameraState)
    }

    override fun onParticipantVideoTrackAvailable(
        participant: RemoteParticipant,
        track: RemoteVideoTrack
    ) {

        Timber.v("onParticipantVideoTrackAvailable")
        with(binder.localVideoTrack) {
            this?.let {
                removeRenderer(vv_secondary)
                addRenderer(vv_primary)
                iv_btn_flip_cam.visibility = View.VISIBLE
            }
        }
        include_profile.visibility = View.GONE
        track.addRenderer(vv_secondary)
    }

    override fun onParticipantVideoTrackStateChange(
        participant: RemoteParticipant,
        enable: Boolean
    ) {
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
        val viewState =
            if (enable) {
                srcGreenBgWhite(fab_camera_handle, R.drawable.ic_video_black)
                View.VISIBLE
            } else {
                srcWhiteBgGreen(fab_camera_handle, R.drawable.ic_video_off)
                View.GONE
            }
        vv_primary.visibility = viewState
    }


    private fun handleCameraFlip(source: CameraCapturer.CameraSource) {
        @Suppress("ControlFlowWithEmptyBody")
        if (source == CameraCapturer.CameraSource.FRONT_CAMERA) {

        } else {

        }
    }
}
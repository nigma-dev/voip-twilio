package com.enigma.module_voip_ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.twilio.video.VideoTrack
import com.twilio.video.VideoView
import de.hdodenhof.circleimageview.CircleImageView

class ParticipantView(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_participant_view, this)
    }

    private val networkIv: ImageView by lazy { findViewById(R.id.iv_network_status) }

    private val micOffCiv: CircleImageView by lazy { findViewById(R.id.iv_mic_off) }

    private val profileCiv: CircleImageView by lazy { findViewById(R.id.iv_user_profile) }

    private val coverIv: ImageView by lazy { findViewById(R.id.iv_user_profile_cover) }

    private val nameTv: TextView by lazy { findViewById(R.id.tv_name) }

    private val twilioVv: VideoView by lazy { findViewById(R.id.twilio_view_view) }


    private fun setMicState(state: MicState) {
        micOffCiv.visibility = if (state == MicState.ON) GONE else VISIBLE
    }

    private fun setProfile(url: String?) {
        Glide
            .with(context)
            .load(url)
            .placeholder(R.drawable.ic_ph_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(profileCiv)
        Glide
            .with(context)
            .load(url)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(coverIv)
    }

    private fun setNetworkSate(state: NetworkState) {
        networkIv.setImageDrawable(ContextCompat.getDrawable(context, state.icon))
    }

    private fun setVideoState(state: VideoState) {
        /*other view visibility*/
        with(if (state == VideoState.PAUSE_VIDEO) VISIBLE else GONE) {
            profileCiv.visibility = this
            coverIv.visibility = this
            nameTv.visibility = this
        }

        twilioVv.visibility = if (state == VideoState.PAUSE_VIDEO) GONE else VISIBLE
    }

    fun setState(state: ParticipantViewState) {
        setProfile(state.imageUrl)
        setNetworkSate(state.networkState)
        setMicState(state.micState)
        setVideoState(state.videoState)
    }

    fun addRender(track: VideoTrack) {
        track.addRenderer(twilioVv)
    }
}

enum class VideoState {
    VIDEO,
    PAUSE_VIDEO
}

enum class MicState {
    ON,
    OFF
}

enum class NetworkState(@DrawableRes val icon: Int) {
    LEVEL_0(R.drawable.ic_signal),
    LEVEL_1(R.drawable.ic_signal),
    LEVEL_2(R.drawable.ic_signal),
    LEVEL_3(R.drawable.ic_signal),
    LEVEL_4(R.drawable.ic_signal),
    LEVEL_5(R.drawable.ic_signal),
    LEVEL_6(R.drawable.ic_signal)
}
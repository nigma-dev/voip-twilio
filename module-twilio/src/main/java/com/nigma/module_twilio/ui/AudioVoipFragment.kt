package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.base.BaseVoipFragment
import com.nigma.module_twilio.VoipService.ServiceBinder
import de.hdodenhof.circleimageview.CircleImageView

class AudioVoipFragment(binder: ServiceBinder) : BaseVoipFragment(binder) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflateUi(R.layout.fragment_voip_audio)
    }

    override fun btnAudio(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_audio_output_handle)
    }

    override fun btnMic(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_audio_mic)
    }

    override fun btnHungUp(): FloatingActionButton? {
        return view?.findViewById(R.id.fab_audio_end)
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
}
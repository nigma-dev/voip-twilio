package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R
import com.nigma.module_twilio.base.BaseVoipFragment
import com.nigma.module_twilio.service.VoipService.ServiceBinder
import kotlinx.android.synthetic.main.fragment_voip_audio.*
import timber.log.Timber

class AudioVoipFragment(binder: ServiceBinder) : BaseVoipFragment(binder) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("onCreateView")
        return inflateUi(R.layout.fragment_voip_audio)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("onViewCreated")
    }

    override fun btnAudio(): FloatingActionButton = fab_audio_output_handle

    override fun btnMic(): FloatingActionButton = fab_audio_mic

    override fun btnHungUp(): FloatingActionButton = fab_audio_end
}
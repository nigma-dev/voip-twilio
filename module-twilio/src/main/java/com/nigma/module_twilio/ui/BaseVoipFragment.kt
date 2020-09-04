package com.nigma.module_twilio.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseVoipFragment : Fragment() {

    private lateinit var voipActivity: VoipActivity

    @Throws(IllegalStateException::class)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is VoipActivity) throw  IllegalStateException("please attach form VoipActivity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}
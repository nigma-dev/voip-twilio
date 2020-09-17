package com.nigma.module_twilio.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nigma.module_twilio.ACTION_SERVICE_STOP
import com.nigma.module_twilio.VoipServiceBinder
import com.nigma.module_twilio.base.CallStyleActionActivity
import com.nigma.module_twilio.utils.KEY_IS_VOIP_VIDEO_TYPE
import com.nigma.module_twilio.utils.User
import com.nigma.module_twilio.utils.pushFragment
import com.nigma.module_twilio.utils.removeFragmentStacks

class VoipActivity : CallStyleActionActivity() {

    val user = MutableLiveData<User>()

    private var isVideoCall = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            isVideoCall = intent.getBooleanExtra(KEY_IS_VOIP_VIDEO_TYPE, false)
        }
        register()
    }

    override fun onDestroy() {
        unregister()
        super.onDestroy()
    }

    override fun onServiceBounced(binder: VoipServiceBinder) {
        with(if (isVideoCall) VideoVoipFragment(binder) else AudioVoipFragment(binder)) {
            binder.initCallback(this)
            user.value = binder.user
            pushFragment(this)
        }
    }

    fun clickHungUp() {
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    removeFragmentStacks()
                    finish()
                }, 200
            )
    }


    private fun register() {
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(receiver,
                IntentFilter().apply {
                    addAction(ACTION_SERVICE_STOP)
                }
            )
    }

    private fun unregister() {
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(receiver)
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                ACTION_SERVICE_STOP -> {
                    finish()
                }
            }
        }
    }
}


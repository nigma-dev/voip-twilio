package com.nigma.module_twilio.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
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


    class VoipBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

        }
    }
}


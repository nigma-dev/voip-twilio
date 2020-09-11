package com.nigma.module_twilio.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.nigma.module_twilio.base.CallStyleActionActivity
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.VoipService.Companion.KEY_USER_OBJECT
import com.nigma.module_twilio.utils.User
import com.nigma.module_twilio.utils.pushFragment
import com.nigma.module_twilio.utils.removeFragmentStacks
import timber.log.Timber

class VoipActivity : CallStyleActionActivity() {

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            user = intent.getSerializableExtra(KEY_USER_OBJECT) as User
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onServiceBounced(binder: VoipService.ServiceBinder) {
        val fragment =
            if (binder.isVideoCall) VideoVoipFragment(binder) else AudioVoipFragment(binder)
        binder.initCallback(fragment.participantCallback, fragment.voipMediaActionStateCallback)
        pushFragment(fragment)
    }

    fun clickHungUp() {
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    removeFragmentStacks()
                    finish()
                }, 200
            )
        Timber.i("clickHungUp")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}


package com.nigma.module_twilio.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.nigma.module_twilio.base.CallStyleActionActivity
import com.nigma.module_twilio.model.User
import com.nigma.module_twilio.service.VoipService
import com.nigma.module_twilio.service.VoipService.Companion.KEY_USER_OBJECT
import com.nigma.module_twilio.utils.pushFragment
import com.nigma.module_twilio.utils.removeFragmentStacks
import timber.log.Timber

class VoipActivity : CallStyleActionActivity() {

    val userLiveData = MutableLiveData<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val user = intent.getSerializableExtra(KEY_USER_OBJECT) as User
            userLiveData.value = user
        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    override fun onServiceBounced(binder: VoipService.ServiceBinder) {
        val fragment = if (binder.isVideoCall) VideoVoipFragment(binder) else AudioVoipFragment(binder)
        binder.initCallback(fragment.participantCallback, fragment.audioRoutingCallback)
        pushFragment(fragment)
    }

    fun clickHungUp() {
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    removeFragmentStacks()
                    finishAndRemoveTask()
                }, 200
            )
        Timber.i("clickHungUp")
    }

}


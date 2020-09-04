package com.nigma.module_twilio.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.nigma.module_twilio.service.VoipService
import timber.log.Timber

class VoipActivity : AppCompatActivity(), ServiceConnection {

    private val service by lazy { Intent(applicationContext, VoipService::class.java) }

    private var isBind = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isBind) bindService(service, this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(this)
        super.onDestroy()
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        p1 ?: return
        val binder = p1 as VoipService.ServiceBinder
        binder
            .getMediaStateLiveData()
            .observe(this, {

            })
        isBind = true
        Timber.i("onServiceConnected | component name $p0 | binder ${p1?.pingBinder()}")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        isBind = false
        Timber.i("onServiceDisconnected | component name $p0 ")
    }
}
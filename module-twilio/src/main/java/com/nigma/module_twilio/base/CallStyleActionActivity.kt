package com.nigma.module_twilio.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import com.nigma.module_twilio.VoipService
import timber.log.Timber

abstract class CallStyleActionActivity : GuardRelatedActivity(), ServiceConnection {

    private val content by lazy { findViewById<View>(android.R.id.content) }

    private val service by lazy { Intent(applicationContext, VoipService::class.java) }

    var isBind = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content.setOnClickListener { handleShowHide() }
        if (!isBind) bindService(service, this, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        handleShowHide()
    }

    override fun onDestroy() {
        unbindService(this)
        super.onDestroy()
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        p1 ?: return
        val binder = p1 as VoipService.ServiceBinder
        isBind = true
        onServiceBounced(binder)
        Timber.i("onServiceConnected")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        isBind = false
        Timber.i("onServiceDisconnected ")
    }

    abstract fun onServiceBounced(binder: VoipService.ServiceBinder)

    protected open fun onShowSystemUi() {
        showSysUi()
    }

    protected open fun onHideSystemUi() {
        hideSysUi()
    }

    private fun handleShowHide() {
        onShowSystemUi()
        Handler(Looper.getMainLooper()).postDelayed({
            onHideSystemUi()
        }, 1000)
    }

    @Suppress("DEPRECATION")
    private fun showSysUi() {
        window
            .decorView
            .systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    @Suppress("DEPRECATION")
    private fun hideSysUi() {
        window
            .decorView
            .systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }
}
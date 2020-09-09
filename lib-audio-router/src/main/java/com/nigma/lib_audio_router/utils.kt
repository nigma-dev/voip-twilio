package com.nigma.lib_audio_router

import android.os.Looper

fun checkIsOnMainThread() {
    check(!(Thread.currentThread() !== Looper.getMainLooper().thread)) { "Not on main thread!" }
}

fun getThreadInfo(): String? {
    return "@[name=" + Thread.currentThread().name + ", id=" + Thread.currentThread().id + "]"
}
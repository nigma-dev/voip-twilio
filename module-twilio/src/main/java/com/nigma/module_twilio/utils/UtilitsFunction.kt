package com.nigma.module_twilio.utils

import android.app.Service
import android.content.Context
import android.content.res.ColorStateList
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nigma.module_twilio.R


@Throws(IllegalArgumentException::class, NullPointerException::class)
fun checkNull(vararg any: Any?) {
    for (arg in any) {
        arg ?: throw IllegalArgumentException("")
    }
}

fun Service.toast(msg: String?) {
    msg?.let { info ->
        Toast.makeText(applicationContext, info, Toast.LENGTH_SHORT).show()
    }
}

fun srcWhiteBgGreen(fab: FloatingActionButton, @DrawableRes src: Int) {
    with(fab) {
        val drawable = ContextCompat.getDrawable(this.context, src)
        setImageDrawable(drawable)
        backgroundTintList = getColor(this.context, R.color.color_green)
        imageTintList = getColor(this.context, android.R.color.white)
    }
}

fun srcGreenBgWhite(fab: FloatingActionButton, @DrawableRes src: Int) {
    with(fab) {
        val drawable = ContextCompat.getDrawable(this.context, src)
        setImageDrawable(drawable)
        backgroundTintList = getColor(this.context, android.R.color.white)
        imageTintList = getColor(this.context, R.color.color_green)
    }
}

fun getColor(context: Context, @ColorRes color: Int): ColorStateList {
    return ColorStateList.valueOf(ContextCompat.getColor(context, color))
}
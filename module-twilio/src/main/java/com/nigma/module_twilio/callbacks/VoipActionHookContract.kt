package com.nigma.module_twilio.callbacks

import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

interface VoipActionHookContract {

    fun btnAudio(): FloatingActionButton?

    fun btnMic(): FloatingActionButton?

    fun btnHungUp(): FloatingActionButton?

    fun tvStatus(): TextView?

    fun tvUsername(): TextView?

    fun ivUserProfile(): CircleImageView?
}
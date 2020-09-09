package com.nigma.lib_audio_router.model

import androidx.annotation.DrawableRes
import com.nigma.lib_audio_router.R

enum class AudioDevice(@DrawableRes icon: Int) {
    SPEAKER(R.drawable.ic_speaker),
    EARPIECE(R.drawable.ic_earpiece),
    BLUETOOTH(R.drawable.ic_bluetooth),
    AUDIO_JACK(R.drawable.ic_headset)
}
package com.enigma.lib_call_media

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import timber.log.Timber

class ToneManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null


    fun playConnectingTone() {
        Timber.i("playConnectingTone")
        checkToneDuplication()
        createTone(context, R.raw.outgoing_tone)
            ?.let {
                with(it) {
                    isLooping = true
                    start()
                    Timber.i("playConnectingTone | inside")
                }
            }
    }

    fun playConnectedTone() {
        Timber.i("playConnectedTone")
        checkToneDuplication()
        createTone(context, R.raw.connected_tone)
            ?.let {
                with(it) {
                    isLooping = false
                    start()
                    Timber.i("playConnectedTone | inside")
                }
            }
    }

    fun playDisconnectedTone() {
        playConnectedTone()
    }

    fun playParticipantConnectedTone() {
        playConnectedTone()
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    private fun checkToneDuplication() {
        Timber.i("checkToneDuplication")
        mediaPlayer?.let {
            Timber.i("checkToneDuplication |inside $it")
            if (it.isPlaying) {
                Timber.i("checkToneDuplication |inside if")
                it.stop()
                it.release()
            }
            Timber.i("checkToneDuplication |inside end")
        }
    }

    private fun createTone(context: Context, @RawRes raw: Int): MediaPlayer? {
        val player = MediaPlayer.create(context, raw)
        mediaPlayer = player
        Timber.i("createTone $player | $mediaPlayer")
        return player
    }
}
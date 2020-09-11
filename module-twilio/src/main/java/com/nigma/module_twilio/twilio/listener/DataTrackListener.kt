package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManager
import com.twilio.video.RemoteDataTrack
import timber.log.Timber
import java.nio.ByteBuffer

class DataTrackListener(
    private val twilioManager: TwilioManager
) : RemoteDataTrack.Listener {

    override fun onMessage(remoteDataTrack: RemoteDataTrack, messageBuffer: ByteBuffer) {
        Timber.i("onMessage : $messageBuffer")
    }

    override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
        Timber.i("onMessage : $message")
    }
}
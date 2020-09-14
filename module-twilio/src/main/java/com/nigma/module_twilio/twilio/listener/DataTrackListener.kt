package com.nigma.module_twilio.twilio.listener

import com.nigma.module_twilio.twilio.TwilioManagerContract
import com.twilio.video.RemoteDataTrack
import timber.log.Timber
import java.nio.ByteBuffer

class DataTrackListener(
    private val contract: TwilioManagerContract
) : RemoteDataTrack.Listener {

    override fun onMessage(remoteDataTrack: RemoteDataTrack, messageBuffer: ByteBuffer) {
        Timber.i("onMessage : $messageBuffer")
    }

    override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {
        contract.onMessage(remoteDataTrack, message)
    }
}
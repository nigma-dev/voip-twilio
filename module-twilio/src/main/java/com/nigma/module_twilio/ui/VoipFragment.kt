package com.nigma.module_twilio.ui

import android.os.Bundle
import android.view.View
import com.nigma.module_twilio.VoipServiceBinder
import com.nigma.module_twilio.base.BaseVoipFragment
import com.nigma.module_twilio.utils.loadImage

abstract class VoipFragment(
    protected val binder: VoipServiceBinder
) : BaseVoipFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleMicrophoneBtn(binder.microphoneState)
        user.observe(viewLifecycleOwner, { user ->
            usernameTv?.text = user.name
            loadImage(profileCiv, user.imageUrl)
            with(binder.selectedDevice) {
                this?.let { device ->
                    onAudioRoutedDeviceChanged(device, binder.auxiliaryAvailable)
                }
            }
        })
    }

    override fun onDestroyView() {
        binder.releaseCallback()
        super.onDestroyView()
    }
}
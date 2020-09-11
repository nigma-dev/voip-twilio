package com.nigma.module_twilio.ui.auxiliary_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.module_twilio.R
import kotlinx.android.synthetic.main.sheet_audio_output_devices.*

class AudioOutputDevicesSheet(
    private val devices: List<AudioDevice>,
    callback: (device: AudioDevice) -> Unit
) : BottomSheetDialogFragment() {

    private val adapter = AudioOutputDevicesAdapter(callback, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater
            .inflate(
                R.layout.sheet_audio_output_devices,
                container,
                false
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.addList(devices)
        with(recycler_devices_list) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@AudioOutputDevicesSheet.adapter
        }
    }
}
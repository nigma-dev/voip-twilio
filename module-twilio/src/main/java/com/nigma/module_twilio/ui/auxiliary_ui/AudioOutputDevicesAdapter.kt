package com.nigma.module_twilio.ui.auxiliary_ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.nigma.lib_audio_router.model.AudioDevice
import com.nigma.module_twilio.R
import timber.log.Timber

class AudioOutputDevicesAdapter(
    private val callback: (device: AudioDevice) -> Unit,
    private val audioOutputDevicesSheet: AudioOutputDevicesSheet
) : RecyclerView.Adapter<AudioOutputDevicesAdapter.ViewHolder>() {

    private val audioDevices = mutableListOf<AudioDevice>()

    @MainThread
    fun addList(devices: List<AudioDevice>) {
        audioDevices.clear()
        audioDevices.addAll(devices)
        Timber.v("addList | ${devices.size}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.v("onCreateViewHolder | $viewType")
        val inflateView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_audio_device, parent, false)
        return ViewHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return audioDevices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("onBindViewHolder | $position")
        holder.bind(audioDevices[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(device: AudioDevice) {
            itemView.setOnClickListener {
                callback(device)
                audioOutputDevicesSheet.dismiss()
            }
            with(itemView) {
                findViewById<TextView>(R.id.tv_audio_name).text = device.deviceName
                findViewById<ImageView>(R.id.iv_audio_icon).setImageResource(device.icon)
            }
        }
    }
}
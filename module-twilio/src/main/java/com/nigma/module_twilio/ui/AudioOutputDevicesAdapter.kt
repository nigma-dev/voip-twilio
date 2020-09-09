package com.nigma.module_twilio.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.nigma.module_twilio.R
import com.nigma.module_twilio.model.AudioDeviceModel
import timber.log.Timber

class AudioOutputDevicesAdapter(
    private val callback: (device: AudioDeviceModel) -> Unit,
    private val audioOutputDevicesSheet: AudioOutputDevicesSheet
) : RecyclerView.Adapter<AudioOutputDevicesAdapter.ViewHolder>() {

    private val audioDevices = mutableListOf<AudioDeviceModel>()

    @MainThread
    fun addList(devices: List<AudioDeviceModel>) {
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

        fun bind(audioDevicesModel: AudioDeviceModel) {
            itemView.setOnClickListener {
                callback(audioDevicesModel)
                audioOutputDevicesSheet.dismiss()
            }
            with(itemView) {
                findViewById<TextView>(R.id.tv_audio_name).text = audioDevicesModel.name
                findViewById<ImageView>(R.id.iv_audio_icon).setImageResource(audioDevicesModel.icon)
            }
        }
    }
}
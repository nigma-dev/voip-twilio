package com.nigma.module_twilio.callbacks

import com.twilio.video.Room

interface RoomEvent : Room.Listener{

    override fun onRecordingStarted(room: Room) {}

    override fun onRecordingStopped(room: Room) {}
}
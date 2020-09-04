package com.nigma.module_twilio.segregated_interface

import com.twilio.video.Room

interface RoomEvent : Room.Listener{

    override fun onRecordingStarted(room: Room) {}

    override fun onRecordingStopped(room: Room) {}
}
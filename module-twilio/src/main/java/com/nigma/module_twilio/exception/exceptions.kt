package com.nigma.module_twilio.exception

class CameraPermissionException : VoipException()

class AudioPermissionException : VoipException()

class CreateVideoTrackFailedException : VoipException()

class CreateDataTrackFailedException : VoipException()

class CreateAudioTrackFailedException : VoipException()

abstract class VoipException : Exception()
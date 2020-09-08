package com.nigma.module_twilio.model

import java.io.Serializable

data class User(
    var name: String,
    var imageUrl: String
) : Serializable
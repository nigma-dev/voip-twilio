package com.nigma.module_twilio.utils


@Throws(IllegalArgumentException::class,NullPointerException::class)
fun checkNull(vararg any: Any?) {
    for (arg in any) {
        arg ?: throw IllegalArgumentException("")
    }
}
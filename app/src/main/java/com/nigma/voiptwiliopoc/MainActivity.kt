package com.nigma.voiptwiliopoc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nigma.module_twilio.utils.User
import com.nigma.module_twilio.VoipService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity(), Callback {

    private val service by lazy { Intent(this, VoipService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextUserName.setText(getDeviceName() ?: "user")
        buttonJoin.setOnClickListener {
            val identity = editTextUserName.text.toString()
            getAccessToken(
                JSONObject()
                    .apply {
                        put("identity", identity)
                    })
        }

        buttonEnd.setOnClickListener {
            with(service) {
                action = VoipService.ACTION_VOIP_DISCONNECT_ROOM
                putExtra(VoipService.KEY_ROOM_NAME, editTextRoomId.text.toString())
                startService(this)
            }
        }
    }

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    private fun getAccessToken(jsonObject: JSONObject) {
        val body: RequestBody = jsonObject.toString().toRequestBody(JSON)
        val request: Request = Request.Builder()
            .url("http://twilio-env.eba-mjb4e2p2.ap-southeast-1.elasticbeanstalk.com/token")
            .post(body)
            .build()
        OkHttpClient().newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        Timber.e(e, "${call.isCanceled()}")
        Handler(Looper.getMainLooper())
            .post {
                toast(e)
            }
    }

    override fun onResponse(call: Call, response: Response) {
        response
            .body
            ?.let {
                val jsonObject = JSONObject(it.string())
                val token = jsonObject.getString("token")
                val user = User(editTextUserName.text.toString(), "https://d.newsweek.com/en/full/1502638/tifa-ff7-remake-e3-2019.webp")
                Timber.i("user -> $user")
                with(service) {
                    action = VoipService.ACTION_VOIP_CONNECT_ROOM
                    putExtra(VoipService.KEY_ROOM_NAME, editTextRoomId.text.toString())
                    putExtra(VoipService.KEY_ACCESS_TOKEN, token)
                    putExtra(VoipService.KEY_IS_VOIP_VIDEO_TYPE, switch1.isChecked)
                    putExtra(VoipService.KEY_USER_OBJECT, user)
                    startService(this)
                }
            }
    }

    private fun toast(any: Any) {
        Toast.makeText(this, any.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) return ""
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}
package com.nigma.voiptwiliopoc

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nigma.module_twilio.VoipService
import com.nigma.module_twilio.ui.VoipActivity
import com.nigma.module_twilio.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity(), Callback {

    private val service by lazy { Intent(this, VoipService::class.java) }

    private val voipActivity by lazy { Intent(this, VoipActivity::class.java) }

    private val voipPermission = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    private val broadcastManager
        get() = LocalBroadcastManager
            .getInstance(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        broadcastManager.registerReceiver(
            receiver,
            with(IntentFilter()) {
                addAction("camera")
                addAction("audio")
                return@with this
            }
        )
        editTextUserName.setText(getDeviceName() ?: "user")
        buttonJoin.setOnClickListener {
            if (checkVoipPermission(switch1.isEnabled)) {
                val identity = editTextUserName.text.toString()
                getAccessToken(
                    JSONObject()
                        .apply {
                            put("identity", identity)
                        })
            }
        }

        buttonEnd.setOnClickListener {
            with(service) {
                action = ACTION_VOIP_DISCONNECT_ROOM
                putExtra(KEY_ROOM_NAME, editTextRoomId.text.toString())
                startService(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastManager.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Toast.makeText(applicationContext, "${p1?.action}", Toast.LENGTH_SHORT).show()
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
                val user = User(
                    editTextUserName.text.toString(),
                    "https://d.newsweek.com/en/full/1502638/tifa-ff7-remake-e3-2019.webp"
                )
                Timber.i("user -> $user")
                with(service) {
                    action = ACTION_VOIP_CONNECT_ROOM
                    putExtra(KEY_ROOM_NAME, editTextRoomId.text.toString())
                    putExtra(KEY_ACCESS_TOKEN, token)
                    putExtra(KEY_IS_VOIP_VIDEO_TYPE, switch1.isChecked)
                    putExtra(KEY_USER_OBJECT, user)
                    startService(this)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 212) {
            if (checkVoipPermission(switch1.isEnabled)) {
                val identity = editTextUserName.text.toString()
                getAccessToken(
                    JSONObject()
                        .apply {
                            put("identity", identity)
                        })
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

    private fun checkVoipPermission(isCameraRequire: Boolean): Boolean {
        val needsPermission = arrayListOf<String>()
        for (perms in voipPermission) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perms
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (perms == Manifest.permission.CAMERA && !isCameraRequire) continue
                needsPermission.add(perms)
            }
        }
        if (needsPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                needsPermission.toTypedArray(),
                212
            )
            return false
        }
        return true
    }


}
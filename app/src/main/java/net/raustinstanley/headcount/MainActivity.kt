package net.raustinstanley.headcount

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var socket: Socket
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            socket = IO.socket("${Constants.HOST}:${Constants.PORT}")
            socket.connect()
        } catch (e: URISyntaxException) {
            Log.d("Socket", "socket error")
        }

        prefs = getSharedPreferences(Constants.Prefs.SHARED_PREFS, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(this)

        when {
            !prefs.contains(Constants.Prefs.NAME) -> {
                val registerFragment = RegisterFragment()
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_main, registerFragment)
                        .commit()
            }
            else -> {
                val mainFragment = MainFragment()
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_main, mainFragment)
                        .commit()
            }
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        when (p1) {
            Constants.Prefs.TOKEN -> registerToken(p0!!.getString(p1, ""))
        }
    }

    private fun registerToken(token: String) {
        val json = JSONObject()
        json.put("name", prefs.getString(Constants.Prefs.NAME, ""))
        json.put("token", token)
        socket.emit(Constants.SocketEvents.TOKEN, json)
    }
}

package net.raustinstanley.headcount

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {
    lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            socket = IO.socket("${Constants.HOST}:${Constants.PORT}")
            socket.connect()
        } catch (e: URISyntaxException) {
            Log.d("Socket", "socket error")
        }

        val prefs = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE)

        when {
            !prefs.contains(Constants.PREFS_NAME) -> {
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
}

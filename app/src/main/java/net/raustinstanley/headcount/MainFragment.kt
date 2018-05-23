package net.raustinstanley.headcount

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.net.URISyntaxException

class MainFragment : Fragment() {
    private lateinit var socket: Socket
    private lateinit var txtHeadcount: TextView
    private lateinit var switch: Switch
    private lateinit var spnName: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            socket = IO.socket("${Constants.HOST}:${Constants.PORT}")
            socket.connect()
        } catch (e: URISyntaxException) {
            Log.d("Socket", "socket error")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_main, container, false)

        txtHeadcount = view.findViewById(R.id.text_headcount)
        switch = view.findViewById(R.id.switch_rsvp)

        initSocketListeners()
        initRsvpSwitch()
        initUiListeners()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        socket.emit(Constants.SocketEvents.GET_HEADCOUNT)
    }

    override fun onResume() {
        super.onResume()

        socket.emit(Constants.SocketEvents.GET_HEADCOUNT)
    }

    private fun initSocketListeners() {
        socket.on(Constants.SocketEvents.GET_HEADCOUNT, { data ->
            val count = data[0] as Int
            runOnUiThread {
                txtHeadcount.text = count.toString()
            }
        })

        socket.on(Constants.SocketEvents.RSVP, { _ ->
            socket.emit(Constants.SocketEvents.GET_HEADCOUNT)
        })

        socket.on(Constants.SocketEvents.UPDATE, { data ->
            val count = data[0] as Int
            runOnUiThread {
                txtHeadcount.text = count.toString()
            }
        })

        socket.on(Constants.SocketEvents.GET_USER, { data ->
            val json = data[0] as JSONObject
            runOnUiThread {
                switch.isChecked = json.getBoolean("rsvp")
            }
        })
    }

    private fun initRsvpSwitch() {
        val nameJson = JSONObject()
        nameJson.put("name", "Austin")
        socket.emit(Constants.SocketEvents.GET_USER, nameJson)
    }

    private fun initUiListeners() {
        switch.setOnCheckedChangeListener { _, isChecked ->
            val json = JSONObject()
            json.put("name", "Austin")
            json.put("rsvp", isChecked)
            socket.emit(Constants.SocketEvents.RSVP, json)
        }
    }
}
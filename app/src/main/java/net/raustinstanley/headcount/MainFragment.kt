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
import khronos.*
import khronos.Dates.today
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.net.URISyntaxException

class MainFragment : Fragment() {
    private lateinit var socket: Socket
    private lateinit var txtHeadcount: TextView
    private lateinit var txtDate: TextView
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
        initSocketListeners()
        initUI(view)
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

    private fun initUI(view: View) {
        txtHeadcount = view.findViewById(R.id.text_headcount)
        txtDate = view.findViewById(R.id.text_date)
        switch = view.findViewById(R.id.switch_rsvp)

        var thisSunday = Dates.today
        while (thisSunday.toString("EEE") != "Sun") {
            thisSunday += 1.days
        }

        txtDate.text = String.format(getString(R.string.date), thisSunday.toString("EEE, MMM d"))

        switch.setOnCheckedChangeListener { _, isChecked ->
            val json = JSONObject()
            json.put("name", "Austin")
            json.put("rsvp", isChecked)
            socket.emit(Constants.SocketEvents.RSVP, json)
        }

        val nameJson = JSONObject()
        nameJson.put("name", "Austin")
        socket.emit(Constants.SocketEvents.GET_USER, nameJson)
    }
}
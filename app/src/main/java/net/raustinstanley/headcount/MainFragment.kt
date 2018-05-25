package net.raustinstanley.headcount

import android.app.Fragment
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.github.nkzawa.socketio.client.Socket
import khronos.Dates.today
import khronos.days
import khronos.plus
import khronos.toString
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var txtHeadcount: TextView
    private lateinit var txtDate: TextView
    private lateinit var switch: Switch
    private lateinit var btnViewRsvp: Button
    private lateinit var socket: Socket
    private lateinit var activity: MainActivity
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        socket = activity.socket
        prefs = activity.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)
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
        btnViewRsvp = view.findViewById(R.id.button_view_rsvp)

        var thisSunday = today
        while (thisSunday.toString("EEE") != "Sun") {
            thisSunday += 1.days
        }

        txtDate.text = String.format(getString(R.string.date), thisSunday.toString("EEE, MMM d"))

        switch.setOnCheckedChangeListener { _, isChecked ->
            val json = JSONObject()
            json.put("name", prefs.getString(Constants.PREFS_NAME, ""))
            json.put("rsvp", isChecked)
            socket.emit(Constants.SocketEvents.RSVP, json)
        }

        val nameJson = JSONObject()
        nameJson.put("name", prefs.getString(Constants.PREFS_NAME, ""))
        socket.emit(Constants.SocketEvents.GET_USER, nameJson)

        btnViewRsvp.setOnClickListener {
            val rsvpFragment = RsvpFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_main, rsvpFragment)
                    .addToBackStack("rsvp")
                    .commit()

        }
    }

}
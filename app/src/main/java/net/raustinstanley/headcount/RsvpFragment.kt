package net.raustinstanley.headcount

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.nkzawa.socketio.client.Socket
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray

class RsvpFragment : Fragment() {
    private lateinit var activity: MainActivity
    private lateinit var socket: Socket
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RsvpAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        socket = activity.socket
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_rsvp, container, false)

        socket.on(Constants.SocketEvents.GET_COMING, { data ->
            viewAdapter = RsvpAdapter(data[0] as JSONArray)
            viewManager = LinearLayoutManager(activity)

            runOnUiThread {
                recyclerView = view.findViewById<RecyclerView>(R.id.recycler_list).apply {
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        socket.emit(Constants.SocketEvents.GET_COMING)
    }

    class RsvpAdapter(private val names: JSONArray) : RecyclerView.Adapter<RsvpAdapter.ViewHolder>() {
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val textView = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.text_view, parent, false) as TextView

            return ViewHolder(textView)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.textView?.text = names.getString(position)
        }

        override fun getItemCount() = names.length()
    }
}
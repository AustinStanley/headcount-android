package net.raustinstanley.headcount

import android.app.Fragment
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.github.nkzawa.socketio.client.Socket
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject

class RegisterFragment : Fragment() {
    private lateinit var socket: Socket
    private lateinit var activity: MainActivity
    private lateinit var button: Button
    private lateinit var editName: EditText
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as MainActivity
        socket = activity.socket
        prefs = activity.getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE)

        socket.on(Constants.SocketEvents.REGISTER, { _ ->
            val mainFragment = MainFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_main, mainFragment)
                    .commit()
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_register, container, false)

        editName = view.findViewById(R.id.edit_name)
        button = view.findViewById(R.id.button_submit)

        button.setOnClickListener {
            runOnUiThread {
                val name = editName.text.trim()

                when {
                    name.isBlank() -> toast("Enter your first name")
                    name.contains(' ') -> toast("First name only, please")
                    else ->  {
                        prefs.edit()
                                .putString(Constants.PREFS_NAME, name.toString())
                                .apply()

                        val json = JSONObject()
                        json.put("name", name.toString())
                        socket.emit(Constants.SocketEvents.REGISTER, json)
                    }
                }
            }
        }

        return view
    }
}
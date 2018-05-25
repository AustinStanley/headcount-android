package net.raustinstanley.headcount

import android.content.Context
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseTokenManager : FirebaseInstanceIdService() {
    private val tag = "Firebase"

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(tag, "Refreshed token: $refreshedToken")

        getSharedPreferences(Constants.Prefs.SHARED_PREFS, Context.MODE_PRIVATE).edit()
                .putString(Constants.Prefs.TOKEN, refreshedToken)
                .apply()
    }

}
package net.raustinstanley.headcount

class Constants {
    companion object {
        const val HOST = "http://192.168.1.8"
        const val PORT = 8000
    }

    class SocketEvents {
        companion object {
            const val RSVP = "rsvp"
            const val GET_NAMES = "getnames"
            const val GET_HEADCOUNT = "getheadcount"
            const val UPDATE = "update"
            const val GET_USER = "getuser"
            const val GET_COMING="getcoming"
            const val REGISTER = "register"
            const val TOKEN = "token"
        }
    }

    class Prefs {
        companion object {
            const val SHARED_PREFS = "prefs"
            const val NAME = "name"
            const val TOKEN = "token"
        }
    }
}
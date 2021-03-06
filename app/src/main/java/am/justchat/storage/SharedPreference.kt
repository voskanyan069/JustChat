package am.justchat.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    companion object {
        private var instance: SharedPreference? = null

        fun getInstance(context: Context): SharedPreference {
            if (instance == null) {
                instance = SharedPreference(context)
            }
            return instance!!
        }
    }

    val sharedPreferences: SharedPreferences = context
            .getSharedPreferences("storage", Context.MODE_PRIVATE)
    val editPreferences: SharedPreferences.Editor = sharedPreferences.edit()
}
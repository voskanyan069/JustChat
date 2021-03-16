package am.justchat.models

import com.google.gson.annotations.SerializedName

data class Status(
        @SerializedName("login")
        val login: String,

        @SerializedName("new_status")
        val newStatus: String
)

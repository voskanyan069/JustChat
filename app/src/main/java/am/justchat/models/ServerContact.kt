package am.justchat.models

import com.google.gson.annotations.SerializedName

data class ServerContact(
        @SerializedName("login")
        val login: String,

        @SerializedName("contact_login")
        val contactLogin: String
)

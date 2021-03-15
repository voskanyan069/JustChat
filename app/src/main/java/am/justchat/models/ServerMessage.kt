package am.justchat.models

import com.google.gson.annotations.SerializedName

data class ServerMessage(
        @SerializedName("login")
        val login: String,

        @SerializedName("chat_login")
        val chatLogin: String,

        @SerializedName("message_text")
        val messageText: String
)

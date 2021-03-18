package am.justchat.models

import com.google.gson.annotations.SerializedName

data class UpdateUser(
        @SerializedName(value = "login")
        val login: String,

        @SerializedName(value = "new_value")
        val newValue: String
)

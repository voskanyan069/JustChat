package am.justchat.models

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("login")
    var login: String,

    @SerializedName("username")
    var username: String,

    @SerializedName("password")
    var password: String
)
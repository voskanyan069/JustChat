package am.justchat.api.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface CallsService {
    @GET("get_calls/{login}")
    fun getUserCalls(@Path(value = "login") login: String): Call<JsonObject>
}
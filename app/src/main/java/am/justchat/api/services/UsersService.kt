package am.justchat.api.services

import am.justchat.api.models.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface UsersService {
    @POST("add_user")
    fun addUser(@Body user: User): Call<JsonObject>

    @GET("get_user/{login}")
    fun getUser(@Path(value = "login") login: String): Call<JsonObject>
}
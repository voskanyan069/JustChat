package am.justchat.api.services

import am.justchat.models.UpdateUser
import am.justchat.models.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface UsersService {
    @GET("/get_user/{login}")
    fun getUser(@Path(value = "login") login: String): Call<JsonObject>

    @POST("/add_user")
    fun addUser(@Body user: User): Call<JsonObject>

    @POST("/update_username")
    fun updateUsername(@Body user: UpdateUser): Call<JsonObject>

    @POST("/update_password")
    fun updatePassword(@Body user: UpdateUser): Call<JsonObject>
}
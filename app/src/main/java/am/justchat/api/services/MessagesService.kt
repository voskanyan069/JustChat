package am.justchat.api.services

import am.justchat.models.ServerMessage
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface MessagesService {
    @POST("/send_message")
    fun sendMessage(@Body message: ServerMessage): Call<JsonObject>

    @GET("/get_messages/{login}/{chat_login}")
    fun getMessages(@Path(value = "login") login: String, @Path(value = "chat_login") chatLogin: String, @Query("after") after: String): Call<JsonObject>
}
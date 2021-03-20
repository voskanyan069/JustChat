package am.justchat.api.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatsService {
    @GET("/get_chats/{login}")
    fun getUserChats(@Path(value = "login") login: String, @Query("q") query: String): Call<JsonObject>
}
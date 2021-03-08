package am.justchat.api.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StoriesService {
    @GET("/get_stories/{login}")
    fun getUserStories(@Path(value = "login") login: String): Call<JsonObject>
}
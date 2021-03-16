package am.justchat.api.services

import am.justchat.models.Status
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StatusService {
    @POST("/update_status")
    fun updateStatus(@Body status: Status): Call<JsonObject>

    @GET("/get_status/{login}")
    fun getStatus(@Path(value = "login") login: String): Call<JsonObject>
}
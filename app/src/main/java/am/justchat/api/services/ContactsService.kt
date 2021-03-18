package am.justchat.api.services

import am.justchat.models.ServerContact
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContactsService {
    @GET("/get_contacts/{login}")
    fun getContacts(@Path(value = "login") login: String): Call<JsonObject>

    @POST("/delete_contact")
    fun deleteContact(@Body contact: ServerContact): Call<JsonObject>
}
package am.justchat.api.services

import am.justchat.models.ServerContact
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ContactsService {
    @GET("/get_contacts/{login}")
    fun getContacts(@Path(value = "login") login: String, @Query(value = "q") query: String): Call<JsonObject>

    @POST("/add_contact")
    fun addContact(@Body contact: ServerContact): Call<JsonObject>

    @POST("/delete_contact")
    fun deleteContact(@Body contact: ServerContact): Call<JsonObject>
}
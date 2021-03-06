package am.justchat.api.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ContactsService {
    @GET("get_contacts/{login}")
    fun getUserContacts(@Path(value = "login") login: String): Call<JsonObject>
}
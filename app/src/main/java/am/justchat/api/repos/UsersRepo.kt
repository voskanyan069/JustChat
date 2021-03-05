package am.justchat.api.repos

import am.justchat.api.Config
import am.justchat.api.services.UsersService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class UsersRepo {
    var usersService: UsersService? = null

    companion object {
        private var instance: UsersRepo? = null

        fun getInstance(): UsersRepo? {
            if (instance == null) {
                instance = UsersRepo()
            }
            return instance
        }
    }

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Config.URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        usersService = retrofit.create(UsersService::class.java)
    }
}
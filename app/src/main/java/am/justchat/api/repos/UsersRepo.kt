package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.UsersService

class UsersRepo {
    var usersService: UsersService? = null

    companion object {
        private var instance: UsersRepo? = null

        fun getInstance(): UsersRepo {
            if (instance == null) {
                instance = UsersRepo()
            }
            return instance as UsersRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        usersService = retrofit.create(UsersService::class.java)
    }
}
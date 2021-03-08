package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.ChatsService

class ChatsRepo {
    var chatsService: ChatsService? = null

    companion object {
        private var instance: ChatsRepo? = null

        fun getInstance(): ChatsRepo {
            if (instance == null) {
                instance = ChatsRepo()
            }
            return instance as ChatsRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        chatsService = retrofit.create(ChatsService::class.java)
    }
}
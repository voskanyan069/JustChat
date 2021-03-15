package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.MessagesService

class MessagesRepo {
    var messagesService: MessagesService? = null

    companion object {
        private var instance: MessagesRepo? = null

        fun getInstance(): MessagesRepo {
            if (instance == null) {
                instance = MessagesRepo()
            }
            return instance as MessagesRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        messagesService = retrofit.create(MessagesService::class.java)
    }
}
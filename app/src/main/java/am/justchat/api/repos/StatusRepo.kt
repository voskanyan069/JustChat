package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.StatusService

class StatusRepo {
    var statusService: StatusService? = null

    companion object {
        private var instance: StatusRepo? = null

        fun getInstance(): StatusRepo {
            if (instance == null) {
                instance = StatusRepo()
            }
            return instance as StatusRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        statusService = retrofit.create(StatusService::class.java)
    }
}
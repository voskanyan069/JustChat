package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.CallsService

class CallsRepo {
    var callsService: CallsService? = null

    companion object {
        private var instance: CallsRepo? = null

        fun getInstance(): CallsRepo {
            if (instance == null) {
                instance = CallsRepo()
            }
            return instance as CallsRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        callsService = retrofit.create(CallsService::class.java)
    }
}
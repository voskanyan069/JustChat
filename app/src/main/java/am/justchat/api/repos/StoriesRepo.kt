package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.StoriesService

class StoriesRepo {
    var storiesService: StoriesService? = null

    companion object {
        private var instance: StoriesRepo? = null

        fun getInstance(): StoriesRepo {
            if (instance == null) {
                instance = StoriesRepo()
            }
            return instance as StoriesRepo
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        storiesService = retrofit.create(StoriesService::class.java)
    }
}
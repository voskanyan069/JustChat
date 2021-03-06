package am.justchat.api.repos

import am.justchat.api.RetrofitClient
import am.justchat.api.services.ContactsService

class ContactsRepo {
    var contactsService: ContactsService? = null

    companion object {
        private var instance: ContactsRepo? = null

        fun getInstance(): ContactsRepo? {
            if (instance == null) {
                instance = ContactsRepo()
            }
            return instance
        }
    }

    init {
        val retrofit = RetrofitClient.getClient()
        contactsService = retrofit.create(ContactsService::class.java)
    }
}
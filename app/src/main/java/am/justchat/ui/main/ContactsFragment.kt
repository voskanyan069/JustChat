package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.ContactsAdapter
import am.justchat.models.Contacts
import am.justchat.states.OnlineState
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsFragment : Fragment() {
    companion object {
        private lateinit var contactsList: RecyclerView

        fun contactDeleted(index: Int) {
            contactsList.adapter?.notifyItemRemoved(index)
        }
    }
    private lateinit var addContactButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)

        val contacts = arrayListOf(
                Contacts("Username_1", OnlineState.ONLINE, "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
                Contacts("Username_2", OnlineState.OFFLINE, "https://images.unsplash.com/photo-1614705755374-538591a6f8f4?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=700&q=80"),
                Contacts("Username_3", OnlineState.ONLINE, "https://images.unsplash.com/photo-1614676314170-3eb1d98d0a25?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
                Contacts("Username_4", OnlineState.OFFLINE, "https://images.unsplash.com/photo-1614631446449-e2c7a0cc1428?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80")
        )

        addContactButton = root.findViewById(R.id.add_contact_btn)
        contactsList = root.findViewById(R.id.contacts_list)
        contactsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        fillContactsList(contacts)

        return root
    }

    private fun fillContactsList(data: ArrayList<Contacts>) {
        contactsList.adapter = ContactsAdapter(data)
    }
}
package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.ContactsAdapter
import am.justchat.api.repos.ContactsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.authentication.SignUpActivity
import am.justchat.models.Contact
import am.justchat.states.OnlineState
import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactsFragment : Fragment() {
    private lateinit var addContactButton: ImageView
    private val contactsArrayList = arrayListOf<Contact>()

    companion object {
        private lateinit var contactsList: RecyclerView

        fun contactDeleted(index: Int) {
            contactsList.adapter?.notifyItemRemoved(index)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)

        getContactsList()

        addContactButton = root.findViewById(R.id.add_contact_btn)
        contactsList = root.findViewById(R.id.contacts_list)
        contactsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        return root
    }

    private fun getContactsList() {
        val contactsRepo = ContactsRepo.getInstance()
        contactsRepo.contactsService!!
                .getUserContacts(CurrentUser.login!!)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                    ) {
                        val jsonParser = JsonParser()
                        val contactsJsonStr = Gson().toJson(response.body())
                        val contactsJson: JsonObject = jsonParser
                                .parse(contactsJsonStr).asJsonObject
                        try {
                            val code: Int = contactsJson.get("code").asInt
                            if (code == 1) {
                                moveToSignUp()
                            }
                        } catch (e: Exception) {
                            val contacts: JsonArray = contactsJson.getAsJsonArray("contacts")
                            for (i in 0..contacts.size().minus(1)) {
                                val contactJson: JsonObject = contacts.get(i).asJsonObject
                                val cont = Contact(
                                        profileUsername = contactJson.get("username").asString,
                                        profileOnlineState = when (contactJson.get("status").asString) {
                                            "online" -> OnlineState.ONLINE
                                            else -> OnlineState.OFFLINE
                                        },
                                        profileImage = contactJson.get("profile_image").asString)
                                contactsArrayList.add(cont)
                            }
                            fillContactsList(contactsArrayList)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
                })
    }

    private fun fillContactsList(data: ArrayList<Contact>) {
        contactsList.adapter = ContactsAdapter(data)
    }

    private fun moveToSignUp() {
        val intent = Intent(activity, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
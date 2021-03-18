package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.adapters.ContactsAdapter
import am.justchat.api.repos.ContactsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Contact
import am.justchat.states.OnlineState
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactsFragment : Fragment() {
    private lateinit var addContactButton: ImageView
    private lateinit var contactsList: RecyclerView
    private val contactsArrayList = arrayListOf<Contact>()
    private val contactsRepo = ContactsRepo.getInstance()
    private var isFragmentActive = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)

        addContactButton = root.findViewById(R.id.add_contact_btn)
        contactsList = root.findViewById(R.id.contacts_list)
        contactsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        return root
    }

    private fun requestsTask(): Job = CoroutineScope(Dispatchers.Main).launch {
        while (isFragmentActive) {
            Log.d("mTag", "Sent contacts get request")
            getContactsList()
            delay(2000L)
        }
    }

    private fun getContactsList() {
        contactsArrayList.clear()
        contactsRepo.contactsService!!
                .getContacts(CurrentUser.login!!)
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
                                        profileLogin = contactJson.get("login").asString,
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

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("mTag", "Fetch error", t)
                    }
                })
    }

    private fun fillContactsList(data: ArrayList<Contact>) {
        contactsList.adapter = ContactsAdapter(data)
    }

    private fun moveToSignUp() {
        val intent = Intent(activity, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        isFragmentActive = false
    }

    override fun onResume() {
        super.onResume()
        isFragmentActive = true
        requestsTask()
    }
}
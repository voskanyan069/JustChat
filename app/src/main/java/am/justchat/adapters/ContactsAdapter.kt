package am.justchat.adapters

import am.justchat.R
import am.justchat.activities.MessengerActivity
import am.justchat.api.repos.ContactsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.holders.ContactsViewHolder
import am.justchat.models.Contact
import am.justchat.models.ServerContact
import am.justchat.states.OnlineState
import am.justchat.ui.main.ContactsFragment
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactsAdapter(private val dataSet: ArrayList<Contact>) : RecyclerView.Adapter<ContactsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_list_item, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind()

        val item = dataSet[position]
        val onlineState = when(item.profileOnlineState) {
            OnlineState.ONLINE -> "Online"
            OnlineState.OFFLINE -> "Offline"
        }
        val containerColor = when(item.profileOnlineState) {
            OnlineState.ONLINE -> R.drawable.user_avatar_green_layout
            OnlineState.OFFLINE -> R.drawable.user_avatar_grey_layout
        }
        holder.profileImageContainer.background = ContextCompat.getDrawable(holder.profileImageContainer.context, containerColor)
        holder.profileUsername.text = item.profileUsername
        holder.profileOnlineState.text = onlineState
        Picasso.get().load(item.profileImage).fit().centerCrop().into(holder.profileImage)

        holder.callContactButton.setOnClickListener {
        }

        holder.chatContactButton.setOnClickListener {
            val intent = Intent(holder.profileImageContainer.context, MessengerActivity::class.java)
            intent.putExtra("login", item.profileLogin)
            intent.putExtra("username", item.profileUsername)
            intent.putExtra("profile_image", item.profileImage)
            holder.profileImageContainer.context.startActivity(intent)
        }

        holder.deleteContactButton.setOnClickListener {
            val contactsRepo = ContactsRepo.getInstance()
            contactsRepo.contactsService!!
                    .deleteContact(ServerContact(
                            login = CurrentUser.login.toString(),
                            contactLogin = item.profileLogin
                    )).enqueue(object : Callback<JsonObject> {
                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                            val jsonParser = JsonParser()
                            val contactJsonStr = Gson().toJson(response.body())
                            val contactJson = jsonParser.parse(contactJsonStr).asJsonObject
                            val isDeleted = contactJson.get("deleted").asBoolean
                            Snackbar.make(
                                    holder.profileImageContainer,
                                    if (isDeleted) { "Contact was successfully deleted" }
                                    else { "Some was wrong while deleting contact, please try again later" },
                                    Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("mTag", "Fetch error", t)
                        }
                    })
        }
    }

    override fun getItemCount(): Int = dataSet.size
}
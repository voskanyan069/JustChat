package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.ContactsViewHolder
import am.justchat.models.Contact
import am.justchat.states.OnlineState
import am.justchat.ui.main.ContactsFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

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

        holder.profileUsername.text = item.profileUsername
        holder.profileOnlineState.text = onlineState
        Picasso.get().load(item.profileImage).fit().centerCrop().into(holder.profileImage)

        holder.callContactButton.setOnClickListener {
            println("CALL BUTTON WAS PRESSED")
        }

        holder.deleteContactButton.setOnClickListener {
            dataSet.removeAt(position)
            ContactsFragment.contactDeleted(position)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}
package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var mainContainer: RelativeLayout
    lateinit var profileImage: CircleImageView
    lateinit var profileUsername: TextView
    lateinit var profileOnlineState: TextView
    lateinit var callContactButton: ImageView
    lateinit var deleteContactButton: ImageView

    fun bind() {
        mainContainer = itemView.findViewById(R.id.contacts_item_container)
        profileImage = itemView.findViewById(R.id.contacts_item_profile_image)
        profileUsername = itemView.findViewById(R.id.contacts_item_username)
        profileOnlineState = itemView.findViewById(R.id.contacts_item_online_state)
        callContactButton = itemView.findViewById(R.id.contacts_item_call_contact)
        deleteContactButton = itemView.findViewById(R.id.contacts_item_delete_contact)
    }
}
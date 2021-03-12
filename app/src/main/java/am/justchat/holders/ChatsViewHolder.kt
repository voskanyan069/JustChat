package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var profileImageContainer: FrameLayout
    lateinit var profileImage: CircleImageView
    lateinit var profileUsername: TextView
    lateinit var lastMessage: TextView

    fun bind() {
        profileImageContainer = itemView.findViewById(R.id.chat_item_profile_image_container)
        profileImage = itemView.findViewById(R.id.chat_item_profile_image)
        profileUsername = itemView.findViewById(R.id.chat_item_username)
        lastMessage = itemView.findViewById(R.id.chat_item_last_message)
    }
}
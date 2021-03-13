package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var profileImage: CircleImageView
    lateinit var profileUsername: TextView
    lateinit var messageText: TextView
    lateinit var messageTime: TextView

    fun bind() {
        profileImage = itemView.findViewById(R.id.rec_message_profile_image)
        profileUsername = itemView.findViewById(R.id.rec_message_sender)
        messageText = itemView.findViewById(R.id.rec_message_text)
        messageTime = itemView.findViewById(R.id.rec_message_time)
    }
}
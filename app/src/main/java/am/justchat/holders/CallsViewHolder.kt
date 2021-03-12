package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var profileImage: CircleImageView
    lateinit var profileUsername: TextView
    lateinit var callMessage: TextView
    lateinit var callStateImage: ImageView

    fun bind() {
        profileImage = itemView.findViewById(R.id.call_item_profile_image)
        profileUsername = itemView.findViewById(R.id.call_item_username)
        callMessage = itemView.findViewById(R.id.call_item_last_call)
        callStateImage = itemView.findViewById(R.id.call_item_state)
    }
}
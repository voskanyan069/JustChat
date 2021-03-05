package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class CallsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var profileUsername: TextView
    lateinit var profileImage: CircleImageView
    lateinit var callMessage: TextView
    lateinit var callStateImage: ImageView

    fun bind() {
        profileUsername = itemView.findViewById(R.id.call_item_username)
        profileImage = itemView.findViewById(R.id.call_item_profile_image)
        callMessage = itemView.findViewById(R.id.call_item_last_call)
        callStateImage = itemView.findViewById(R.id.call_item_state)
    }
}
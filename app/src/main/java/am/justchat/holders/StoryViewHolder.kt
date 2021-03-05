package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var profileImage: CircleImageView
    lateinit var profileUsername: TextView

    fun bind() {
        profileImage = itemView.findViewById(R.id.story_item_profile_image)
        profileUsername = itemView.findViewById(R.id.story_item_profile_username)
    }
}
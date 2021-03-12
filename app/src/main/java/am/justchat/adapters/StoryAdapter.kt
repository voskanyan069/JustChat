package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.StoryViewHolder
import am.justchat.models.Story
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class StoryAdapter(private val dataSet: List<Story>) : RecyclerView.Adapter<StoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_list_item, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind()

        val item = dataSet[position]
        val containerColor = when (item.mediaPath.size) {
            0 -> R.drawable.user_avatar_grey_layout
            else -> R.drawable.user_avatar_green_layout
        }
        holder.profileImageContainer.background = ContextCompat.getDrawable(holder.profileImageContainer.context, containerColor)
        holder.profileUsername.text = item.profileUsername
        Picasso.get().load(item.profileImage).fit().centerCrop().into(holder.profileImage)
    }

    override fun getItemCount(): Int = dataSet.size
}
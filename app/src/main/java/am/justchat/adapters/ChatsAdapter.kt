package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.ChatsViewHolder
import am.justchat.models.Chat
import am.justchat.states.OnlineState
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ChatsAdapter(private val dataSet: List<Chat>) : RecyclerView.Adapter<ChatsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind()

        val item = dataSet[position]

        val containerColor = when (item.isOnline) {
            OnlineState.ONLINE -> R.drawable.user_avatar_green_layout
            else -> R.drawable.user_avatar_grey_layout
        }
        holder.profileImageContainer.background = ContextCompat.getDrawable(holder.profileImageContainer.context, containerColor)
        holder.profileUsername.text = item.profileUsername
        holder.lastMessage.text = item.lastMessage
        Picasso.get().load(item.profileImage).fit().centerCrop().into(holder.profileImage)
    }

    override fun getItemCount(): Int = dataSet.size
}
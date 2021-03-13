package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var messageText: TextView
    lateinit var messageTime: TextView

    fun bind() {
        messageText = itemView.findViewById(R.id.sent_message_text)
        messageTime = itemView.findViewById(R.id.sent_message_time)
    }
}
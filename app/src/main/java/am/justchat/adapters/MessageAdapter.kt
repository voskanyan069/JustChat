package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.ReceivedMessageViewHolder
import am.justchat.holders.SentMessageViewHolder
import am.justchat.models.Message
import am.justchat.states.MessageSenderState
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.lang.ClassCastException
import java.text.SimpleDateFormat

class MessageAdapter(private val dataSet: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                when (viewType) {
                    1 -> R.layout.sent_message_list_item
                    else -> R.layout.received_message_list_item
                },
                parent, false
            )

        return when (viewType) {
            1 -> SentMessageViewHolder(view)
            else -> ReceivedMessageViewHolder(view)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val dateTime: String = simpleDateFormat.format(item.time * 1000L)
        try {
            if (item.viewType == MessageSenderState.SENT) {
                (holder as SentMessageViewHolder).bind()
                holder.messageText.text = item.message
                holder.messageTime.text = dateTime
            } else {
                (holder as ReceivedMessageViewHolder).bind()
                val sender = item.sender
                Picasso.get().load(sender.profileImage).into(holder.profileImage)
                holder.profileUsername.text = sender.username
                holder.messageText.text = item.message
                holder.messageTime.text = dateTime
            }
        } catch (e: ClassCastException) {
            Log.e("mTag", "Cast error", e)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int = when (dataSet[position].viewType) {
        MessageSenderState.SENT -> 1
        MessageSenderState.RECEIVED -> 2
    }
}
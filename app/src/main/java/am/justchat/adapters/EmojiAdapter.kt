package am.justchat.adapters

import am.justchat.R
import am.justchat.activities.PhotoEditorActivity
import am.justchat.holders.EmojiViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(private val dataSet: List<String>) : RecyclerView.Adapter<EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emoji_list_item, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind()

        val item = dataSet[position]
        holder.emojiText.text = item

        holder.emojiText.setOnClickListener {
            PhotoEditorActivity.addEmoji(item)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}
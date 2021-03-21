package am.justchat.holders

import am.justchat.R
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var emojiText: TextView

    fun bind() {
        emojiText = itemView.findViewById(R.id.emoji_text)
        emojiText.typeface = Typeface.createFromAsset(itemView.context.assets, "emojione-android.ttf")
    }
}
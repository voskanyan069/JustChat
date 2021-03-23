package am.justchat.holders

import am.justchat.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var filterTitle: TextView
    lateinit var filterImage: ImageView

    fun bind() {
        filterTitle = itemView.findViewById(R.id.filter_name)
        filterImage = itemView.findViewById(R.id.filter_image)
    }
}
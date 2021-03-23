package am.justchat.adapters

import am.justchat.R
import am.justchat.holders.FilterViewHolder
import am.justchat.listeners.FilterAdapterListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.utils.ThumbnailItem

class FilterAdapter(private val dataSet: List<ThumbnailItem>,
                    private val listener: FilterAdapterListener): RecyclerView.Adapter<FilterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.editor_filter_list_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind()

        val item: ThumbnailItem = dataSet[position]

        holder.filterTitle.text = item.filterName
        holder.filterImage.setImageBitmap(item.image)

        holder.filterImage.setOnClickListener {
            listener.onFilterSelected(item.filter)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}
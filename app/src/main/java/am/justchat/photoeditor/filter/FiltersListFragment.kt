package am.justchat.photoeditor.filter

import am.justchat.R
import am.justchat.activities.MainActivity
import am.justchat.adapters.FilterAdapter
import am.justchat.listeners.FilterAdapterListener
import am.justchat.utils.BitmapUtils
import am.justchat.utils.SpacesItemDecoration
import android.R.attr
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FiltersListFragment : Fragment(), FilterAdapterListener {
    private lateinit var filtersList: RecyclerView
    private lateinit var adapter: FilterAdapter
    private var listener: FilterAdapterListener? = null
    private val filtersArrayList: ArrayList<ThumbnailItem> = arrayListOf()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filters_list, container, false)

        adapter = FilterAdapter(filtersArrayList, this)
        filtersList = root.findViewById(R.id.editor_filters_list)
        filtersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filtersList.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
                resources.displayMetrics).toInt()
        filtersList.addItemDecoration(SpacesItemDecoration(space))
        filtersList.adapter = adapter

        return root
    }

    fun setListener(listener: FilterAdapterListener) {
        this.listener = listener
    }

    fun prepareFilter(thumbImage: Bitmap): Job = CoroutineScope(Dispatchers.Main).launch {
        ThumbnailsManager.clearThumbs()
        filtersArrayList.clear()

        val thumbnailItem = ThumbnailItem()
        thumbnailItem.image = thumbImage
        thumbnailItem.filterName = "Normal"
        ThumbnailsManager.addThumb(thumbnailItem)

        val filters = FilterPack.getFilterPack(activity)

        for (filter in filters) {
            val tI = ThumbnailItem()
            tI.image = thumbImage
            tI.filter = filter
            tI.filterName = filter.name
            ThumbnailsManager.addThumb(tI)
        }

        filtersArrayList.addAll(ThumbnailsManager.processThumbs(activity))
        adapter.notifyDataSetChanged()
    }

    override fun onFilterSelected(filter: Filter) {
        listener?.onFilterSelected(filter)
    }
}
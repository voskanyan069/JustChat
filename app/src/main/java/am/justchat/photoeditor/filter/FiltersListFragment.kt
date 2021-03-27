package am.justchat.photoeditor.filter

import am.justchat.R
import am.justchat.adapters.FilterAdapter
import am.justchat.listeners.FilterAdapterListener
import am.justchat.photoeditor.EditorSettings
import am.justchat.utils.SpacesItemDecoration
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FiltersListFragment : Fragment(), FilterAdapterListener {
    private lateinit var filtersList: RecyclerView
    private lateinit var filtersAdapterListener: FilterAdapterListener
    private val filterThumbsArrayList: ArrayList<ThumbnailItem> = arrayListOf()
    private val processedThumbsArrayList: ArrayList<ThumbnailItem> = arrayListOf()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_filters_list, container, false)

        if (EditorSettings.filterAdapter == null) {
            EditorSettings.filterAdapter = FilterAdapter(processedThumbsArrayList, this)
        }
        filtersList = root.findViewById(R.id.editor_filters_list)
        filtersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filtersList.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
                resources.displayMetrics).toInt()
        filtersList.addItemDecoration(SpacesItemDecoration(space))
        filtersList.adapter = EditorSettings.filterAdapter

        return root
    }

    fun setListener(listener: FilterAdapterListener) {
        filtersAdapterListener = listener
    }

    fun prepareFilter(thumbImage: Bitmap): Job = CoroutineScope(Dispatchers.Main).launch {
        filterThumbsArrayList.clear()
        processedThumbsArrayList.clear()

        val thumbnailItem = ThumbnailItem()
        thumbnailItem.image = thumbImage
        thumbnailItem.filterName = "Normal"
        filterThumbsArrayList.add(thumbnailItem)

        if (EditorSettings.filters == null) {
            EditorSettings.filters = FilterPack.getFilterPack(context)
        }

        for (filter in EditorSettings.filters!!) {
            val tI = ThumbnailItem()
            tI.image = thumbImage
            tI.filter = filter
            tI.filterName = filter.name
            filterThumbsArrayList.add(tI)
        }

        processFiltersImage()
        EditorSettings.filterAdapter?.notifyDataSetChanged()
    }

    private fun processFiltersImage() {
        for (thumb in filterThumbsArrayList) {
            // scaling down the image
            val size = 80
            thumb.image = Bitmap.createScaledBitmap(thumb.image, size, size, false)
            thumb.image = thumb.filter.processFilter(thumb.image)
            processedThumbsArrayList.add(thumb)
        }
    }

    override fun onFilterSelected(filter: Filter) {
        filtersAdapterListener.onFilterSelected(filter)
    }
}
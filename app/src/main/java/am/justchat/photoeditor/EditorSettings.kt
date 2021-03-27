package am.justchat.photoeditor

import am.justchat.adapters.FilterAdapter
import am.justchat.listeners.FilterAdapterListener
import android.graphics.Bitmap
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem

object EditorSettings {
//    Brush
    var brushSize: Int = 10
    var brushColorR: Int = 0
    var brushColorG: Int = 0
    var brushColorB: Int = 0
    var brushColor: Int = Color.rgb(0, 0, 0)

//    Eraser
    var eraserSize: Int = 10

//    Text
    var textColorR: Int = 0
    var textColorG: Int = 0
    var textColorB: Int = 0
    var textColor: Int = Color.rgb(0, 0, 0)

//    Image
    lateinit var originalImage: Bitmap
    fun isOriginalImageInitialized(): Boolean = EditorSettings::originalImage.isInitialized

//    Filters
    var filters: List<Filter>? = null
    var filterAdapter: FilterAdapter? = null
}
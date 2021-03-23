package am.justchat.listeners

import com.zomato.photofilters.imageprocessors.Filter

interface FilterAdapterListener {
    fun onFilterSelected(filter: Filter)
}
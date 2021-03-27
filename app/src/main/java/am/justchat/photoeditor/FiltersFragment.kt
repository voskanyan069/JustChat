package am.justchat.photoeditor

import am.justchat.R
import am.justchat.adapters.FilterViewPagerAdapter
import am.justchat.listeners.EditImageFragmentListener
import am.justchat.listeners.FilterAdapterListener
import am.justchat.photoeditor.filter.EditImageFragment
import am.justchat.photoeditor.filter.FiltersListFragment
import am.justchat.states.SwitchFragment
import am.justchat.utils.BitmapUtils
import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import com.zomato.photofilters.utils.ThumbnailsManager


class FiltersFragment : Fragment(), FilterAdapterListener, EditImageFragmentListener {
    private lateinit var filterCancel: ImageView
    private lateinit var filterSave: ImageView
    private lateinit var filterImagePreview: ImageView
    private lateinit var filterViewPager: ViewPager
    private lateinit var filterTabs: TabLayout

    private lateinit var originalImage: Bitmap
    private lateinit var filteredImage: Bitmap
    private lateinit var finalImage: Bitmap

    private lateinit var filtersListFragment: FiltersListFragment
    private lateinit var editImageFragment: EditImageFragment

    private var brightnessFinal = 0
    private var saturationFinal = 1.0f
    private var contrastFinal = 1.0f

    init {
        System.loadLibrary("NativeImageProcessor")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_filters, container, false)

        filterCancel = root.findViewById(R.id.filter_cancel)
        filterSave = root.findViewById(R.id.filter_save)
        filterImagePreview = root.findViewById(R.id.filter_image_preview)
        filterViewPager = root.findViewById(R.id.filter_viewpager)
        filterTabs = root.findViewById(R.id.filter_tabs)

        loadImage()
        setupViewPager()
        loadFiltersThumbnails()
        filterSaveCancel()
        filterTabs.setupWithViewPager(filterViewPager)

        return root
    }

    private fun filterSaveCancel() {
        filterCancel.setOnClickListener {
            ThumbnailsManager.clearThumbs()
            SwitchFragment.switch(activity!! as AppCompatActivity, EditorFragment(), R.id.editor_fragment_container)
        }
        filterSave.setOnClickListener {
            EditorSettings.originalImage = finalImage
            ThumbnailsManager.clearThumbs()
            SwitchFragment.switch(activity!! as AppCompatActivity, EditorFragment(), R.id.editor_fragment_container)
        }
    }

    private fun loadImage() {
        originalImage = EditorSettings.originalImage
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        filterImagePreview.setImageBitmap(originalImage)
    }

    private fun loadFiltersThumbnails() {
        filtersListFragment.prepareFilter(originalImage)
    }

    private fun setupViewPager() {
        val adapter = FilterViewPagerAdapter(activity!!.supportFragmentManager)

        // adding filter list fragment
        filtersListFragment = FiltersListFragment()
        filtersListFragment.setListener(this)

        // adding edit image fragment
        val editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)

        adapter.addFragment(filtersListFragment, "Filters")
        adapter.addFragment(editImageFragment, "Edit")

        filterViewPager.adapter = adapter
    }

    override fun onFilterSelected(filter: Filter) {
        // reset image controls
//        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        filterImagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        filterImagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        filterImagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onContrastChanged(contrast: Float) {
        contrastFinal = contrast
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrast))
        filterImagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onEditStarted() {}

    override fun onEditCompleted() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        myFilter.addSubFilter(ContrastSubFilter(contrastFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        finalImage = myFilter.processFilter(bitmap)
    }

    private fun resetControls() {
        if (!this::editImageFragment.isInitialized) {
            editImageFragment = EditImageFragment()
        }
        editImageFragment.resetControllers()
        brightnessFinal = 0
        saturationFinal = 1.0f
        contrastFinal = 1.0f
    }
}
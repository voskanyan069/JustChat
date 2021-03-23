package am.justchat.photoeditor.filter

import am.justchat.R
import am.justchat.listeners.EditImageFragmentListener
import am.justchat.views.EditorFilterSeekbar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment

class EditImageFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    private lateinit var editorFilterBrightness: EditorFilterSeekbar
    private lateinit var editorFilterContrast: EditorFilterSeekbar
    private lateinit var editorFilterSaturation: EditorFilterSeekbar
    private var listener: EditImageFragmentListener? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_image, container, false)

        editorFilterBrightness = root.findViewById(R.id.editor_filter_brightness)
        editorFilterContrast = root.findViewById(R.id.editor_filter_contrast)
        editorFilterSaturation = root.findViewById(R.id.editor_filter_saturation)

        initControllers()

        return root
    }

    fun setListener(listener: EditImageFragmentListener) {
        this.listener = listener
    }

    fun resetControllers() {
        editorFilterBrightness.filterSeekbar.progress = 100
        editorFilterContrast.filterSeekbar.progress = 0
        editorFilterSaturation.filterSeekbar.progress = 10
    }

    private fun initControllers() {
        editorFilterBrightness.filterSeekbar.max = 200
        editorFilterBrightness.filterSeekbar.progress = 100

        editorFilterContrast.filterSeekbar.max = 20
        editorFilterContrast.filterSeekbar.progress = 0

        editorFilterSaturation.filterSeekbar.max = 30
        editorFilterSaturation.filterSeekbar.progress = 10

        editorFilterBrightness.filterSeekbar.setOnSeekBarChangeListener(this)
        editorFilterContrast.filterSeekbar.setOnSeekBarChangeListener(this)
        editorFilterSaturation.filterSeekbar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.editor_filter_brightness -> listener?.onBrightnessChanged(progress - 100)
            R.id.editor_filter_contrast -> listener?.onContrastChanged(.10f * progress.plus(10))
            R.id.editor_filter_saturation -> listener?.onSaturationChanged(.10f * progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        listener?.onEditStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        listener?.onEditCompleted()
    }
}
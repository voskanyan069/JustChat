package am.justchat.photoeditor

import am.justchat.R
import am.justchat.activities.PhotoEditorActivity
import am.justchat.views.EditorColorPicker
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor

class BrushBottomSheet : BottomSheetDialogFragment() {
    private lateinit var brushSize: SeekBar
    private lateinit var brushColorPicker: EditorColorPicker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.editor_brush_dialog,
            container, false
        )

        brushSize = root.findViewById(R.id.brush_size_bar)
        brushColorPicker = root.findViewById(R.id.brush_color_picker)

        brushSize.progress = EditorSettings.brushSize
        brushColorPicker.colorPickerR.progress = EditorSettings.brushColorR
        brushColorPicker.colorPickerG.progress = EditorSettings.brushColorG
        brushColorPicker.colorPickerB.progress = EditorSettings.brushColorB
        brushColorPicker.colorPreview.setBackgroundColor(EditorSettings.brushColor)
        brushColorPicker.setColorsListeners(1)

        setBrushSize()
        EditorFragment.updateBrushSize()

        return root
    }

    private fun setBrushSize() {
        brushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.brushSize = progress
                EditorFragment.updateBrushSize()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
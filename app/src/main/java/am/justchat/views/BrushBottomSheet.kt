package am.justchat.views

import am.justchat.R
import am.justchat.photoeditor.EditorSettings
import am.justchat.photoeditor.PhotoEditorActivity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BrushBottomSheet : BottomSheetDialogFragment() {
    private lateinit var brushSize: SeekBar
    private lateinit var brushColorR: SeekBar
    private lateinit var brushColorG: SeekBar
    private lateinit var brushColorB: SeekBar
    private lateinit var brushColorPreview: Button

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
        brushColorR = root.findViewById(R.id.brush_color_r)
        brushColorG = root.findViewById(R.id.brush_color_g)
        brushColorB = root.findViewById(R.id.brush_color_b)
        brushColorPreview = root.findViewById(R.id.brush_color_preview)
        brushColorPreview.setBackgroundColor(Color.BLACK)

        brushSize.progress = EditorSettings.brushSize
        brushColorR.progress = EditorSettings.brushColorR
        brushColorG.progress = EditorSettings.brushColorG
        brushColorB.progress = EditorSettings.brushColorB

        setBrushSize()
        setBrushColor()
        updateBrush()

        return root
    }

    private fun setBrushSize() {
        brushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.brushSize = progress
                updateBrush()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setBrushColor() {
        brushColorR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.brushColorR = progress
                updateBrush()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        brushColorG.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.brushColorG = progress
                updateBrush()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        brushColorB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.brushColorB = progress
                updateBrush()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateBrush() {
        val brushColor = Color.rgb(
                EditorSettings.brushColorR,
                EditorSettings.brushColorG,
                EditorSettings.brushColorB
        )
        brushColorPreview.setBackgroundColor(brushColor)
        EditorSettings.brushColor = brushColor
        PhotoEditorActivity.updateBrush()
    }
}
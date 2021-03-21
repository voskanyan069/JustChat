package am.justchat.views

import am.justchat.R
import am.justchat.photoeditor.EditorSettings
import am.justchat.activities.PhotoEditorActivity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar

class EditorColorPicker(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    val colorPickerR: SeekBar
    val colorPickerG: SeekBar
    val colorPickerB: SeekBar
    val colorPreview: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.editor_color_picker, this, true)
        attrs.let {
            colorPickerR = findViewById(R.id.editor_color_r)
            colorPickerG = findViewById(R.id.editor_color_g)
            colorPickerB = findViewById(R.id.editor_color_b)
            colorPreview = findViewById(R.id.editor_color_preview)
            colorPreview.setBackgroundColor(Color.rgb(
                    colorPickerR.progress,
                    colorPickerG.progress,
                    colorPickerB.progress
            ))
        }
    }

    fun setColorsListeners(tool: Int) {
        colorPickerR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (tool) {
                    1 -> EditorSettings.brushColorR = progress
                    2 -> EditorSettings.textColorR = progress
                }
                updatePreview(tool)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        colorPickerG.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (tool) {
                    1 -> EditorSettings.brushColorG = progress
                    2 -> EditorSettings.textColorG = progress
                }
                updatePreview(tool)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        colorPickerB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (tool) {
                    1 -> EditorSettings.brushColorB = progress
                    2 -> EditorSettings.textColorB = progress
                }
                updatePreview(tool)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updatePreview(tool: Int) {
        val previewColor = when (tool) {
            1 -> Color.rgb(
                    EditorSettings.brushColorR,
                    EditorSettings.brushColorG,
                    EditorSettings.brushColorB
            )
            2 -> Color.rgb(
                    EditorSettings.textColorR,
                    EditorSettings.textColorG,
                    EditorSettings.textColorB
            )
            else -> return
        }
        colorPreview.setBackgroundColor(previewColor)
        when (tool) {
            1 -> {
                EditorSettings.brushColor = previewColor
                PhotoEditorActivity.updateBrushColor()
            }
            2 -> EditorSettings.textColor = previewColor
        }
    }
}
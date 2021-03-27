package am.justchat.photoeditor

import am.justchat.R
import am.justchat.activities.PhotoEditorActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EraserBottomSheet : BottomSheetDialogFragment() {
    private lateinit var eraserSize: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.editor_eraser_dialog,
            container, false
        )

        eraserSize = root.findViewById(R.id.eraser_size_bar)
        eraserSize.progress = EditorSettings.eraserSize
        setEraserSize()
        EditorFragment.updateEraser()

        return root
    }

    private fun setEraserSize() {
        eraserSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                EditorSettings.eraserSize = progress
                EditorFragment.updateEraser()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
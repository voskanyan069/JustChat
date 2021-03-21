package am.justchat.photoeditor

import am.justchat.R
import am.justchat.activities.PhotoEditorActivity
import am.justchat.views.EditorColorPicker
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TextBottomSheet : BottomSheetDialogFragment() {
    private lateinit var textInput: EditText
    private lateinit var textColorPicker: EditorColorPicker
    private lateinit var addText: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.editor_text_dialog,
            container, false
        )

        textInput = root.findViewById(R.id.text_editor_text)
        textColorPicker = root.findViewById(R.id.text_color_picker)
        addText = root.findViewById(R.id.text_submit)

        textColorPicker.colorPickerR.progress = EditorSettings.textColorR
        textColorPicker.colorPickerG.progress = EditorSettings.textColorG
        textColorPicker.colorPickerB.progress = EditorSettings.textColorB
        textColorPicker.setColorsListeners(2)

        addText()

        return root
    }

    private fun addText() {
        addText.setOnClickListener {
            PhotoEditorActivity.addText(textInput.text.toString())
            onDestroy()
        }
    }
}
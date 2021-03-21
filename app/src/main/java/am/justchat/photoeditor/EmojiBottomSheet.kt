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

class EmojiBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.editor_text_dialog,
            container, false
        )
        return root
    }
}
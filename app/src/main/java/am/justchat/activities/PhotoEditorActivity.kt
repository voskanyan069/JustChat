package am.justchat.activities

import am.justchat.R
import am.justchat.photoeditor.EditorFragment
import am.justchat.states.SwitchFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor)

        if (savedInstanceState == null) {
            SwitchFragment.switch(this, EditorFragment(), R.id.editor_fragment_container)
        }
    }
}
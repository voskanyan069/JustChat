package am.justchat.activities

import am.justchat.R
import am.justchat.photoeditor.BrushBottomSheet
import am.justchat.photoeditor.EditorSettings
import am.justchat.photoeditor.EraserBottomSheet
import am.justchat.photoeditor.TextBottomSheet
import am.justchat.views.EditorMenuItem
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView

class PhotoEditorActivity : AppCompatActivity() {
    private lateinit var photoEditorView: PhotoEditorView

    private lateinit var editorBrush: EditorMenuItem
    private lateinit var editorEraser: EditorMenuItem
    private lateinit var editorText: EditorMenuItem
    private lateinit var editorEmoji: EditorMenuItem
    private lateinit var editorFilter: EditorMenuItem

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        @SuppressLint("StaticFieldLeak")
        private lateinit var photoEditor: PhotoEditor

        fun updateBrushColor() {
            photoEditor.setBrushDrawingMode(true)
            photoEditor.brushColor = EditorSettings.brushColor
        }

        fun updateBrushSize() {
            photoEditor.brushSize = EditorSettings.brushSize.toFloat()
        }

        fun updateEraser() {
            photoEditor.setBrushEraserSize(EditorSettings.eraserSize.toFloat())
            photoEditor.brushEraser()
        }

        fun addText(text: String) {
            photoEditor.addText(text, EditorSettings.textColor)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor)

        photoEditorView = findViewById(R.id.photo_editor_view)
        editorBrush = findViewById(R.id.editor_brush)
        editorEraser = findViewById(R.id.editor_eraser)
        editorText = findViewById(R.id.editor_text)
        editorEmoji = findViewById(R.id.editor_emoji)
        editorFilter = findViewById(R.id.editor_filter)

        val fontTypeface = ResourcesCompat.getFont(this, R.font.firasans_medium)
        val emojiTypeface = Typeface.createFromAsset(assets, "emojione-android.ttf")

        photoEditor = PhotoEditor.Builder(this, photoEditorView)
                .setDefaultTextTypeface(fontTypeface)
                .setDefaultEmojiTypeface(emojiTypeface)
                .setPinchTextScalable(true)
                .build()
        checkPermission()
        editorTools()
    }

    private fun editorTools() {
        editorBrush.setOnClickListener {
            val brushBottomSheet = BrushBottomSheet()
            brushBottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
        editorEraser.setOnClickListener {
            val eraserBottomSheet = EraserBottomSheet()
            eraserBottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
        editorText.setOnClickListener {
            val textBottomSheet = TextBottomSheet()
            textBottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
        editorEmoji.setOnClickListener {
            Log.d("mTag", "Emojies list - ${PhotoEditor.getEmojis(this)}")
            photoEditor.addEmoji(PhotoEditor.getEmojis(this)[0])
        }
        editorFilter.setOnClickListener {  }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Snackbar.make(
                        photoEditorView,
                        "Permission denied",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            photoEditorView.source.setImageURI(data?.data)
            Log.d("mTag", "Image data - ${data?.data}")
        }
    }
}
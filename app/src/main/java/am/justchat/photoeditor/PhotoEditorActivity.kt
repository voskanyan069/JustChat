package am.justchat.photoeditor

import am.justchat.R
import am.justchat.views.BrushBottomSheet
import am.justchat.views.EditorMenuItem
import am.justchat.views.EraserBottomSheet
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import java.lang.Exception

class PhotoEditorActivity : AppCompatActivity() {
    private lateinit var photoEditorView: PhotoEditorView

    private lateinit var editorBrush: EditorMenuItem
    private lateinit var editorEraser: EditorMenuItem
    private lateinit var editorText: EditorMenuItem
    private lateinit var editorEmoji: EditorMenuItem
    private lateinit var editorFilter: EditorMenuItem

    private lateinit var bottomSheetDialog: BottomSheetDialogFragment

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        @SuppressLint("StaticFieldLeak")
        private lateinit var photoEditor: PhotoEditor

        fun updateBrush() {
            photoEditor.setBrushDrawingMode(true)
            photoEditor.brushColor = EditorSettings.brushColor
            photoEditor.brushSize = EditorSettings.brushSize.toFloat()
        }

        fun updateEraser() {
            photoEditor.setBrushEraserSize(EditorSettings.eraserSize.toFloat())
            photoEditor.brushEraser()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editor)

        photoEditorView = findViewById(R.id.photo_editor_view)
        bottomSheetDialog = BottomSheetDialogFragment()

        editorBrush = findViewById(R.id.editor_brush)
        editorEraser = findViewById(R.id.editor_eraser)
        editorText = findViewById(R.id.editor_text)
        editorEmoji = findViewById(R.id.editor_emoji)
        editorFilter = findViewById(R.id.editor_filter)
        photoEditor = PhotoEditor.Builder(this, photoEditorView)
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
            photoEditor.addText("Test text", R.color.black)
        }
        editorEmoji.setOnClickListener {  }
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
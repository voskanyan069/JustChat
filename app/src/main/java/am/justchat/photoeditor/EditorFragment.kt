package am.justchat.photoeditor

import am.justchat.R
import am.justchat.states.SwitchFragment
import am.justchat.views.EditorMenuItem
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView

class EditorFragment : Fragment() {
    private lateinit var photoEditorView: PhotoEditorView
    private lateinit var photoEditorUndo: ImageView
    private lateinit var photoEditorRedo: ImageView
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
            photoEditor.setBrushDrawingMode(true)
            photoEditor.brushSize = EditorSettings.brushSize.toFloat()
        }

        fun updateEraser() {
            photoEditor.setBrushEraserSize(EditorSettings.eraserSize.toFloat())
            photoEditor.brushEraser()
        }

        fun addText(text: String) {
            photoEditor.addText(text, EditorSettings.textColor)
        }

        fun addEmoji(emoji: String) {
            photoEditor.addEmoji(emoji)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_editor, container, false)

        photoEditorView = root.findViewById(R.id.photo_editor_view)
        photoEditorUndo = root.findViewById(R.id.photo_editor_undo)
        photoEditorRedo = root.findViewById(R.id.photo_editor_redo)
        editorBrush = root.findViewById(R.id.editor_brush)
        editorEraser = root.findViewById(R.id.editor_eraser)
        editorText = root.findViewById(R.id.editor_text)
        editorEmoji = root.findViewById(R.id.editor_emoji)
        editorFilter = root.findViewById(R.id.editor_filter)

        val fontTypeface = ResourcesCompat.getFont(context!!, R.font.firasans_medium)
        val emojiTypeface = Typeface.createFromAsset(context?.assets, "emojione-android.ttf")

        photoEditor = PhotoEditor.Builder(context, photoEditorView)
            .setDefaultTextTypeface(fontTypeface)
            .setDefaultEmojiTypeface(emojiTypeface)
            .setPinchTextScalable(true)
            .build()
        canvasInit()
        photoControllers()
        editorTools()

        if (!EditorSettings.isOriginalImageInitialized()) {
            checkPermission()
        } else {
            photoEditor.addImage(EditorSettings.originalImage)
        }

        return root
    }

    private fun editorTools() {
        editorBrush.setOnClickListener {
            val brushBottomSheet = BrushBottomSheet()
            brushBottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
        }
        editorEraser.setOnClickListener {
            val eraserBottomSheet = EraserBottomSheet()
            eraserBottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
        }
        editorText.setOnClickListener {
            val textBottomSheet = TextBottomSheet()
            textBottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
        }
        editorEmoji.setOnClickListener {
            val emojiBottomSheet = EmojiBottomSheet()
            emojiBottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
        }
        editorFilter.setOnClickListener {
            SwitchFragment.switch(activity!! as AppCompatActivity, FiltersFragment(), R.id.editor_fragment_container)
        }
    }

    private fun photoControllers() {
        photoEditorUndo.setOnClickListener {
            photoEditor.undo()
        }
        photoEditorRedo.setOnClickListener {
            photoEditor.redo()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
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

    private fun canvasInit() {
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val backgroundImage = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
        photoEditorView.source.setImageBitmap(backgroundImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, data?.data)
            EditorSettings.originalImage = bitmap
            photoEditor.addImage(bitmap)
        }
    }
}

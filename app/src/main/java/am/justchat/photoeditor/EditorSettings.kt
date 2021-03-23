package am.justchat.photoeditor

import android.graphics.Bitmap
import android.graphics.Color

object EditorSettings {
//    Brush
    var brushSize: Int = 10
    var brushColorR: Int = 0
    var brushColorG: Int = 0
    var brushColorB: Int = 0
    var brushColor: Int = Color.rgb(0, 0, 0)

//    Eraser
    var eraserSize: Int = 10

//    Text
    var textColorR: Int = 0
    var textColorG: Int = 0
    var textColorB: Int = 0
    var textColor: Int = Color.rgb(0, 0, 0)

//    Filter
    lateinit var originalImage: Bitmap
}
package am.justchat.views

import am.justchat.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class EditorFilterSeekbar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val filterTitle: TextView
    val filterSeekbar: SeekBar

    init {
        LayoutInflater.from(context).inflate(R.layout.editor_filter_seekbar, this, true)
        attrs.let {
            filterTitle = findViewById(R.id.editor_filter_title)
            filterSeekbar = findViewById(R.id.editor_filter_seekbar)

            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.EditorFilterSeekbar, 0, 0)
            val title = styledAttributes.getString(R.styleable.EditorFilterSeekbar_title)

            filterTitle.text = title
            styledAttributes.recycle()
        }
    }
}
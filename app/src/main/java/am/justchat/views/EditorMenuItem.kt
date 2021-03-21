package am.justchat.views

import am.justchat.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class EditorMenuItem(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.editor_menu_item, this, true)
        attrs.let {
            val icon: ImageView = findViewById(R.id.editor_item_icon)
            val title: TextView = findViewById(R.id.editor_item_title)

            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.EditorMenuItem, 0, 0)
            val titleText = styledAttributes.getString(R.styleable.EditorMenuItem_title)
            val iconDrawable = styledAttributes.getDrawable(R.styleable.EditorMenuItem_icon)

            title.text = titleText
            icon.setImageDrawable(iconDrawable)
            styledAttributes.recycle()
        }
    }
}
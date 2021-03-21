package am.justchat.photoeditor

import am.justchat.R
import am.justchat.adapters.EmojiAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor

class EmojiBottomSheet : BottomSheetDialogFragment() {
    private lateinit var emojiList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(
            R.layout.editor_emoji_dialog,
            container, false
        )

        val emojisArrayList: ArrayList<String> = PhotoEditor.getEmojis(this.context)
        emojiList = root.findViewById(R.id.emoji_list)
        emojiList.adapter = EmojiAdapter(emojisArrayList)
        emojiList.layoutManager = GridLayoutManager(context, 5)

        return root
    }
}
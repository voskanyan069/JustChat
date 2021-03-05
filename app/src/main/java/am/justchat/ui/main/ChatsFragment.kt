package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.ChatsAdapter
import am.justchat.adapters.StoryAdapter
import am.justchat.models.Chats
import am.justchat.models.Story
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatsFragment : Fragment() {
    private lateinit var storiesList: RecyclerView
    private lateinit var chatsList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        val stories = listOf(
            Story("USERNAME_1", "https://images.unsplash.com/photo-1614631446449-e2c7a0cc1428?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Story("USERNAME_2", "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
            Story("USERNAME_3", "https://images.unsplash.com/photo-1614676314170-3eb1d98d0a25?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Story("USERNAME_4", "https://images.unsplash.com/photo-1614613772023-6ef3a1618df3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80")
        )
        val chats = listOf(
            Chats("USERNAME_1", "Hi!", "https://images.unsplash.com/photo-1614631446449-e2c7a0cc1428?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Chats("USERNAME_2", "Hello", "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
            Chats("USERNAME_3", "Hey!", "https://images.unsplash.com/photo-1614676314170-3eb1d98d0a25?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Chats("USERNAME_4", "AAAAAAA", "https://images.unsplash.com/photo-1614613772023-6ef3a1618df3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Chats("USERNAME_5", "ggggg", "https://images.unsplash.com/photo-1614705755374-538591a6f8f4?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=700&q=80"),
            Chats("USERNAME_6", "wwwwwwwWwwwww", "https://images.unsplash.com/photo-1614738577406-4585f82d88d4?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Chats("USERNAME_7", "L0L", "https://images.unsplash.com/photo-1610347125613-3a3493563f6d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80")
        )


        storiesList = root.findViewById(R.id.stories_list)
        storiesList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        fillStoriesList(stories)

        chatsList = root.findViewById(R.id.chats_list)
        chatsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        fillChatsList(chats)

        return root
    }

    private fun fillStoriesList(data: List<Story>) {
        storiesList.adapter = StoryAdapter(data)
    }

    private fun fillChatsList(data: List<Chats>) {
        chatsList.adapter = ChatsAdapter(data)
    }
}
package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.adapters.ChatsAdapter
import am.justchat.adapters.StoryAdapter
import am.justchat.api.repos.ChatsRepo
import am.justchat.api.repos.ContactsRepo
import am.justchat.authentication.CurrentUser
import am.justchat.authentication.SignUpActivity
import am.justchat.models.Chat
import am.justchat.models.Story
import am.justchat.storage.SharedPreference
import android.content.Intent
import android.content.SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ChatsFragment : Fragment() {
    private lateinit var storiesList: RecyclerView
    private lateinit var chatsList: RecyclerView
    private lateinit var sharedPreference: SharedPreferences
    private var login: String = "null"
    private val chatsArrayList = arrayListOf<Chat>()
    private val storiesArrayList = arrayListOf<Story>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        sharedPreference = SharedPreference.getInstance(context!!).sharedPreferences
        getChatsList()
        val stories = listOf(
            Story("USERNAME_1", "https://images.unsplash.com/photo-1614631446449-e2c7a0cc1428?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Story("USERNAME_2", "https://images.unsplash.com/photo-1614676367446-17828873a71c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=675&q=80"),
            Story("USERNAME_3", "https://images.unsplash.com/photo-1614676314170-3eb1d98d0a25?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80"),
            Story("USERNAME_4", "https://images.unsplash.com/photo-1614613772023-6ef3a1618df3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80")
        )

        storiesList = root.findViewById(R.id.stories_list)
        storiesList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        fillStoriesList(stories)

        chatsList = root.findViewById(R.id.chats_list)
        chatsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        return root
    }

    private fun getStoriesList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }
    }

    private fun getChatsList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }

        val chatsRepo = ChatsRepo.getInstance()
        if (login != "null") {
            chatsRepo.chatsService
                ?.getUserChats(login)
                ?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        val jsonParser = JsonParser()
                        val chatsJsonStr = Gson().toJson(response.body())
                        val chatsJson: JsonObject = jsonParser.parse(chatsJsonStr).asJsonObject

                        try {
                            val code: Int = chatsJson.get("code").asInt
                            if (code == 1) {
                                moveToSignUp()
                            }
                        } catch (e: Exception) {
                            val chats: JsonArray = chatsJson.getAsJsonArray("chats")
                            for (i in 0..chats.size().minus(1)) {
                                val chatJson: JsonObject = chats.get(i).asJsonObject
                                val chat = Chat(
                                    profileUsername = chatJson.get("username").asString,
                                    lastMessage = chatJson.get("last_msg").asString,
                                    profileImage = chatJson.get("profile_image").asString
                                )
                                chatsArrayList.add(chat)
                            }
                            fillChatsList(chatsArrayList)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
                })
        }
    }

    private fun fillStoriesList(data: List<Story>) {
        storiesList.adapter = StoryAdapter(data)
    }

    private fun fillChatsList(data: List<Chat>) {
        chatsList.adapter = ChatsAdapter(data)
    }

    private fun moveToSignUp() {
        val intent = Intent(activity, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
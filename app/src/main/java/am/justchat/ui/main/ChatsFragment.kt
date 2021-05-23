package am.justchat.ui.main

import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.adapters.ChatsAdapter
import am.justchat.adapters.StoryAdapter
import am.justchat.api.Config
import am.justchat.api.repos.ChatsRepo
import am.justchat.api.repos.ContactsRepo
import am.justchat.api.repos.StoriesRepo
import am.justchat.models.Chat
import am.justchat.models.Story
import am.justchat.states.OnlineState
import am.justchat.storage.SharedPreference
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatsFragment : Fragment() {
    private lateinit var searchBar: EditText
    private lateinit var storiesList: RecyclerView
    private lateinit var chatsList: RecyclerView
    private lateinit var sharedPreference: SharedPreferences
    private val storiesRepo = StoriesRepo.getInstance()
    private val contactsRepo = ContactsRepo.getInstance()
    private val chatsRepo = ChatsRepo.getInstance()
    private val jsonParser = JsonParser()
    private var login: String = "null"
    private val chatsArrayList = arrayListOf<Chat>()
    private val storiesArrayList = arrayListOf<Story>()
    private var isFragmentActive = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        sharedPreference = SharedPreference.getInstance(context!!).sharedPreferences
        searchBar = root.findViewById(R.id.chats_search_bar)
        storiesList = root.findViewById(R.id.stories_list)
        chatsList = root.findViewById(R.id.chats_list)
        storiesList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        chatsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        searchBar.doAfterTextChanged { getChatsList() }

        return root
    }

    private fun requestsTask(): Job = CoroutineScope(Dispatchers.Main).launch {
        while (isFragmentActive) {
            Log.d("mTag", "Sent stories get request")
            getUserStoriesList()
            for (i in 0..4) {
                Log.d("mTag", "Sent chats get request (query - ${searchBar.text})")
                getChatsList()
                delay(Config.REQUESTS_DELAY)
            }
        }
    }

    private fun getUserStoriesList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }
        storiesArrayList.clear()
        storiesRepo.storiesService!!
            .getUserStories(login)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        val storiesJsonStr = Gson().toJson(response.body())
                        val storiesJson: JsonObject = jsonParser.parse(storiesJsonStr).asJsonObject
                        val storyJson: JsonObject = storiesJson.getAsJsonObject("stories")
                        val storyMediaPathJson: JsonArray = storyJson.getAsJsonArray("media_path")
                        val storyMediaPath = arrayListOf<String>()
                        for (k in 0..storyMediaPathJson.size().minus(1)) {
                            storyMediaPath.add(storyMediaPathJson.get(k).asString)
                        }
                        val story = Story(
                                profileUsername = storyJson.get("login").asString,
                                profileImage = storyJson.get("profile_image").asString,
                                mediaPath = storyMediaPath
                        )
                        storiesArrayList.add(story)
                        getContactsStoriesList()
                    } catch (e: Exception) {
                        Log.e("mTag", "Fetch error", e)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch error", t)
                }
            })
    }

    private fun getContactsStoriesList() {
        contactsRepo.contactsService!!
            .getContacts(login = login, query = "")
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        val contactsJsonStr = Gson().toJson(response.body())
                        val contactsJson: JsonObject = jsonParser.parse(contactsJsonStr).asJsonObject
                        val contacts: JsonArray = contactsJson.getAsJsonArray("contacts")
                        for (i in 0..contacts.size().minus(1)) {
                            val contactJson: JsonObject = contacts.get(i).asJsonObject
                            storiesRepo.storiesService
                                    ?.getUserStories(contactJson.get("login").asString)
                                    ?.enqueue(object : Callback<JsonObject> {
                                        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                            val storiesJsonStr = Gson().toJson(response.body())
                                            val storiesJson: JsonObject = jsonParser.parse(storiesJsonStr).asJsonObject
                                            try {
                                                val storyJson: JsonObject = storiesJson.getAsJsonObject("stories")
                                                val storyMediaPathJson: JsonArray = storyJson.getAsJsonArray("media_path")
                                                val storyMediaPath = arrayListOf<String>()
                                                for (k in 0..storyMediaPathJson.size().minus(1)) {
                                                    storyMediaPath.add(storyMediaPathJson.get(k).asJsonObject.get("path").asString)
                                                }
                                                val story = Story(
                                                        profileUsername = storyJson.get("login").asString,
                                                        profileImage = storyJson.get("profile_image").asString,
                                                        mediaPath = storyMediaPath
                                                )
                                                storiesArrayList.add(story)
                                            } catch (e: Exception) {
                                                Log.d("mTag", "Story get error", e)
                                            }
                                        }

                                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                            Log.e("mTag", "Fetch error", t)
                                        }
                                    })
                        }
                        fillStoriesList(storiesArrayList)
                    } catch (e: Exception) {
                        Log.e("mTag", "Fetch error", e)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch error", t)
                }
            })
    }

    private fun getChatsList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }
        chatsArrayList.clear()
        chatsRepo.chatsService!!
            .getUserChats(login = login, query = searchBar.text.toString())
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                ) {
                    try {
                        val chatsJsonStr = Gson().toJson(response.body())
                        val chatsJson: JsonObject = jsonParser.parse(chatsJsonStr).asJsonObject
                        val chats: JsonArray = chatsJson.getAsJsonArray("chats")
                        for (i in 0..chats.size().minus(1)) {
                            val chatJson: JsonObject = chats.get(i).asJsonObject
                            val chat = Chat(
                                    profileLogin = chatJson.get("login").asString,
                                    profileUsername = chatJson.get("username").asString,
                                    isOnline = when (chatJson.get("status").asString) {
                                        "online" -> OnlineState.ONLINE
                                        else -> OnlineState.OFFLINE
                                    },
                                    lastMessage = chatJson.get("last_msg").asString,
                                    profileImage = chatJson.get("profile_image").asString
                            )
                            chatsArrayList.add(chat)
                        }
                        fillChatsList(chatsArrayList)
                    } catch (e: Exception) {
                        Log.e("mTag", "Fetch error", e)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch error", t)
                }
            })
    }

    private fun fillStoriesList(data: List<Story>) {
        storiesList.adapter = StoryAdapter(data)
        storiesList.adapter?.notifyDataSetChanged()
    }

    private fun fillChatsList(data: List<Chat>) {
        chatsList.adapter = ChatsAdapter(data)
        chatsList.adapter?.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        isFragmentActive = false
    }

    override fun onResume() {
        super.onResume()
        isFragmentActive = true
        requestsTask()
    }
}
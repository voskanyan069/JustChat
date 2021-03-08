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
import am.justchat.api.repos.StoriesRepo
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
    private val storiesRepo = StoriesRepo.getInstance()
    private val jsonParser = JsonParser()
    private var login: String = "null"
    private val chatsArrayList = arrayListOf<Chat>()
    private val storiesArrayList = arrayListOf<Story>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        sharedPreference = SharedPreference.getInstance(context!!).sharedPreferences

        getUserStoriesList()
        getChatsList()

        storiesList = root.findViewById(R.id.stories_list)
        chatsList = root.findViewById(R.id.chats_list)
        storiesList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        chatsList.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)

        return root
    }

    private fun getUserStoriesList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }

        storiesRepo.storiesService
            ?.getUserStories(login)
            ?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val storiesJsonStr = Gson().toJson(response.body())
                    val storiesJson: JsonObject = jsonParser.parse(storiesJsonStr).asJsonObject
                    try {
                        val code: Int = storiesJson.get("code").asInt
                        if (code == 1) {
                            moveToSignUp()
                        }
                    } catch (e: Exception) {
                        val storyJson: JsonObject = storiesJson.getAsJsonObject("stories")
                        val storyMediaPathJson: JsonArray = storyJson.getAsJsonArray("media_path")
                        val storyMediaPath = arrayListOf<String>()
                        if (storyMediaPathJson.size() == 0) {
                            val story = Story(
                                    profileUsername = storyJson.get("login").asString,
                                    profileImage = storyJson.get("profile_image").asString,
                                    mediaPath = arrayListOf()
                            )
                            storiesArrayList.add(story)
                        } else {
                            for (k in 0..storyMediaPathJson.size().minus(1)) {
                                storyMediaPath.add(storyMediaPathJson.get(k).asString)
                            }
                            val story = Story(
                                    profileUsername = storyJson.get("login").asString,
                                    profileImage = storyJson.get("profile_image").asString,
                                    mediaPath = storyMediaPath
                            )
                            storiesArrayList.add(story)
                        }
                        getContactsStoriesList()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
            })
    }

    private fun getContactsStoriesList() {
        val contactsRepo = ContactsRepo.getInstance()
        contactsRepo.contactsService
            ?.getUserContacts(login)
            ?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val contactsJsonStr = Gson().toJson(response.body())
                    val contactsJson: JsonObject = jsonParser.parse(contactsJsonStr).asJsonObject
                    try {
                        val code: Int = contactsJson.get("code").asInt
                        if (code == 1) {
                            moveToSignUp()
                        }
                    } catch (e: Exception) {
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
                                                val code: Int = storiesJson.get("code").asInt
                                                if (code == 1) {
                                                    moveToSignUp()
                                                }
                                            } catch (e: Exception) {
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
                                                println("${story.profileUsername} CONTACT MEDIA PATH - $storyMediaPath")
                                                storiesArrayList.add(story)
                                            }
                                        }

                                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
                                    })
                        }
                        fillStoriesList(storiesArrayList)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
            })
    }

    private fun getChatsList() {
        if (login == "null") {
            login = sharedPreference.getString("login", null).toString()
        }

        val chatsRepo = ChatsRepo.getInstance()
        chatsRepo.chatsService
            ?.getUserChats(login)
            ?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
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
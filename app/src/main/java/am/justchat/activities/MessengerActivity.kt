package am.justchat.activities

import am.justchat.R
import am.justchat.adapters.MessageAdapter
import am.justchat.api.repos.MessagesRepo
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Message
import am.justchat.models.MessageUser
import am.justchat.models.ServerMessage
import am.justchat.states.MessageSenderState
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MessengerActivity : AppCompatActivity() {
    private lateinit var appToolbar: MaterialToolbar
    private lateinit var messagesList: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var messageSendButton: ImageView
    private lateinit var usersRepo: UsersRepo
    private lateinit var messagesRepo: MessagesRepo
    private val messagesArrayList = arrayListOf<Message>()
    private val jsonParser = JsonParser()
    private var login: String? = null
    private var username: String? = null
    private var profileImage: String? = null
    private var after: String = "0"
    private var isActivityActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        login = intent.getStringExtra("login")
        username = intent.getStringExtra("username")
        profileImage = intent.getStringExtra("profile_image")
        appToolbar = findViewById(R.id.chat_app_bar)
        messagesList = findViewById(R.id.messages_list)
        messagesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        messagesList.adapter = MessageAdapter(messagesArrayList)
        messageInput = findViewById(R.id.message_input)
        messageSendButton = findViewById(R.id.message_send)
        usersRepo = UsersRepo.getInstance()
        messagesRepo = MessagesRepo.getInstance()
        isActivityActive = true

        if (login != null && profileImage != null) {
            getUser()
            messagesListener()
            sendMessage()
        }
    }

    private fun getUser() {
        usersRepo.usersService!!
            .getUser(login!!)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val userJsonStr = Gson().toJson(response.body())
                    val userJson = jsonParser.parse(userJsonStr).asJsonObject
                    try {
                        val code: Int = userJson.get("code").asInt
                        if (code == 1) {
                            moveToSignUp()
                        }
                    } catch (e: Exception) {
                        val user = userJson.get("user").asJsonObject
                        appToolbar.title = user.get("username").asString
                        appToolbar.subtitle = user.get("status").asString.replace('o', 'O')
                        appToolbar.setNavigationOnClickListener {
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch Error", t)
                }
            })
    }

    private fun messagesListener(): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while (isActivityActive) {
                Log.d("mTag", "Messages get request sent")
                getMessages()
                updateMessages()
                delay(2000L)
            }
        }
    }

    private fun getMessages() {
        messagesRepo.messagesService!!
                .getMessages(CurrentUser.login.toString(), login.toString(), after)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val messagesJsonStr = Gson().toJson(response.body())
                        val messagesJson = jsonParser.parse(messagesJsonStr).asJsonObject
                        try {
                            val messages = messagesJson.get("messages").asJsonArray
                            for (i in 0..messages.size().minus(1)) {
                                val message = messages.get(i).asJsonObject
                                val senderLogin = message.get("login").asString
                                val chatLogin = message.get("username").asString
                                val profileImage = message.get("profile_image").asString
                                val messageText = message.get("message_text").asString
                                val messageTime = message.get("message_time").asString
                                val time = messageTime
                                        .substring(0, messageTime.indexOf('.'))
                                        .toLong()
                                after = time.plus(1).toString()
                                val thisMessage = Message(
                                        viewType = when {
                                            senderLogin.equals(login) -> MessageSenderState.RECEIVED
                                            else -> MessageSenderState.SENT
                                        },
                                        sender = MessageUser(username = chatLogin, profileImage = profileImage),
                                        message = messageText,
                                        time = time
                                )
                                messagesArrayList.add(thisMessage)
                            }
                        } catch (e: Exception) {
                            Log.e("mTag", "Error on message fetch", e)
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.d("mTag", "Fetch error", t)
                    }
                })
    }

    private fun sendMessage() {
        messageSendButton.setOnClickListener {
            when {
                messageInput.text.isNotBlank() -> {
                    messagesRepo.messagesService!!
                            .sendMessage(
                                    ServerMessage(
                                        login = CurrentUser.login.toString(),
                                        chatLogin = login.toString(),
                                        messageText = messageInput.text.toString()
                                    )
                            )
                            .enqueue(object : Callback<JsonObject> {
                                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                    val messageJsonUrl = Gson().toJson(response.body())
                                    val messageJson = jsonParser.parse(messageJsonUrl).asJsonObject
                                    try {
                                        val messageSent = messageJson.get("message_sent").asBoolean
                                        if (messageSent) {
                                            messageInput.text.clear()
                                        }
                                    } catch (e: Exception) {
                                        Log.d("mTag", "Message send error", e)
                                    }
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                    Log.e("mTag", "Fetch error", t)
                                }
                            })
                }
            }
        }
    }

    private fun updateMessages() {
        messagesList.adapter?.notifyDataSetChanged()
        messagesList.scrollToPosition(messagesArrayList.size - 1)
    }

    private fun moveToSignUp() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityActive = false
    }

    override fun onStop() {
        super.onStop()
        isActivityActive = false
    }

    override fun onResume() {
        super.onResume()
        isActivityActive = true
    }

    override fun onStart() {
        super.onStart()
        isActivityActive = true
    }
}

package am.justchat.activities

import am.justchat.R
import am.justchat.adapters.MessageAdapter
import am.justchat.adapters.StatusUpdaterAdapter
import am.justchat.api.Config
import am.justchat.api.repos.MessagesRepo
import am.justchat.api.repos.StatusRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Message
import am.justchat.models.MessageUser
import am.justchat.models.ServerMessage
import am.justchat.states.MessageSenderState
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessengerActivity : AppCompatActivity() {
    private lateinit var appToolbar: MaterialToolbar
    private lateinit var messagesList: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var messageSendButton: ImageView
    private val statusRepo = StatusRepo.getInstance()
    private val messagesRepo = MessagesRepo.getInstance()
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

        StatusUpdaterAdapter.updateStatus("online")
        login = intent.getStringExtra("login")
        username = intent.getStringExtra("username")
        profileImage = intent.getStringExtra("profile_image")
        appToolbar = findViewById(R.id.chat_app_bar)
        messagesList = findViewById(R.id.messages_list)
        messagesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        messagesList.adapter = MessageAdapter(messagesArrayList)
        messageInput = findViewById(R.id.message_input)
        messageSendButton = findViewById(R.id.message_send)
        isActivityActive = true

        requestsTask()
        sendMessage()
    }

    private fun requestsTask(): Job = CoroutineScope(Dispatchers.Main).launch {
        while (isActivityActive) {
            Log.d("mTag", "Messages get request sent")
            Log.d("mTag", "Status get request sent")
            getStatus()
            getMessages()
            updateMessages()
            StatusUpdaterAdapter.updateStatus("online")
            delay(Config.REQUESTS_DELAY)
        }
    }

    private fun getStatus() {
        statusRepo.statusService!!
            .getStatus(login.toString())
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    try {
                        val statusJsonStr = Gson().toJson(response.body())
                        val statusJson = jsonParser.parse(statusJsonStr).asJsonObject
                        val status = statusJson.get("status").asString
                        appToolbar.title = username.toString()
                        appToolbar.subtitle = status.replace('o', 'O')
                        appToolbar.setNavigationOnClickListener {
                            finish()
                        }
                    } catch (e: Exception) {
                        Log.e("mTag", "Fetch Error", e)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mTag", "Fetch Error", t)
                }
            })
    }

    private fun getMessages() {
        messagesRepo.messagesService!!
                .getMessages(CurrentUser.login.toString(), login.toString(), after)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        try {
                            val messagesJsonStr = Gson().toJson(response.body())
                            val messagesJson = jsonParser.parse(messagesJsonStr).asJsonObject
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
                            Log.e("mTag", "Message get error", e)
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
                                    try {
                                        val messageJsonUrl = Gson().toJson(response.body())
                                        val messageJson = jsonParser.parse(messageJsonUrl).asJsonObject
                                        val messageSent = messageJson.get("message_sent").asBoolean
                                        if (messageSent) {
                                            messageInput.text.clear()
                                        }
                                    } catch (e: Exception) {
                                        Log.d("mTag", "Message sent error", e)
                                    }
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                    Snackbar.make(messageSendButton, t.message.toString(), Snackbar.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        isActivityActive = false
        StatusUpdaterAdapter.updateStatus("offline")
    }

    override fun onStop() {
        super.onStop()
        isActivityActive = false
        StatusUpdaterAdapter.updateStatus("offline")
    }

    override fun onStart() {
        super.onStart()
        isActivityActive = true
        StatusUpdaterAdapter.updateStatus("online")
    }
}

package am.justchat.activities

import am.justchat.R
import am.justchat.adapters.MessageAdapter
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.Message
import am.justchat.models.MessageUser
import am.justchat.states.MessageSenderState
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
    private var login: String? = null
    private var username: String? = null
    private var profileImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        login = intent.getStringExtra("login")
        username = intent.getStringExtra("username")
        profileImage = intent.getStringExtra("profile_image")
        appToolbar = findViewById(R.id.chat_app_bar)
        messagesList = findViewById(R.id.messages_list)
        messagesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        messageInput = findViewById(R.id.message_input)
        messageSendButton = findViewById(R.id.message_send)
        usersRepo = UsersRepo.getInstance()

        if (login != null && profileImage != null) {
            getUser()
            getMessages()
        }
    }

    private fun getUser() {
        usersRepo.usersService!!
            .getUser(login!!)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    val jsonParser = JsonParser()
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

    private fun getMessages() {
        val list = arrayListOf<Message>(
            Message(MessageSenderState.SENT, "Hi!", MessageUser(CurrentUser.username!!, CurrentUser.profileImage!!), (165648904.5589867).toLong()),
            Message(MessageSenderState.RECEIVED, "Hello", MessageUser(username!!, profileImage!!), (165648912304.5589867).toLong()),
            Message(MessageSenderState.SENT, "How are you?", MessageUser(CurrentUser.username!!, CurrentUser.profileImage!!), (165648943204.5589867).toLong()),
            Message(MessageSenderState.RECEIVED, "Fine, and you?", MessageUser(username!!, profileImage!!), (165648904.5523289867).toLong()),
            Message(MessageSenderState.SENT, "Thanks, it's just a test", MessageUser(CurrentUser.username!!, CurrentUser.profileImage!!), (16325648904.5589867).toLong()),
            Message(MessageSenderState.SENT, "Bye", MessageUser(CurrentUser.username!!, CurrentUser.profileImage!!), (1656489045.5589867).toLong())
        )

        messagesList.adapter = MessageAdapter(list)
        updateMessages()
    }

    private fun updateMessages() {
        messagesList.adapter?.notifyDataSetChanged()
    }

    private fun moveToSignUp() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
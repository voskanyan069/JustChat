package am.justchat.activities

import am.justchat.R
import am.justchat.adapters.StatusUpdaterAdapter
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.states.SwitchFragment
import am.justchat.storage.SharedPreference
import am.justchat.ui.main.CallsFragment
import am.justchat.ui.main.ChatsFragment
import am.justchat.ui.main.ContactsFragment
import am.justchat.ui.main.SettingsFragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var sharedPreference: SharedPreferences
    private val usersRepo = UsersRepo.getInstance()
    private var isActivityActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAuthenticated()
        if (savedInstanceState == null) {
            SwitchFragment.switch(this, ChatsFragment(), R.id.fragment_container)
        }
        init()
    }

    private fun init() {
        bottomNavigationMenu = findViewById(R.id.bottom_navigation_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener(navListener)
    }

    private fun isAuthenticated() {
        sharedPreference = SharedPreference.getInstance(this).sharedPreferences

        val isAuth = sharedPreference.getBoolean("is_authenticated", false)
        if (!isAuth) {
            moveToSignUp()
        } else {
            val login = sharedPreference.getString("login", null)
            val username = sharedPreference.getString("username", null)
            StatusUpdaterAdapter.updateStatus("online")

            if (login != null) {
                usersRepo.usersService!!
                    .getUser(login)
                    .enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                            try {
                                val jsonParser = JsonParser()
                                val userJsonStr = Gson().toJson(response.body())
                                val userJson: JsonObject = jsonParser.parse(userJsonStr).asJsonObject
                                CurrentUser.login = login
                                CurrentUser.username = username
                                CurrentUser.profileImage = userJson
                                        .get("user").asJsonObject
                                        .get("profile_image").asString
                                isActivityActive = true
                                requestsTask()
                            } catch (e: Exception) {
                                Log.e("mTag", "Fetch Error", e)
                            }
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("mTag", "Fetch error", t)
                        }
                    })
            }
        }
    }

    private fun requestsTask(): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            while (isActivityActive) {
                StatusUpdaterAdapter.updateStatus("online")
                delay(10000L)
            }
        }
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                var selectedFragment: Fragment? = null
                when (item.itemId) {
                    R.id.tab_chats -> selectedFragment = ChatsFragment()
                    R.id.tab_calls -> selectedFragment = CallsFragment()
                    R.id.tab_contacts -> selectedFragment = ContactsFragment()
                    R.id.tab_settings -> selectedFragment = SettingsFragment()
                }
                SwitchFragment.switch(this, selectedFragment!!, R.id.fragment_container)
                true
            }

    private fun moveToSignUp() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
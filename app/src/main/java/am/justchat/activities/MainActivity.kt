package am.justchat.activities

import am.justchat.R
import am.justchat.api.repos.StatusRepo
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.fragments.SwitchFragment
import am.justchat.models.Status
import am.justchat.storage.SharedPreference
import am.justchat.ui.main.CallsFragment
import am.justchat.ui.main.ChatsFragment
import am.justchat.ui.main.ContactsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import am.justchat.ui.main.SettingsFragment
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var sharedPreference: SharedPreferences
    private val statusRepo = StatusRepo.getInstance()
    private val usersRepo = UsersRepo.getInstance()

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
            updateStatus("online")

            if (login != null) {
                usersRepo.usersService!!
                    .getUser(login)
                    .enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                            val jsonParser = JsonParser()
                            val userJsonStr = Gson().toJson(response.body())
                            val userJson: JsonObject = jsonParser.parse(userJsonStr).asJsonObject
                            try {
                                val code: Int = userJson.get("code").asInt
                                if (code == 1) {
                                    moveToSignUp()
                                }
                            } catch (e: Exception) {
                                CurrentUser.login = login
                                CurrentUser.username = username
                                CurrentUser.profileImage = userJson
                                    .get("user").asJsonObject
                                    .get("profile_image").asString
                            }
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            Log.e("mTag", "Fetch error", t)
                        }
                    })
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

    private fun updateStatus(status: String) {
        statusRepo.statusService!!
                .updateStatus(
                        Status(
                                login = CurrentUser.login.toString(),
                                newStatus = status
                        )
                ).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        Log.d("mTag", "Status updated")
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e("mTag", "Fetch error", t)
                    }
                })
    }

    private fun moveToSignUp() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateStatus("offline")
    }

    override fun onStop() {
        super.onStop()
        updateStatus("offline")
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onStart() {
        super.onStart()
        updateStatus("online")
    }
}
package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SettingsFragment : Fragment() {
    private lateinit var profileImage: CircleImageView
    private lateinit var profileLogin: TextView
    private lateinit var profileUsername: TextView
    private lateinit var logOutText: TextView
    private lateinit var notificationSwitcher: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        profileImage = root.findViewById(R.id.settings_avatar)
        profileLogin = root.findViewById(R.id.settings_login)
        profileUsername = root.findViewById(R.id.settings_username)
        logOutText = root.findViewById(R.id.settings_log_out)
        notificationSwitcher = root.findViewById(R.id.notification_enabled)

        getUser()
        logOut()
        switchNotificationsState()

        return root
    }

    private fun getUser() {
        val usersRepo = UsersRepo.getInstance()
        usersRepo.usersService
            ?.getUser(CurrentUser.login.toString())
            ?.enqueue(object : Callback<JsonObject> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
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
                        Picasso.get().load(user.get("profile_image").asString).into(profileImage)
                        profileLogin.text = "Login: ${user.get("login").asString}"
                        profileUsername.text = "Username: ${user.get("username").asString}"
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
            })
    }

    private fun logOut() {
        logOutText.setOnClickListener {
             moveToSignUp()
        }
    }

    private fun switchNotificationsState() = notificationSwitcher.setOnCheckedChangeListener { _, _ -> }

    private fun moveToSignUp() {
        val intent = Intent(context, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
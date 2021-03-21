package am.justchat.ui.main

import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.activities.PhotoEditorActivity
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.UpdateUser
import am.justchat.storage.SharedPreference
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {
    private lateinit var profileImage: CircleImageView
    private lateinit var profileLogin: TextView
    private lateinit var profileUsername: TextView
    private lateinit var logOutText: TextView
    private lateinit var updateUsername: TextView;
    private lateinit var updatePassword: TextView;
    private lateinit var notificationSwitcher: SwitchMaterial

    private lateinit var messageBoxView: View
    private lateinit var messageBoxBuilder: AlertDialog.Builder
    private lateinit var messageBoxInstance: AlertDialog
    private lateinit var messageTitle: TextView
    private lateinit var messageContentInput: EditText
    private lateinit var messageErrorMessage: TextView
    private lateinit var messageSubmitBtn: Button

    private lateinit var sharedPreferenceEditor: SharedPreferences.Editor
    private val jsonParser = JsonParser()
    private val usersRepo = UsersRepo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferenceEditor = SharedPreference(context!!).editPreferences
        profileImage = root.findViewById(R.id.settings_avatar)
        profileLogin = root.findViewById(R.id.settings_login)
        profileUsername = root.findViewById(R.id.settings_username)
        logOutText = root.findViewById(R.id.settings_log_out)
        updateUsername = root.findViewById(R.id.settings_update_username)
        updatePassword = root.findViewById(R.id.settings_update_password)
        notificationSwitcher = root.findViewById(R.id.notification_enabled)

        getUser()
        initDialog()
        updateProfileImage()
        updateUsername()
        updatePassword()
        switchNotificationsState()
        logOut()

        return root
    }

    private fun updateProfileImage() {
        profileImage.setOnClickListener {
            val intent = Intent(this.context, PhotoEditorActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUsername() {
        updateUsername.setOnClickListener {
            messageBoxInstance = messageBoxBuilder.show()
            messageTitle.text = getString(R.string.update_username)
            messageContentInput.text.clear()
            messageContentInput.hint = getString(R.string.new_username)
            messageContentInput.inputType = InputType.TYPE_CLASS_TEXT
            messageSubmitBtn.text = getString(R.string.update)
            messageSubmitBtn.setOnClickListener {
                messageContentInput.text = messageContentInput.text.trim() as Editable
                when {
                    messageContentInput.text.isBlank() -> messageErrorMessage.text =
                            getString(R.string.incorrect_username)
                    messageContentInput.text.length < 4 -> messageErrorMessage.text =
                            getString(R.string.username_min_len)
                    messageContentInput.text.length > 16 -> messageErrorMessage.text =
                            getString(R.string.username_max_len)
                    else -> {
                        val newUsername = messageContentInput.text.toString()
                        usersRepo.usersService!!
                                .updateUsername(UpdateUser(
                                        login = CurrentUser.login.toString(),
                                        newValue = newUsername
                                )).enqueue(object : Callback<JsonObject> {
                                    @SuppressLint("SetTextI18n")
                                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                        messageErrorMessage.text = ""
                                        val userJsonStr = Gson().toJson(response.body())
                                        val userJson = jsonParser.parse(userJsonStr).asJsonObject
                                        val isUsernameUpdated = userJson.get("username_updated").asBoolean
                                        val text = when(isUsernameUpdated) {
                                            true -> "Username was successfully updated"
                                            false -> "Some was wrong while updating username, please try again later"
                                        }
                                        if (isUsernameUpdated) {
                                            CurrentUser.username = newUsername
                                            sharedPreferenceEditor.putString("username", newUsername)
                                            sharedPreferenceEditor.commit()
                                        }
                                        Snackbar.make(updateUsername, text, Snackbar.LENGTH_SHORT).show()
                                        messageBoxInstance.dismiss()
                                        profileUsername.text = "Username: ${CurrentUser.username}"
                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        Log.e("mTag", "Fetch error", t)
                                    }
                                })
                    }
                }
            }
        }
    }

    private fun updatePassword() {
        updatePassword.setOnClickListener {
            messageBoxInstance = messageBoxBuilder.show()
            messageTitle.text = getString(R.string.update_password)
            messageContentInput.text.clear()
            messageContentInput.hint = getString(R.string.new_password)
            messageContentInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            messageSubmitBtn.text = getString(R.string.update)
            messageSubmitBtn.setOnClickListener {
                messageContentInput.text = messageContentInput.text.trim() as Editable
                when {
                    messageContentInput.text.isBlank() -> messageErrorMessage.text =
                            getString(R.string.incorrect_username)
                    messageContentInput.text.length < 6 -> messageErrorMessage.text =
                            getString(R.string.password_min_len)
                    messageContentInput.text.length > 16 -> messageErrorMessage.text =
                            getString(R.string.password_max_len)
                    else -> {
                        usersRepo.usersService!!
                                .updatePassword(UpdateUser(
                                        login = CurrentUser.login.toString(),
                                        newValue = messageContentInput.text.toString()
                                )).enqueue(object : Callback<JsonObject> {
                                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                                        messageErrorMessage.text = ""
                                        val userJsonStr = Gson().toJson(response.body())
                                        val userJson = jsonParser.parse(userJsonStr).asJsonObject
                                        val text = when(userJson.get("password_updated").asBoolean) {
                                            true -> "Password was successfully updated"
                                            false -> "Some was wrong while updating password, please try again later"
                                        }
                                        Snackbar.make(updatePassword, text, Snackbar.LENGTH_SHORT).show()
                                        messageBoxInstance.dismiss()
                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        Log.e("mTag", "Fetch error", t)
                                    }
                                })
                    }
                }
            }
        }
    }

    private fun initDialog() {
        messageBoxView = LayoutInflater.from(activity).inflate(R.layout.update_user_dialog, null, false)
        messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        messageBoxView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        messageTitle = messageBoxView.findViewById(R.id.dialog_title)
        messageContentInput = messageBoxView.findViewById(R.id.dialog_input)
        messageErrorMessage = messageBoxView.findViewById(R.id.dialog_error_msg)
        messageSubmitBtn = messageBoxView.findViewById(R.id.dialog_submit)

        messageBoxBuilder.setOnCancelListener {
            (messageBoxView.parent as ViewGroup).removeAllViews()
        }

        messageBoxBuilder.setOnDismissListener {
            (messageBoxView.parent as ViewGroup).removeAllViews()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUser() {
        profileLogin.text = "Login: ${CurrentUser.login}"
        profileUsername.text = "Username: ${CurrentUser.username}"
        Picasso.get().load(CurrentUser.profileImage.toString()).into(profileImage)
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
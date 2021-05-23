package am.justchat.ui.main

import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.api.repos.UsersRepo
import am.justchat.authentication.CurrentUser
import am.justchat.models.UpdateUser
import am.justchat.storage.SharedPreference
import am.justchat.storage.StorageInstance
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
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
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
    private lateinit var storageInstance: StorageReference
    private val usersRepo = UsersRepo.getInstance()
    private val jsonParser = JsonParser()

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val IMAGE_CROP_CODE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferenceEditor = SharedPreference(context!!).editPreferences
        storageInstance = StorageInstance.getInstance().reference
        profileImage = root.findViewById(R.id.settings_avatar)
        profileLogin = root.findViewById(R.id.settings_login)
        profileUsername = root.findViewById(R.id.settings_username)
        logOutText = root.findViewById(R.id.settings_log_out)
        updateUsername = root.findViewById(R.id.settings_update_username)
        updatePassword = root.findViewById(R.id.settings_update_password)
        notificationSwitcher = root.findViewById(R.id.notification_enabled)

        updateUser()
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
            checkPermission()
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
                            .updateUsername(
                                UpdateUser(
                                    login = CurrentUser.login.toString(),
                                    newValue = newUsername
                                )
                            ).enqueue(object : Callback<JsonObject> {
                                @SuppressLint("SetTextI18n")
                                override fun onResponse(
                                    call: Call<JsonObject>,
                                    response: Response<JsonObject>
                                ) {
                                    try {
                                        messageErrorMessage.text = ""
                                        val userJsonStr = Gson().toJson(response.body())
                                        val userJson = jsonParser.parse(userJsonStr).asJsonObject
                                        val isUsernameUpdated =
                                                userJson.get("username_updated").asBoolean
                                        val text = when (isUsernameUpdated) {
                                            true -> "Username was successfully updated"
                                            false -> "Some was wrong while updating username, please try again later"
                                        }
                                        if (isUsernameUpdated) {
                                            CurrentUser.username = newUsername
                                            sharedPreferenceEditor.putString(
                                                    "username",
                                                    newUsername
                                            )
                                            sharedPreferenceEditor.commit()
                                        }
                                        Snackbar.make(updateUsername, text, Snackbar.LENGTH_SHORT)
                                                .show()
                                        messageBoxInstance.dismiss()
                                        profileUsername.text = "Username: ${CurrentUser.username}"
                                    } catch (e: Exception) {
                                        Log.e("mTag", "Fetch error", e)
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
                            .updatePassword(
                                UpdateUser(
                                    login = CurrentUser.login.toString(),
                                    newValue = messageContentInput.text.toString()
                                )
                            ).enqueue(object : Callback<JsonObject> {
                                override fun onResponse(
                                    call: Call<JsonObject>,
                                    response: Response<JsonObject>
                                ) {
                                    try {
                                        messageErrorMessage.text = ""
                                        val userJsonStr = Gson().toJson(response.body())
                                        val userJson = jsonParser.parse(userJsonStr).asJsonObject
                                        val text = when (userJson.get("password_updated").asBoolean) {
                                            true -> "Password was successfully updated"
                                            false -> "Some was wrong while updating password, please try again later"
                                        }
                                        Snackbar.make(updatePassword, text, Snackbar.LENGTH_SHORT)
                                                .show()
                                        messageBoxInstance.dismiss()
                                    } catch (e: Exception) {
                                        Log.e("mTag", "Fetch error", e)
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
    }

    @SuppressLint("InflateParams")
    private fun initDialog() {
        messageBoxView = LayoutInflater.from(activity).inflate(
            R.layout.update_user_dialog,
            null,
            false
        )
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
    private fun updateUser() {
        profileLogin.text = "Login: ${CurrentUser.login}"
        profileUsername.text = "Username: ${CurrentUser.username}"
        Picasso.get().load(CurrentUser.profileImage.toString()).into(profileImage)
    }

    private fun logOut() {
        logOutText.setOnClickListener {
            moveToSignUp()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Snackbar.make(
                        profileImage,
                        "Permission denied",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    val destinationUri =
                        Uri.fromFile(File(context?.cacheDir, "profile_cropped_image"))
                    UCrop.of(data?.data!!, destinationUri)
                        .withAspectRatio(4f, 4f)
                        .start(requireContext(), this, IMAGE_CROP_CODE)
                }
                IMAGE_CROP_CODE -> {
                    val resultUri: Uri = UCrop.getOutput(data!!)!!
                    val imagePath = "users/profile_images/${CurrentUser.login}.jpg"
                    storageInstance
                        .child(imagePath)
                        .putFile(resultUri)
                        .addOnSuccessListener {
                            storageInstance.child(imagePath).downloadUrl
                                .addOnSuccessListener {
                                    CurrentUser.profileImage = it.toString()
                                    usersRepo.usersService!!
                                        .updateProfileImage(
                                            UpdateUser(
                                                login = CurrentUser.login.toString(),
                                                newValue = it.toString()
                                            )
                                        ).enqueue(object : Callback<JsonObject> {
                                            override fun onResponse(
                                                call: Call<JsonObject>,
                                                response: Response<JsonObject>
                                            ) {
                                                try {
                                                    val userJsonStr = Gson().toJson(response.body())
                                                    val userJson =
                                                            jsonParser.parse(userJsonStr).asJsonObject
                                                    val imageUpdated =
                                                            userJson.get("profile_image_updated").asBoolean
                                                    if (imageUpdated) {
                                                        updateUser()
                                                        Log.d("mTag", "Profile image updated")
                                                    }
                                                } catch (e: Exception) {
                                                    Snackbar.make(
                                                            profileImage,
                                                            "If the image is not changed reload the screen",
                                                            Snackbar.LENGTH_SHORT
                                                    ).show()
                                                    updateUser()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<JsonObject>,
                                                t: Throwable
                                            ) {
                                                Log.e("mTag", "Fetch error", t)
                                            }
                                        })
                                }
                        }
                }
            }
        }
    }

    private fun switchNotificationsState() =
        notificationSwitcher.setOnCheckedChangeListener { _, _ -> }

    private fun moveToSignUp() {
        val intent = Intent(context, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
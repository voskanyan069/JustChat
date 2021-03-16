package am.justchat.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.activities.AuthenticationActivity
import am.justchat.activities.MainActivity
import am.justchat.api.repos.UsersRepo
import am.justchat.fragments.SwitchFragment
import am.justchat.models.User
import am.justchat.storage.SharedPreference
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.input.InputManager
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpFragment : Fragment() {
    private lateinit var loginInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var errorMessage: TextView
    private lateinit var signUpButton: Button
    private lateinit var logInText: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var editorPreference: SharedPreferences.Editor
    private val usersRepo = UsersRepo.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_sign_up, container, false)

        editorPreference = SharedPreference.getInstance(context!!).editPreferences
        loginInput = root.findViewById(R.id.sign_up_login_input)
        usernameInput = root.findViewById(R.id.sign_up_username_input)
        passwordInput = root.findViewById(R.id.sign_up_password_input)
        errorMessage = root.findViewById(R.id.sign_up_error_msg)
        signUpButton = root.findViewById(R.id.sign_up_submit_btn)
        logInText = root.findViewById(R.id.log_in_text)

        progressDialog = ProgressDialog(context!!)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Please wait until loading end.")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        editorPreference.putBoolean("is_authenticated", false)
        editorPreference.commit()

        signUp()
        toLogIn()

        return root
    }

    private fun signUp() {
        signUpButton.setOnClickListener {
            loginInput.text = loginInput.text.trim() as Editable?
            usernameInput.text = usernameInput.text.trim() as Editable?
            when {
                loginInput.text.isBlank() -> errorMessage.text = getString(R.string.incorrect_login)
                loginInput.text.length < 4 -> errorMessage.text = getString(R.string.login_min_len)
                loginInput.text.length > 16 -> errorMessage.text = getString(R.string.login_max_len)
                loginInput.text.contains("/") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                loginInput.text.contains("?") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                loginInput.text.contains("&") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                loginInput.text.contains(".") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                loginInput.text.contains(",") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                loginInput.text.contains("null") -> errorMessage.text =
                    getString(R.string.not_allowed_login)
                usernameInput.text.isBlank() -> errorMessage.text =
                    getString(R.string.incorrect_username)
                usernameInput.text.length < 4 -> errorMessage.text =
                    getString(R.string.username_min_len)
                usernameInput.text.length > 16 -> errorMessage.text =
                    getString(R.string.username_max_len)
                passwordInput.text.isBlank() -> errorMessage.text =
                    getString(R.string.incorrect_password)
                passwordInput.text.length < 6 -> errorMessage.text =
                    getString(R.string.password_min_len)
                passwordInput.text.length > 16 -> errorMessage.text =
                    getString(R.string.password_max_ken)
                else -> {
                    errorMessage.text = ""
                    progressDialog.show()
                    val user = User(
                        loginInput.text.toString(),
                        usernameInput.text.toString(),
                        passwordInput.text.toString()
                    )
                    usersRepo.usersService!!
                        .addUser(user)
                        .enqueue(object : Callback<JsonObject> {
                            override fun onResponse(
                                call: Call<JsonObject>,
                                response: Response<JsonObject>
                            ) {
                                val jsonParser = JsonParser()
                                val userJsonStr = Gson().toJson(response.body())
                                val userJson: JsonObject = jsonParser
                                    .parse(userJsonStr).asJsonObject
                                try {
                                    val code: Int = userJson.get("code").asInt
                                    if (code == 2) {
                                        errorMessage.text = getString(R.string.taken_login)
                                        cancelProgressDialog()
                                    }
                                } catch (e: Exception) {
                                    try {
                                        val isCreated = userJson.get("created").asBoolean
                                        if (isCreated) {
                                            editorPreference.putBoolean(
                                                "is_authenticated",
                                                true
                                            )
                                            editorPreference.putString(
                                                "login",
                                                loginInput.text.toString()
                                            )
                                            editorPreference.putString(
                                                "username",
                                                usernameInput.text.toString()
                                            )
                                            editorPreference.commit()
                                            CurrentUser.login = loginInput.text.toString()
                                            CurrentUser.username = usernameInput.text.toString()
                                            val intent = Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                    } catch (e: Exception) {
                                        errorMessage.text = e.message
                                        Log.d("mTag", "Sign up error", e)
                                    }
                                    cancelProgressDialog()
                                }
                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                cancelProgressDialog()
                                Log.e("mTag", "Fetch error", t)
                            }
                        })
                }
            }
        }
    }

    private fun toLogIn() {
        logInText.setOnClickListener {
            SwitchFragment.switch(activity as AppCompatActivity, LogInFragment(), R.id.auth_fragment_container)
        }
    }

    private fun cancelProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.cancel()
        }
    }
}
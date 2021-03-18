package am.justchat.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import am.justchat.activities.MainActivity
import am.justchat.api.repos.UsersRepo
import am.justchat.states.SwitchFragment
import am.justchat.storage.SharedPreference
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.util.Log
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

class LogInFragment : Fragment() {
    private lateinit var loginInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var errorMessage: TextView
    private lateinit var logInButton: Button
    private lateinit var createAccountText: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var editorPreference: SharedPreferences.Editor
    private val usersRepo = UsersRepo.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_log_in, container, false)

        editorPreference = SharedPreference.getInstance(context!!).editPreferences
        loginInput = root.findViewById(R.id.log_in_login_input)
        passwordInput = root.findViewById(R.id.log_in_password_input)
        errorMessage = root.findViewById(R.id.log_in_error_msg)
        logInButton = root.findViewById(R.id.log_in_submit_btn)
        createAccountText = root.findViewById(R.id.create_account_text)

        progressDialog = ProgressDialog(context!!)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Please wait until loading end.")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        editorPreference.putBoolean("is_authenticated", false)
        editorPreference.commit()

        logIn()
        toSignUp()

        return root
    }

    private fun logIn() {
        logInButton.setOnClickListener {
            loginInput.text = loginInput.text.trim() as Editable?
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
                passwordInput.text.isBlank() -> errorMessage.text =
                    getString(R.string.incorrect_password)
                passwordInput.text.length < 6 -> errorMessage.text =
                    getString(R.string.password_min_len)
                passwordInput.text.length > 16 -> errorMessage.text =
                    getString(R.string.password_max_len)
                else -> {
                    progressDialog.show()
                    usersRepo.usersService!!
                        .getUser(loginInput.text.toString())
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
                                    if (code == 1) {
                                        errorMessage.text = getString(R.string.login_not_detected)
                                        cancelProgressDialog()
                                    }
                                } catch (e: Exception) {
                                    val user: JsonObject = userJson.get("user").asJsonObject
                                    val password: String = user.get("password").asString
                                    when (password) {
                                        passwordInput.text.toString() -> {
                                            errorMessage.text = ""
                                            val username: String = user.get("username").asString
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
                                                username
                                            )
                                            editorPreference.commit()
                                            CurrentUser.login = loginInput.text.toString()
                                            CurrentUser.username = username
                                            val intent = Intent(
                                                context,
                                                MainActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                        else -> {
                                            println("ELSE")
                                            errorMessage.text =
                                                getString(R.string.wrong_password)
                                            cancelProgressDialog()
                                        }
                                    }
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

    private fun toSignUp() {
        createAccountText.setOnClickListener {
            SwitchFragment.switch(activity as AppCompatActivity, SignUpFragment(), R.id.auth_fragment_container)
        }
    }

    private fun cancelProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.cancel()
        }
    }
}
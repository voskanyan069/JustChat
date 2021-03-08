package am.justchat.authentication

import am.justchat.R
import am.justchat.activities.MainActivity
import am.justchat.models.User
import am.justchat.api.repos.UsersRepo
import am.justchat.storage.SharedPreference
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

class SignUpActivity : AppCompatActivity() {
    private lateinit var loginInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var errorMessage: TextView
    private lateinit var signUpButton: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var usersRepo: UsersRepo
    private lateinit var editorPreference: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
        signUp()
    }

    private fun init() {
        editorPreference = SharedPreference.getInstance(this).editPreferences
        loginInput = findViewById(R.id.sign_up_login_input)
        usernameInput = findViewById(R.id.sign_up_username_input)
        passwordInput = findViewById(R.id.sign_up_password_input)
        errorMessage = findViewById(R.id.sign_up_error_msg)
        signUpButton = findViewById(R.id.sign_up_submit_btn)

        usersRepo = UsersRepo.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Please wait until loading end.")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }

    private fun signUp() {
        signUpButton.setOnClickListener {
            when {
                loginInput.text.isBlank() -> errorMessage.text = getString(R.string.incorrect_login)
                loginInput.text.length < 6 -> errorMessage.text = getString(R.string.login_min_len)
                loginInput.text.length > 16 -> errorMessage.text = getString(R.string.login_max_len)
                loginInput.text.contains("/") -> errorMessage.text = getString(R.string.not_allowed_login)
                loginInput.text.contains("?") -> errorMessage.text = getString(R.string.not_allowed_login)
                loginInput.text.contains("&") -> errorMessage.text = getString(R.string.not_allowed_login)
                loginInput.text.contains("&") -> errorMessage.text = getString(R.string.not_allowed_login)
                usernameInput.text.isBlank() -> errorMessage.text = getString(R.string.incorrect_username)
                usernameInput.text.length < 6 -> errorMessage.text = getString(R.string.username_min_len)
                usernameInput.text.length > 16 -> errorMessage.text = getString(R.string.username_max_len)
                passwordInput.text.isBlank() -> errorMessage.text = getString(R.string.incorrect_password)
                passwordInput.text.length < 6 -> errorMessage.text = getString(R.string.password_min_len)
                passwordInput.text.length > 16 -> errorMessage.text = getString(R.string.password_max_ken)
                else -> {
                    errorMessage.text = ""
                    progressDialog.show()
                    val user = User(
                            loginInput.text.toString(),
                            usernameInput.text.toString(),
                            passwordInput.text.toString()
                    )
                    val r = Runnable {
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
                                            runOnUiThread {
                                                errorMessage.text = getString(R.string.taken_login)
                                                progressDialog.cancel()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        runOnUiThread {
                                            try {
                                                val isCreated = userJson.get("created").asBoolean
                                                if (isCreated) {
                                                    editorPreference.putBoolean("is_authenticated", true)
                                                    editorPreference.putString("login", loginInput.text.toString())
                                                    editorPreference.putString("username", usernameInput.text.toString())
                                                    editorPreference.commit()
                                                    CurrentUser.login = loginInput.text.toString()
                                                    CurrentUser.username = usernameInput.text.toString()
                                                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(intent)
                                                }
                                            } catch (e: Exception) {
                                                errorMessage.text = e.message
                                            }
                                            cancelProgressDialog()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                    cancelProgressDialog()
                                    println("ERROR: ${t.message}")
                                }
                            })
                    }
                    Thread(r).start()
                }
            }
        }
    }

    private fun cancelProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.cancel()
        }
    }
}
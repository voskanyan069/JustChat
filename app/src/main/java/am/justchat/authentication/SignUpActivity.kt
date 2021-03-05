package am.justchat.authentication

import am.justchat.R
import am.justchat.api.models.User
import am.justchat.api.repos.UsersRepo
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    private lateinit var loginInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var errorMessage: TextView
    private lateinit var signUpButton: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var usersRepo: UsersRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()
        signUp()
    }

    private fun init() {
        loginInput = findViewById(R.id.sign_up_login_input)
        usernameInput = findViewById(R.id.sign_up_username_input)
        passwordInput = findViewById(R.id.sign_up_password_input)
        errorMessage = findViewById(R.id.sign_up_error_msg)
        signUpButton = findViewById(R.id.sign_up_submit_btn)

        usersRepo = UsersRepo.getInstance()!!
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
                usernameInput.text.isBlank() -> errorMessage.text =
                    getString(R.string.incorrect_username)
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

                    val r = Runnable {
                        usersRepo.usersService!!
                            .addUser(user)
                            .enqueue(object : Callback<JsonObject> {
                                override fun onResponse(
                                    call: Call<JsonObject>,
                                    response: Response<JsonObject>
                                ) {
                                    try {
                                        val jsonParser = JsonParser()
                                        val userJsonStr = Gson().toJson(response.body())
                                        val userJson: JsonObject =
                                            jsonParser.parse(userJsonStr).asJsonObject
                                        val code: Int = userJson.get("code").asInt
                                        if (code == 2) {
                                            runOnUiThread {
                                                errorMessage.text = getString(R.string.taken_login)
                                                progressDialog.cancel()
                                            }
                                        } else {
                                            println("Account created | Signed in")
                                            progressDialog.cancel()
                                        }
                                    } catch (ignored: Exception) {
                                    } finally {
                                        cancelProgressDialog()
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
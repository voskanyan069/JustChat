package am.justchat.activities

import am.justchat.R
import am.justchat.authentication.SignUpFragment
import am.justchat.fragments.SwitchFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        SwitchFragment.switch(this, SignUpFragment(), R.id.auth_fragment_container)
    }
}
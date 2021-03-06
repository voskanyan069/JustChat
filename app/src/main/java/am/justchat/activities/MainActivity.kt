package am.justchat.activities

import am.justchat.R
import am.justchat.authentication.SignUpActivity
import am.justchat.storage.SharedPreference
import am.justchat.ui.main.CallsFragment
import am.justchat.ui.main.ChatsFragment
import am.justchat.ui.main.ContactsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import am.justchat.ui.main.SettingsFragment
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAuthenticated()
        if (savedInstanceState == null) {
            switchFragment(ChatsFragment())
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
        println("isAuth: $isAuth")
        if (!isAuth) {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                var selectedFragment: Fragment? = null
                when (item.itemId) {
                    R.id.tab_chats -> {
                        selectedFragment = ChatsFragment()
                    }
                    R.id.tab_calls -> {
                        selectedFragment = CallsFragment()
                    }
                    R.id.tab_contacts -> {
                        selectedFragment = ContactsFragment()
                    }
                    R.id.tab_settings -> {
                        selectedFragment = SettingsFragment()
                    }
                }
                switchFragment(selectedFragment!!)
                true
            }

    private fun switchFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
}
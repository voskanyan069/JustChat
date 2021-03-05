package am.justchat.activities

import am.justchat.R
import am.justchat.authentication.SignUpActivity
import am.justchat.ui.main.CallsFragment
import am.justchat.ui.main.ChatsFragment
import am.justchat.ui.main.ContactsFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import am.justchat.ui.main.SettingsFragment
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)

        if (savedInstanceState == null) {
            switchFragment(ChatsFragment())
        }
        init()
    }

    private fun init() {
        bottomNavigationMenu = findViewById(R.id.bottom_navigation_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener(navListener)
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
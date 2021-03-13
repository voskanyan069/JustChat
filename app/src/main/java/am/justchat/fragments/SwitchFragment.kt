package am.justchat.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SwitchFragment {
    companion object {
        fun switch(activity: AppCompatActivity, fragment: Fragment, fragmentContainer: Int) = activity.supportFragmentManager.beginTransaction().replace(fragmentContainer, fragment).commit()
    }
}
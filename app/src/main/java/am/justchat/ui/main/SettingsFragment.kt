package am.justchat.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import am.justchat.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {
    private lateinit var notificationSwitcher: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        notificationSwitcher = root.findViewById(R.id.notification_enabled)
        switchNotificationsState()

        return root
    }

    private fun switchNotificationsState() = notificationSwitcher.setOnCheckedChangeListener { _, _ -> }
}
package com.example.a1440.ui.setting

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.a1440.ui.top.MainActivity

class SettingViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs by lazy {
        app.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveToggleState(isChecked: Boolean) {
        prefs.edit().putBoolean(KEY_TOGGLE, isChecked).apply()
    }

    fun getSavedToggleState() = prefs.getBoolean(
        KEY_TOGGLE,
        false
    )

    fun saveMinutes(minutes: Int) =
        prefs.edit().putInt(KEY_MINUTES, minutes).apply()

    fun getSavedMinutes() = prefs.getInt(
        KEY_MINUTES,
        MainActivity.DEFAULT_MINUTES
    )

    fun validate(number: String): Boolean {
        val notificationMinutes = number.toIntOrNull() ?: return false
        return notificationMinutes in 1..1440
    }

    companion object {
        const val PREFS_NAME = "setting_minutes"
        const val KEY_MINUTES = "minutes"
        const val KEY_TOGGLE = "toggle"
    }
}
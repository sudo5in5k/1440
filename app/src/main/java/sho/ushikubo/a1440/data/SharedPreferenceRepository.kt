package sho.ushikubo.a1440.data

import android.content.Context
import sho.ushikubo.a1440.ui.top.MainActivity

class SharedPreferenceRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val savedMinutes = prefs.getInt(KEY_MINUTES, MainActivity.DEFAULT_MINUTES)

    fun saveMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_MINUTES, minutes).apply()
    }

    val switchState = prefs.getBoolean(KEY_SWITCH, false)

    fun saveSwitchState(checked: Boolean) {
        prefs.edit().putBoolean(KEY_SWITCH, checked).apply()
    }

    companion object {
        private const val PREFS_NAME = "setting_minutes"
        private const val KEY_MINUTES = "minutes"
        private const val KEY_SWITCH = "toggle"
    }
}
package com.example.a1440.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
import com.example.a1440.ui.top.MainActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        notification_timer.text =
            Editable.Factory.getInstance().newEditable(getSavedMinutes().toString())

        save.setOnClickListener {
            if (validate()) {
                val intent = Intent()
                val minutes =
                    notification_timer.text.toString().toIntOrNull() ?: return@setOnClickListener
                saveMinutes(minutes)
                intent.putExtra(FROM_SETTING, minutes)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "1から1440までの数字を指定してください", Toast.LENGTH_LONG).show()
            }
        }

        toggle.setOnCheckedChangeListener { _, isChecked ->
            saveToggleState(isChecked)
            if (isChecked) {
                save.isEnabled = true
            } else {
                save.isEnabled = false
            }
        }
        toggle.isChecked = getSavedToggleState()
    }

    private fun saveToggleState(isChecked: Boolean) {
        prefs.edit().putBoolean(KEY_TOGGLE, isChecked).apply()
    }

    private fun getSavedToggleState() = prefs.getBoolean(
        KEY_TOGGLE,
        false
    )

    private fun saveMinutes(minutes: Int) =
        prefs.edit().putInt(KEY_MINUTES, minutes).apply()

    private fun getSavedMinutes() = prefs.getInt(
        KEY_MINUTES,
        MainActivity.DEFAULT_MINUTES
    )

    private fun validate(): Boolean {
        val notificationMinutes = notification_timer.text.toString().toIntOrNull() ?: return false
        return notificationMinutes in 1..1440
    }

    companion object {
        const val FROM_SETTING = "from_setting"
        const val PREFS_NAME = "setting_minutes"
        const val KEY_MINUTES = "minutes"
        const val KEY_TOGGLE = "toggle"
    }
}
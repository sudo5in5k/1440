package com.example.a1440.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences(
            MainActivity.PREFS_NAME,
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
                intent.putExtra(FROM_INTENT, minutes)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "1から1440までの数字を指定してください", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveMinutes(minutes: Int) =
        prefs.edit().putInt(MainActivity.KEY_MINUTES, minutes).apply()

    private fun getSavedMinutes() = prefs.getInt(
        MainActivity.KEY_MINUTES,
        MainActivity.DEFAULT_MINUTES
    )

    private fun validate(): Boolean {
        val notificationMinutes = notification_timer.text.toString().toIntOrNull() ?: return false
        return notificationMinutes in 1..1440
    }

    companion object {
        const val FROM_INTENT = "hoge"
    }

}
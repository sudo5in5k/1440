package com.example.a1440.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        save.setOnClickListener {
            if (validate()) {
                val intent = Intent()
                intent.putExtra(FROM_INTENT, notification_timer.text.toString().toIntOrNull())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun validate(): Boolean {
        val notificationMinutes = notification_timer.text.toString().toIntOrNull() ?: return false
        return notificationMinutes in 1..1440
    }

    companion object {
        const val FROM_INTENT = "hoge"
    }

}
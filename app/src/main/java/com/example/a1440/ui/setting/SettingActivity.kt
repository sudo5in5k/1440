package com.example.a1440.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a1440.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val settingViewModel: SettingViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(this.application)
            .create(SettingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        notification_timer.text =
            Editable.Factory.getInstance()
                .newEditable(settingViewModel.getSavedMinutes().toString())

        save.setOnClickListener {
            if (settingViewModel.validate(notification_timer.text.toString())) {
                val minutes =
                    notification_timer.text.toString().toIntOrNull() ?: return@setOnClickListener
                settingViewModel.saveMinutes(minutes)
                val intent = Intent().apply { putExtra(FROM_SETTING, minutes) }
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "1から1440までの数字を指定してください", Toast.LENGTH_LONG).show()
            }
        }

        toggle.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveToggleState(isChecked)
            if (isChecked) {
                save.isEnabled = true
            } else {
                save.isEnabled = false
                Toast.makeText(this, "通知設定を解除しました", Toast.LENGTH_SHORT).show()
                // TODO alarmManager.cancel
            }
        }
        toggle.isChecked = settingViewModel.getSavedToggleState()
    }

    companion object {
        const val FROM_SETTING = "from_setting"
    }
}
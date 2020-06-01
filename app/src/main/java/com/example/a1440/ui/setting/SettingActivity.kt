package com.example.a1440.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a1440.R
import com.example.a1440.ui.top.TopViewModel
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val settingViewModel: SettingViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(this.application)
            .create(SettingViewModel::class.java)
    }

    private val topViewModel: TopViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(this.application)
            .create(TopViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_header_setting)
        }

        notification_timer.text =
            Editable.Factory.getInstance()
                .newEditable(settingViewModel.getSavedMinutes().toString())

        save.setOnClickListener {
            closeKeyboard()
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
            settingViewModel.saveSwitchState(isChecked)
            if (isChecked) {
                save.isEnabled = true
                setting_timer_container.visibility = View.VISIBLE
            } else {
                save.isEnabled = false
                setting_timer_container.visibility = View.GONE
                topViewModel.cancelAlarmRepeat(settingViewModel.getSavedMinutes())
            }
        }
        toggle.isChecked = settingViewModel.getSwitchState()
    }

    private fun closeKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val FROM_SETTING = "from_setting"
    }
}
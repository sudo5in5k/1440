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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.a1440.R
import com.example.a1440.databinding.ActivitySettingBinding
import com.example.a1440.ui.top.TopViewModel
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.apply {
            viewModel = settingViewModel
            setLifecycleOwner(this@SettingActivity)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_header_setting)
        }

        observeValues()
    }

    private fun observeValues() {
        settingViewModel.apply {
            closeKeyboard.observe(this@SettingActivity) {
                if (it) {
                    closeKeyboard()
                }
            }

            backToMain.observe(this@SettingActivity) {
                val intent = Intent().apply { putExtra(FROM_SETTING, it) }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            showValidateError.observe(this@SettingActivity) {
                Toast.makeText(this@SettingActivity, "1から1440までの数字を指定してください", Toast.LENGTH_LONG)
                    .show()
            }

            cancelAlarm.observe(this@SettingActivity) {
                topViewModel.cancelAlarmRepeat(it)
            }
        }
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
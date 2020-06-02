package sho.ushikubo.a1440.ui.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import sho.ushikubo.a1440.data.SharedPreferenceRepository

class SettingViewModel(app: Application) : AndroidViewModel(app) {

    private val repository =
        SharedPreferenceRepository(app.applicationContext)

    val currentSettingMinutes = MutableLiveData<String>(repository.savedMinutes.toString())

    val checked = MutableLiveData<Boolean>(repository.switchState)

    val timerContainerVisibility = checked.map {
        it
    }

    private val _cancelAlarm = MutableLiveData<Int>()
    val cancelAlarm: LiveData<Int>
        get() = _cancelAlarm

    private val _closeKeyBoard = MutableLiveData<Boolean>()
    val closeKeyboard: LiveData<Boolean>
        get() = _closeKeyBoard

    private val _backToMain = MutableLiveData<Int>()
    val backToMain: LiveData<Int>
        get() = _backToMain

    private val _showValidateError = MutableLiveData<Boolean>()
    val showValidateError: LiveData<Boolean>
        get() = _showValidateError

    private fun validate(minutesString: String?): Boolean {
        minutesString ?: return false
        val minutesInt = kotlin.runCatching { minutesString.toIntOrNull() }.getOrNull() ?: false
        return minutesInt in 1..1440
    }

    fun onSwitchStatusChanged() {
        val checked = checked.value ?: false
        repository.saveSwitchState(checked)
        if (!checked) {
            _cancelAlarm.value = repository.savedMinutes
        }
    }

    fun onSaveClick() {
        closeKeyboard()
        val currentMinutes = currentSettingMinutes.value
        if (validate(currentMinutes)) {
            val currentMinutesInt = currentMinutes?.toInt() ?: return
            repository.saveMinutes(currentMinutesInt)
            _backToMain.value = currentMinutesInt
        } else {
            _showValidateError.value = true
        }
    }

    private fun closeKeyboard() {
        _closeKeyBoard.value = true
    }
}
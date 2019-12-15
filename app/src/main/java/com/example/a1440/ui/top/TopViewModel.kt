package com.example.a1440.ui.top

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import java.util.concurrent.TimeUnit

class TopViewModel : ViewModel() {

    fun sendEventLog(firebaseAnalytics: FirebaseAnalytics) {
        val bundle = Bundle().apply { putString("Device_OS", "${Build.VERSION.SDK_INT}") }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    /**
     * 翌日の00:00:00を取得する
     */
    private fun getTomorrowCal(deviceCal: Calendar) = deviceCal.apply {
        add(Calendar.DATE, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    /**
     * 翌日00:00:00と現在時刻との分数差を取得
     */
    fun getDiffMinutes() =
        (TimeUnit.MILLISECONDS.toMinutes(getTomorrowCal(Calendar.getInstance()).timeInMillis - Calendar.getInstance().timeInMillis) + 1).toInt()

    /**
     * PUSHにて設定するTriggerAtTimeを取得
     */
    fun getNotificationTriggerAtTime(deviceCal: Calendar, minutes: Int): Long {
        val settingMinutesToMills = TimeUnit.MINUTES.toMillis(minutes.toLong())
        val remainingTimeMills = getTomorrowCal(deviceCal).timeInMillis - settingMinutesToMills
        // 残り分数が通知設定分数よりも少ない場合は即発火を防ぐため1日ずらす
        return if (getDiffMinutes() < minutes) {
            remainingTimeMills + TimeUnit.DAYS.toMillis(1)
        } else {
            remainingTimeMills
        }
    }
}
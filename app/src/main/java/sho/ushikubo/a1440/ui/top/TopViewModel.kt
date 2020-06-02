package sho.ushikubo.a1440.ui.top

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import sho.ushikubo.a1440.broadcast.RemindBroadcastReceiver
import sho.ushikubo.a1440.ui.setting.SettingActivity
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import java.util.concurrent.TimeUnit

class TopViewModel(private val app: Application) : AndroidViewModel(app) {

    private val alarmManager =
        app.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

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
    private fun getNotificationTriggerAtTime(deviceCal: Calendar, minutes: Int): Long {
        val settingMinutesToMills = TimeUnit.MINUTES.toMillis(minutes.toLong())
        val remainingTimeMills = getTomorrowCal(deviceCal).timeInMillis - settingMinutesToMills
        // 残り分数が通知設定分数よりも少ない場合は即発火を防ぐため1日ずらす
        return if (getDiffMinutes() < minutes) {
            remainingTimeMills + TimeUnit.DAYS.toMillis(1)
        } else {
            remainingTimeMills
        }
    }

    fun getMinutesFromIntent(intent: Intent?): Int? {
        return intent?.getIntExtra(
            SettingActivity.FROM_SETTING,
            MainActivity.DEFAULT_MINUTES
        )
    }

    private fun createBroadCastIntent(minutes: Int) =
        Intent(app.applicationContext, RemindBroadcastReceiver::class.java).apply {
            putExtra("minutes", minutes)
            action = RemindBroadcastReceiver.ACTION_TAG
        }

    private fun createBroadCastPendingIntent(minutes: Int): PendingIntent =
        PendingIntent.getBroadcast(
            app.applicationContext, -1, createBroadCastIntent(minutes),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

    fun setAlarmRepeat(minutes: Int) {
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getNotificationTriggerAtTime(Calendar.getInstance(), minutes),
            AlarmManager.INTERVAL_DAY, createBroadCastPendingIntent(minutes)
        )
    }

    fun cancelAlarmRepeat(minutes: Int) {
        alarmManager?.cancel(createBroadCastPendingIntent(minutes))
        Toast.makeText(app.baseContext, "アラームをキャンセルしました", Toast.LENGTH_SHORT).show()
    }
}
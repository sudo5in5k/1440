package com.example.a1440.ui

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
import com.example.a1440.service.RemindService
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sendEventLog()

        Observable.interval(1, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val newCal = getTomorrowCal(Calendar.getInstance())
                val diff =
                    (TimeUnit.MILLISECONDS.toMinutes(newCal.timeInMillis - Calendar.getInstance().timeInMillis) + 1).toInt()
                timer.text = diff.toString()
            }.addTo(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun sendEventLog() {
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
     * PUSHにて設定した残り時間を取得
     */
    private fun getNotificationTriggerAtTime(deviceCal: Calendar, minutes: Int) =
        getTomorrowCal(deviceCal).timeInMillis - TimeUnit.MINUTES.toMillis(minutes.toLong())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val minutes = data?.getIntExtra(SettingActivity.FROM_SETTING, DEFAULT_MINUTES) ?: return

            when (requestCode) {
                REQUEST_CODE -> {
                    val intent = Intent(applicationContext, RemindService::class.java).apply {
                        putExtra("minutes", minutes)
                    }
                    val pendingIntent = PendingIntent.getService(
                        applicationContext, -1, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
                    alarmManager?.setInexactRepeating(
                        AlarmManager.RTC,
                        getNotificationTriggerAtTime(Calendar.getInstance(), minutes),
                        AlarmManager.INTERVAL_DAY, pendingIntent
                    )
                    Toast.makeText(this, "残り${minutes}分で設定しました", Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
                true
            }
            else -> true
        }
    }

    companion object {
        const val REQUEST_CODE = 111
        const val DEFAULT_MINUTES = 1440
    }
}

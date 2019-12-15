package com.example.a1440.ui.top

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a1440.R
import com.example.a1440.broadcast.RemindBroadcastReceiver
import com.example.a1440.ui.setting.SettingActivity
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
    private val topViewModel: TopViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(this.application).create(
            TopViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        topViewModel.sendEventLog(firebaseAnalytics)

        Observable.interval(1, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                timer.text = topViewModel.getDiffMinutes().toString()
            }.addTo(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val minutes = topViewModel.getMinutesFromIntent(data) ?: return
            when (requestCode) {
                REQUEST_CODE_TO_SETTING -> {
                    topViewModel.setAlarmRepeat(minutes)
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
                startActivityForResult(
                    intent,
                    REQUEST_CODE_TO_SETTING
                )
                true
            }
            else -> true
        }
    }

    companion object {
        const val REQUEST_CODE_TO_SETTING = 111
        const val DEFAULT_MINUTES = 1440
    }
}

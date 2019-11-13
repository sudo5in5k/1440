package com.example.a1440.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
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

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sendEventLog()
        verifyToken()

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
        val bundle = Bundle().apply { putString("TEST", "onCreate is called") }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    private fun verifyToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
            OnCompleteListener {
                if (!it.isSuccessful) {
                    Log.d("ushi", "getInstanceId failed: ${it.exception}")
                    return@OnCompleteListener
                }
                val token = it.result?.token ?: return@OnCompleteListener
                Toast.makeText(this, token, Toast.LENGTH_LONG).show()
            }
        )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val minutes = data?.getIntExtra(SettingActivity.FROM_INTENT, 0) ?: return
            when (requestCode) {
                REQUEST_CODE -> Log.d("debug", "minutes: $minutes")
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
    }
}

package com.example.a1440.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a1440.R
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

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.interval(1, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val newCal = getTomorrowCal(Calendar.getInstance())
                val diff =
                    TimeUnit.MILLISECONDS.toMinutes(newCal.timeInMillis - Calendar.getInstance().timeInMillis).toInt()
                timer.text = diff.toString()
            }.addTo(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    /**
     * 翌日の00:00:00を取得する
     */
    private fun getTomorrowCal(deviceCal: Calendar) = deviceCal.apply {
        add(Calendar.DATE, 1)
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

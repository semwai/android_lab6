package ru.semwai.android_lab6

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MainActivityExecutionService : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var backgroundThread: Future<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        textSecondsElapsed.text = getString(R.string.text, secondsElapsed)
    }

    override fun onStart() {
        Log.v(THREAD_STATE, "started")
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        startTime = System.currentTimeMillis()
        backgroundThread = (applicationContext as ExecutorClass).executor.submit {
            while (!backgroundThread.isCancelled) {
                Thread.sleep(1000)
                Log.v(THREAD_STATE, "running")
                textSecondsElapsed.post {
                    textSecondsElapsed.text = getString(
                        R.string.text,
                        secondsElapsed + ((System.currentTimeMillis() - startTime) / 1000)
                    )
                }
            }

        }
        super.onStart()
    }

    override fun onStop() {
        Log.v(THREAD_STATE, "stopped, threads: " + Thread.getAllStackTraces().size)
        backgroundThread.cancel(true)
        endTime = System.currentTimeMillis()
        secondsElapsed += ((endTime - startTime) / 1000).toInt()
        with(sharedPrefs.edit()) {
            putInt(SECONDS, secondsElapsed)
            apply()
        }
        super.onStop()
    }

    companion object {
        const val SECONDS = "Seconds elapsed"
        const val THREAD_STATE = "Thread (Execution service)"
    }

    class ExecutorClass : Application() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
    }

}
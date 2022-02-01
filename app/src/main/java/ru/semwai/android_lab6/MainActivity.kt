package ru.semwai.android_lab6

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private var startTime: Long = 0
    private var endTime: Long = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var backgroundThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        textSecondsElapsed.text = getString(R.string.text, secondsElapsed)
    }

    override fun onStart() {
        Log.v(THREAD_STATE, "onStart")
        secondsElapsed = sharedPrefs.getInt(SECONDS, 0)
        startTime = System.currentTimeMillis()
        backgroundThread = Thread {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    Thread.sleep(1000)
                    Log.v(THREAD_STATE, "running")
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = getString(
                            R.string.text,
                            secondsElapsed + ((System.currentTimeMillis() - startTime) / 1000)
                        )
                    }
                }
            } catch (e: InterruptedException) {
                Log.v(THREAD_STATE, "stoped")
            }
        }
        backgroundThread.start()
        super.onStart()
    }

    override fun onStop() {
        Log.v(THREAD_STATE, "onStop, threads: " + Thread.getAllStackTraces().size)
        backgroundThread.interrupt()
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
        const val THREAD_STATE = "Thread"
    }

}
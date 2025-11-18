package edu.temple.myapplication

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log

@Suppress("ControlFlowWithEmptyBody")
class TimerService : Service() {

    private var isRunning = false

    private var timerHandler : Handler? = null

    lateinit var t: TimerThread

    var paused = false

    inner class TimerBinder : Binder() {

        val isRunning: Boolean
            get() = this@TimerService.isRunning

        val paused: Boolean
            get() = this@TimerService.paused

        fun start(startValue: Int){
            if (!isRunning) {
                this@TimerService.start(startValue)
            }
        }

        fun setHandler(handler: Handler) {
            timerHandler = handler
        }

        fun stop() {
            if (::t.isInitialized && t.isAlive) {
                t.interrupt()
            }
        }

        fun pause() {
            if(isRunning) {
                this@TimerService.pause()
            }
        }

    }

    override fun onCreate() {
        super.onCreate()

        Log.d("TimerService status", "Created")
    }

    override fun onBind(intent: Intent): IBinder {
        return TimerBinder()
    }

    private fun start(startValue: Int) {
        t = TimerThread(startValue)
        t.start()
    }

    private fun pause () {
        paused = !paused
    }

    inner class TimerThread(private val startValue: Int) : Thread() {

        override fun run() {
            try {
                isRunning = true
                paused = false
                for (i in startValue downTo 1)  {
                    Log.d("Countdown", i.toString())

                    timerHandler?.sendEmptyMessage(i)

                    while (paused) {
                        sleep(50)
                    }
                    sleep(1000)

                }
            } catch (e: InterruptedException) {
                Log.d("Timer interrupted", e.toString())
            } finally {
                isRunning = false
                paused = false
            }
        }

    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (::t.isInitialized && t.isAlive) {
            t.interrupt()
        }

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("TimerService status", "Destroyed")
    }


}
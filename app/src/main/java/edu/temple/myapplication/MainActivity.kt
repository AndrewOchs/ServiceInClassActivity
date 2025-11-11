package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.Service
import java.security.Provider
import java.util.logging.Handler
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {


//create handler and show countdown in activity step 2
    lateinit var TimeBinder: TimerService.TimerBinder
    var isConnected = false

    val serviceConnection = object : ServiceConnection{
        override
        fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            TimeBinder = p1 as TimerService.TimerBinder
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.textView)



        //bind
        var intent = Intent(this, TimerService::class.java)
        bindService(intent,serviceConnection,BIND_AUTO_CREATE)

        //start, pause, and unpause
        findViewById<Button>(R.id.startButton).setOnClickListener {
            if(!TimeBinder.isRunning && isConnected){
                findViewById<Button>(R.id.startButton).text = "pause"
                TimeBinder.start(1000)
            }else if(TimeBinder.paused && isConnected){
                findViewById<Button>(R.id.startButton).text = "pause"
                TimeBinder.pause()
            }else if(!TimeBinder.paused && isConnected){
                findViewById<Button>(R.id.startButton).text = "unpause"
                TimeBinder.pause()
            }
            //if not started then start
                //change text to pause
            //else if paused
                //change text to unpause
            //else if unpaused
                //change text to pause


        }
        //stop
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            //change start text to start
            findViewById<Button>(R.id.startButton).text = "start"
            TimeBinder.stop()
        }
    }

    override fun onDestroy() {
        //unbind
        unbindService(serviceConnection)
        super.onDestroy()
    }
}
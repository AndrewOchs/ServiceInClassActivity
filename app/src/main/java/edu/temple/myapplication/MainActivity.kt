package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.Service
import java.security.Provider
import java.util.logging.Handler
import java.util.logging.LogRecord
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {


//create handler and show countdown in activity step 2
    lateinit var TimeBinder: TimerService.TimerBinder
    var isConnected = false
    private var menu: Menu? = null

//    val timerHandler = Handler(Looper.getMainLooper()){
//
//        true
//    }

    private val handler = object : android.os.Handler() {


        override fun handleMessage(msg: Message) {
            findViewById<TextView>(R.id.textView).text = msg.what.toString()
        }

    }


    val serviceConnection = object : ServiceConnection{
        override
        fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            TimeBinder = p1 as TimerService.TimerBinder
            TimeBinder.setHandler(handler)
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val text = findViewById<TextView>(R.id.textView)
        when (item.itemId) {
            R.id.action_start_pause -> {
                if(isConnected) {
                    if (TimeBinder.isRunning && !TimeBinder.paused) {
                        item.title = "Start"
                        item.setIcon(R.drawable.ic_play)
                        TimeBinder.pause()
                    } else {
                        item.title = "Pause"
                        item.setIcon(R.drawable.ic_pause)
                        if (!TimeBinder.isRunning) {
                            TimeBinder.start(1000)
                        } else {
                            TimeBinder.pause()
                        }
                    }
                }
                return true
            }
            R.id.action_stop -> {
                if(isConnected) {
                    text.text = "0"
                    TimeBinder.stop()
                    val startPauseItem = menu?.findItem(R.id.action_start_pause)
                    startPauseItem?.title = "Start"
                    startPauseItem?.setIcon(R.drawable.ic_play)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        //unbind
        unbindService(serviceConnection)

        super.onDestroy()
    }
}
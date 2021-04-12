package com.evitalz.homevitalz.heartskan.ui.activities.manualentry

import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.evitalz.homevitalz.heartskan.AlarmHandler
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.Utility.alarmdateformat
import com.evitalz.homevitalz.heartskan.Utility.alarmtimeformat
import com.evitalz.homevitalz.heartskan.databinding.ActivityStartAlarmBinding
import java.util.*

class StartAlarmActivity : AppCompatActivity(), AlarmHandler {

    lateinit var binding : ActivityStartAlarmBinding
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var wakeLock:PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler = this

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)

        binding.tvalarmdate.text = alarmdateformat.format(Calendar.getInstance().time.time)
        binding.tvalarmtime.text = alarmtimeformat.format(Calendar.getInstance().time.time)

        mediaPlayer = MediaPlayer.create(this, R.raw.audio)
        mediaPlayer.start()


    }

    override fun onDismissClicked(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
//        wakeLock.release()
    }
}
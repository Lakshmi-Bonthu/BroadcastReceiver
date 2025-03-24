package com.example.broadcastreceiver.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import java.io.File

class MediaPlaybackService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filePath = intent?.getStringExtra("filePath")
        if (filePath != null) {
            playAudio(filePath)
        }
        return START_NOT_STICKY
    }

    private fun playAudio(filePath: String) {
        mediaPlayer?.release() // Release any existing media player
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // This is not a bound service
    }
}

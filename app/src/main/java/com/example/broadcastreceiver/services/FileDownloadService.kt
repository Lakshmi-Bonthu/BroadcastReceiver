package com.example.broadcastreceiver.services

import android.app.Service


import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileDownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filePath = intent?.getStringExtra("filePath")
        if (filePath != null) {
            Thread {
                downloadFile(filePath)
            }.start()
        }
        return START_NOT_STICKY
    }

    private fun downloadFile(filePath: String) {
        try {
            val file = File(filePath)
            val destination = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file.name)
            FileInputStream(file).use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("FileDownloadService", "File downloaded to: ${destination.absolutePath}")
        } catch (e: Exception) {
            Log.e("FileDownloadService", "Error downloading file: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // This is not a bound service
    }
}

package com.example.startedservicecompose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MyService : Service() {

    private lateinit var musicPlayer: MediaPlayer
    private var isPlaying: Boolean = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        musicPlayer = MediaPlayer.create(this, R.raw.vikram_rolex_bgm)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, getNotification())
        Toast.makeText(this, "Service created...", Toast.LENGTH_SHORT).show()

        intent?.let {
            when (it.action) {
                "ACTION_START" -> {
                    playSong()
                }

                "ACTION_STOP" -> {
                    pauseSong()
                }
            }
        }
        return START_STICKY
    }

    private fun playSong() {
        if (!isPlaying) {
            musicPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseSong() {
        if (isPlaying) {
            musicPlayer.stop()
            isPlaying = false
        }
        stopSelf()
    }

    private fun getNotification(): Notification {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "notification_channel")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Service running")
            .setContentText("Music is playing in background")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val descriptionText = "Notification channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notification_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
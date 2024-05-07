package de.tech41.tones.vocalstar

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
class VService: Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = VServiceBinder()

    inner class VServiceBinder : Binder() {
        fun getService(): VService = this@VService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        android.os.Debug.waitForDebugger();
        // mediaPlayer = MediaPlayer.create(this, R.raw.your_music_file) // Replace with your music file
       // mediaPlayer.isLooping = true
    }

    fun startMusic() {

    }

    fun stopMusic() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
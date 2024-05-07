package de.tech41.tones.vocalstar

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.Service.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi


class VService: Service() {
    private val AUDIO_EFFECT_REQUEST = 0
    private var AUDIO_RECORD_REQUEST_CODE = 300
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = VServiceBinder()
    private val TAG: String = MainActivity::class.java.name
    private lateinit var viewModel: Model
    lateinit var audioManager: AudioManager
    var isPlaying = false
    @JvmField
    var CHANNEL_ID = "Vocalstar"

    inner class VServiceBinder : Binder() {
        fun getService(): VService = this@VService
    }

    fun getLatency() :Float{
        return 0.2f
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        android.os.Debug.waitForDebugger();
        LiveEffectEngine.create()
        LiveEffectEngine.setRecordingDeviceId(getRecordingDeviceId())
        LiveEffectEngine.setPlaybackDeviceId(getPlaybackDeviceId())
        mAAudioRecommended = LiveEffectEngine.isAAudioRecommended()
        EnableAudioApiUI(true)
        LiveEffectEngine.setAPI(apiSelection)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            Toast.makeText(this, "Android Audio service starting", Toast.LENGTH_SHORT).show()
            val notification = NotificationCompat.Builder(this, CHANNEL_ID).build()
            ServiceCompat.startForeground(
                this,
                100,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                } else {
                    0
                },
            )
        }catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e(TAG,"Fatal - cant start Vocalstar Service")
            }
        }
        return START_STICKY;
    }
    override fun onDestroy() {
        super.onDestroy()
        stopEffect()
        LiveEffectEngine.delete()
    }

    private fun getRecordingDeviceId(): Int {
        return 2 //(recordingDeviceSpinner.getSelectedItem() as AudioDeviceListEntry).getId()
    }

    private fun getPlaybackDeviceId(): Int {
        return 701 //(playbackDeviceSpinner.getSelectedItem() as AudioDeviceListEntry).getId()
    }
    private fun isRecordPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    private fun startEffect() {
        val success = LiveEffectEngine.setEffectOn(true)
        if (success) {
            //statusText.setText(R.string.status_playing)
            // toggleEffectButton.setText(R.string.stop_effect)
            isPlaying = true
            EnableAudioApiUI(false)
        } else {
            // statusText.setText(R.string.status_open_failed)
            isPlaying = false
        }
    }

    @OptIn(UnstableApi::class)
    private fun stopEffect() {
        Log.d(TAG, "Playing, attempting to stop")
        LiveEffectEngine.setEffectOn(false)
        // resetStatusView()
        // toggleEffectButton.setText(R.string.start_effect)
        isPlaying = false
        EnableAudioApiUI(true)
    }

    companion object {
        init {
            System.loadLibrary("vocalstar")
        }
    }
}
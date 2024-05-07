package de.tech41.tones.vocalstar

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.app.Service.*
import android.content.Context
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
import android.media.AudioDeviceInfo

class VService: Service() {
    private val OBOE_API_AAUDIO = 0
    private val OBOE_API_OPENSL_ES = 1
    private val AUDIO_EFFECT_REQUEST = 0
    private var AUDIO_RECORD_REQUEST_CODE = 300
    private lateinit var mediaPlayer: MediaPlayer
    private val binder = VServiceBinder()
    private val TAG: String = MainActivity::class.java.name
    private lateinit var viewModel: Model
    lateinit var audioManager: AudioManager
    internal var mAAudioRecommended = true
    var apiSelection: Int = OBOE_API_AAUDIO
    var isPlaying = false

    val deviceIdIn = 5
    val deviceIdOut = 0

    inner class VServiceBinder : Binder() {
        fun getService(): VService = this@VService
    }


    fun getLatency() :Float{
        return 0.2f
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun EnableAudioApiUI(enable: Boolean) {
        if (apiSelection == OBOE_API_AAUDIO && !mAAudioRecommended) {
            apiSelection = OBOE_API_OPENSL_ES
        }
    }
    override fun onCreate() {
        super.onCreate()
        var audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        android.os.Debug.waitForDebugger();
        LiveEffectEngine.create()
        LiveEffectEngine.setRecordingDeviceId(getRecordingDeviceId())
        LiveEffectEngine.setPlaybackDeviceId(getPlaybackDeviceId())
        mAAudioRecommended = LiveEffectEngine.isAAudioRecommended()
        EnableAudioApiUI(true)
        LiveEffectEngine.setAPI(apiSelection)
        EnableAudioApiUI(false)
    }

    @OptIn(UnstableApi::class)
    fun getDevices(devices: Array<AudioDeviceInfo>){
        for (device in devices) {
            var str = getInfoString(device)
            Log.d(TAG, str)

            var typestr = ""
            when(device.type){
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> typestr = "Speaker"
                AudioDeviceInfo.TYPE_USB_DEVICE ->typestr = "USB"
                AudioDeviceInfo.TYPE_BLE_HEADSET-> typestr = "Headset"
                AudioDeviceInfo.TYPE_BUILTIN_EARPIECE-> typestr = "Earpiece"
                AudioDeviceInfo.TYPE_BUILTIN_MIC-> typestr = "Mic"
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES-> typestr = "Headphone"
                AudioDeviceInfo.TYPE_WIRED_HEADSET-> typestr = "Headset"
                AudioDeviceInfo.TYPE_TELEPHONY-> typestr = "Telephony"
                else -> { // Note the block
                    Log.d("device","not known Type " + device.type.toString())
                }
            }
            if (device.isSource && device.isSink){
                viewModel.devicesIn.add(Pair(device.id.toString(), device.getProductName().toString() + " " + typestr))
                Log.d(TAG, "IN ID " + device.id.toString() + " " + typestr)
            }else if(device.isSource) {
                viewModel.devicesIn.add(Pair(device.id.toString(), device.getProductName().toString() + " " + typestr))
                Log.d(TAG, "IN ID " + device.id.toString() + " " + typestr)
            } else if(device.isSink) {
                viewModel.devicesOut.add(Pair(device.id.toString(), device.getProductName().toString() + " " + typestr))
                Log.d(TAG, "OUT ID " + device.id.toString() + " " + typestr)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun initAudio(){
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRateStr = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        viewModel.sampleRate = sampleRateStr.toInt()
        val framesPerBurstStr = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        viewModel.framesPerBurst = framesPerBurstStr.toInt()
        var currentAudioMode = audioManager.ringerMode
        viewModel.devicesIn.clear()
        viewModel.devicesOut.clear()
        val devicesIn = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
        getDevices(devicesIn)
        val devicesOut = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        getDevices(devicesOut)
    }
    fun startAudio(viewModel : Model){
        this.viewModel = viewModel
        initAudio()
        LiveEffectEngine.setRecordingDeviceId(getRecordingDeviceId())
        LiveEffectEngine.setPlaybackDeviceId(getPlaybackDeviceId())
        LiveEffectEngine.setDefaults(viewModel.sampleRate, viewModel.framesPerBurst)
       // Thread(Runnable { LiveEffectEngine.setEffectOn(true) }).start()
       LiveEffectEngine.setEffectOn(true)
    }

    fun getInfoString(adi :AudioDeviceInfo ):String{
            var sb =  StringBuilder();
            sb.append("Id: ");
            sb.append(adi.getId());

            sb.append("\nProduct name: ");
            sb.append(adi.getProductName());

            sb.append("\nType: ");
            sb.append(adi.getType());

            sb.append("\nIs source: ");
            sb.append((if (adi.isSource())  "Yes" else "No"));

            sb.append("\nIs sink: ");
            sb.append((if (adi.isSink())  "Yes" else "No"));

            sb.append("\nChannel counts: ");
            var channelCounts = adi.getChannelCounts();
            sb.append(channelCounts);

            sb.append("\nChannel masks: ");
            var channelMasks = adi.getChannelMasks();
            sb.append(channelMasks);

            sb.append("\nChannel index masks: ");
            var channelIndexMasks = adi.getChannelIndexMasks();
            sb.append(channelIndexMasks);

            sb.append("\nEncodings: ");
            var encodings = adi.getEncodings();
            sb.append(encodings);

            sb.append("\nSample Rates: ");
            var sampleRates = adi.getSampleRates();
            sb.append(sampleRates);
            return sb.toString()
    }

    fun stopAudio(){
        LiveEffectEngine.setEffectOn(false)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            Toast.makeText(this, "Android Audio service starting", Toast.LENGTH_SHORT).show()
            val notification = NotificationCompat.Builder(this, "Vocalstar").build()
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
        return 2 //2 //(recordingDeviceSpinner.getSelectedItem() as AudioDeviceListEntry).getId()
    }

    private fun getPlaybackDeviceId(): Int {
        return 701 // 701 //(playbackDeviceSpinner.getSelectedItem() as AudioDeviceListEntry).getId()
    }
    private fun isRecordPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    @OptIn(UnstableApi::class)
    private fun stopEffect() {
        Log.d(TAG, "Playing, attempting to stop")
        LiveEffectEngine.setEffectOn(false)
        isPlaying = false
        EnableAudioApiUI(true)
    }

    companion object {
        init {
            System.loadLibrary("vocalstar")
        }
    }
}
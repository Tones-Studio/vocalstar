package de.tech41.tones.vocalstar.player

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
import androidx.media3.exoplayer.ExoPlayer
import de.tech41.tones.vocalstar.IPlayer
import de.tech41.tones.vocalstar.LiveEffectEngine
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.PLAYER
import de.tech41.tones.vocalstar.getOutDevice


class FilePlayer @OptIn(UnstableApi::class)
constructor(context:Context, viewModel: Model) : IPlayer {
    val context : Context = context
    val mediaPlayer = ExoPlayer.Builder(context).build()
    var tag = "de.tech41.tones.vocalstar.FilePlayer2"

    val viewModel : Model = viewModel
    var isPlayerInit = false

    private val mAudioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    override fun updatePosition(){
        if(!isPlayerInit){
            return
        }
        var sec : Float = (mediaPlayer?.currentPosition!!.toFloat()) / 1000.0f
        viewModel.positionPercent = sec * 100.0f / viewModel.duration
        viewModel.position = sec
        Log.d(tag, viewModel.positionPercent.toString() )
    }

    init{
        mediaPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        Log.d(tag, "isPlaying")
                    } else {
                        Log.e(tag, mediaPlayer.playerError.toString())
                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.playWhenReady,
                        // player.playbackState, player.playbackSuppressionReason and
                        // player.playerError for details.
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause
                    if (cause is HttpDataSourceException) {
                        Log.e(tag, error.toString()) // An HTTP error occurred.
                        val httpError = cause
                        // It's possible to find out more about the error both by casting and by querying
                        // the cause.
                        if (httpError is InvalidResponseCodeException) {
                            Log.e(tag,  error.toString())
                        } else {
                            Log.e(tag,  error.toString())
                        }
                    }
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

                }
            }
        )
    }
    override fun setVolume(vol:Float) {
        if(!isPlayerInit){
            return
        }
        mediaPlayer.volume = vol
    }

    override fun isPlaying():Boolean{
        if(!isPlayerInit){
            return false
        }
        return mediaPlayer.isPlaying
    }

    @OptIn(UnstableApi::class)
    override fun setup(){
        setUri(viewModel.playerUri!!)

        Log.d(tag, "Duration " + viewModel.duration.toString())
        isPlayerInit = true
    }
    override fun play() {
        if(!isPlayerInit){
            return
        }
        viewModel.duration = getDuration()
        mediaPlayer.play()
    }
    override fun pause() {
        if(!isPlayerInit){
            return
        }
        mediaPlayer.pause()
    }

    override fun stop() {
        if(!isPlayerInit){
            return
        }
        mediaPlayer.pause()
    }

    override fun back() {
        if(!isPlayerInit){
            return
        }
        mediaPlayer.seekTo(0)
    }

    override fun forward() {
        if(!isPlayerInit){
            return
        }
    }

    override fun setPosition(percent: Float) {
        if(!isPlayerInit){
            return
        }
        mediaPlayer.seekTo((getDuration() * percent * 10.0f).toLong())
    }

    override fun getDuration(): Float {
        if(!isPlayerInit){
            return 0f
        }
        val f: Float = mediaPlayer.duration.toFloat()
        return  f / 1000.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.FILE
    }

    override fun release(){
        mediaPlayer.release()
    }

    override fun setUri(url: Uri){
        viewModel.playerUri = url
        val mediaItem = MediaItem.fromUri(url)
        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()

        // get duration - this might fail
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context,url)
       try {
           var r = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
           if(r!=null) {
               viewModel.duration = r!!.toFloat() / 1000f
           }else{

           }
       }catch(e:Exception){
           viewModel.duration = 0f
       }
        mmr.release()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(UnstableApi::class)
    override fun setSpeaker(){
        var devices =  mAudioManager.getDevices ( AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices){
            if( device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER){
                mediaPlayer.setPreferredAudioDevice(device)
                LiveEffectEngine.setPlaybackDeviceId(device.id)
                LiveEffectEngine.setEffectOn(false)
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun setHeadphone(){
        var device =  getOutDevice( viewModel, viewModel.deviceOutSelected)
        LiveEffectEngine.setPlaybackDeviceId(device.first.toInt())
        LiveEffectEngine.setEffectOn(true)
        var devices =  mAudioManager.getDevices ( AudioManager.GET_DEVICES_OUTPUTS)
        for (d in devices){
            if( d.id == device.first.toInt()){
                mediaPlayer.setPreferredAudioDevice(d)
            }
        }
    }
}
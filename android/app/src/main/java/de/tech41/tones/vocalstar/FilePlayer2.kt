package de.tech41.tones.vocalstar

import android.content.Context
import android.media.MediaMetadataRetriever

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
import androidx.media3.exoplayer.ExoPlayer

class FilePlayer2 @OptIn(UnstableApi::class)
constructor(context:Context, viewModel: Model) : IPlayer{

    val mediaPlayer = ExoPlayer.Builder(context).build()
    var tag = "de.tech41.tones.vocalstar.FilePlayer2"
    val context : Context = context
    val viewModel : Model = viewModel
    private var lastPosition : Long = 0

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
            }
        )
        mediaPlayer.setForegroundMode(true)
    }
    override fun setVolume(vol:Float) {
        mediaPlayer.volume = vol
    }
    override fun setup(){
        val mediaItem = MediaItem.fromUri(viewModel.playerUri!!)
        mediaPlayer.prepare()
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(viewModel.playerUri?.path)
        viewModel.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toFloat() / 1000f
        mmr.release()
        Log.d(tag, "Duration " + viewModel.duration.toString())

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (true) {
                        sleep(500)
                        if (viewModel.duration > 0  && mediaPlayer.isPlaying) {
                            var sec : Float = (mediaPlayer.currentPosition.toFloat()) / 1000.0f
                            viewModel.positionPercent = sec * 100.0f / viewModel.duration
                            viewModel.position = sec
                            Log.d(tag, viewModel.positionPercent.toString() )
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }
    override fun play() {
        viewModel.duration = getDuration()
        mediaPlayer.play()
    }
    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        lastPosition = mediaPlayer.contentPosition
        mediaPlayer.pause()
    }

    override fun back() {
        mediaPlayer.seekTo(0)
        lastPosition = 0
    }

    override fun forward() {

    }

    override fun setPosition(percent: Float) {
        mediaPlayer.seekTo((getDuration() * percent * 10.0f).toLong())
    }

    override fun getDuration(): Float {
        val f: Float = mediaPlayer.duration  as Float
        return  f / 1000.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.FILE
    }
}
package de.tech41.tones.vocalstar

import android.R
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.FileOutputStream
import java.io.InputStream

class FilePlayer  constructor(context:Context,  viewModel: Model) : IPlayer{

    var TAG = "de.tech41.tones.vocalstar.FilePlayer"
    val context : Context = context
    val viewModel : Model = viewModel
    var  mediaPlayer : MediaPlayer? = null
    var lastPosition : Int = 0
    override fun setVolume(vol:Float) {
        mediaPlayer?.setVolume(vol, vol)
    }

    override fun setup(){
        mediaPlayer = MediaPlayer.create(context, de.tech41.tones.vocalstar.R.raw.slow)
      // mediaPlayer?.prepare()
        viewModel.duration = 240f//getDuration()
        Log.d(TAG, "Duration " + viewModel.duration.toString())
    }
    override fun play() {
        if(mediaPlayer == null) {

            viewModel.duration = getDuration()
        }
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        lastPosition = mediaPlayer?.currentPosition!!
        mediaPlayer?.stop()
    }

    override fun back() {
        mediaPlayer?.seekTo(0)
        lastPosition = 0
    }

    override fun forward() {

    }

    override fun setPosition(percent: Float) {
        mediaPlayer?.seekTo((getDuration() * percent).toInt())
    }

    override fun getDuration(): Float {
        val f: Float = mediaPlayer?.duration!!  as Float
        return  f / 100.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.FILE
    }
}
package de.tech41.tones.vocalstar

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log

class FilePlayer  constructor(context:Context,  viewModel: Model) : IPlayer{

    var TAG = "de.tech41.tones.vocalstar.FilePlayer"
    val context : Context = context
    val viewModel : Model = viewModel
    var  mediaPlayer : MediaPlayer? = null
    var lastPosition : Int = 0
    override fun setVolume(vol:Float) {
        mediaPlayer?.setVolume(vol, vol)
    }

    override fun updatePosition(){
            var sec : Float = (mediaPlayer?.currentPosition!!.toFloat()) / 1000.0f
            viewModel.positionPercent = sec * 100.0f / viewModel.duration
            viewModel.position = sec
            Log.d(TAG, viewModel.positionPercent.toString() )
    }

    override fun setup(){
        mediaPlayer = MediaPlayer.create(context, viewModel.playerUri)
        mediaPlayer?.setOnCompletionListener {it:MediaPlayer ->
            viewModel.isPlaying = false
        }

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(viewModel.playerUri?.path)
        viewModel.duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toFloat() / 1000f
        mmr.release()

        Log.d(TAG, "Duration " + viewModel.duration.toString())

        /*
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (true) {
                        sleep(500)
                        if (viewModel.duration > 0  && mediaPlayer?.isPlaying!!) {
                            var sec : Float = (mediaPlayer?.currentPosition!!.toFloat()) / 1000.0f
                            viewModel.positionPercent = sec * 100.0f / viewModel.duration
                            viewModel.position = sec
                            Log.d(TAG, viewModel.positionPercent.toString() )
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()

         */
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
        mediaPlayer?.seekTo((getDuration() * percent * 10.0f).toInt())
    }

    override fun getDuration(): Float {
        val f: Float = mediaPlayer?.duration!!  as Float
        return  f / 1000.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.FILE
    }

    override fun release(){
        mediaPlayer?.release()
    }
}
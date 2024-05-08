package de.tech41.tones.vocalstar

import android.R
import android.content.Context
import android.media.MediaPlayer
import java.io.FileOutputStream
import java.io.InputStream


class FilePlayer  constructor(context:Context,  viewModel: Model) : IPlayer{

    val context : Context = context
    val viewModel : Model = viewModel
    var mediaPlayer : MediaPlayer? = null

    override fun play() {
        mediaPlayer = MediaPlayer.create(context, de.tech41.tones.vocalstar.R.raw.slow)
        mediaPlayer?.start() // no need to call prepare(); create() does that for you
    }

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun back() {
        TODO("Not yet implemented")
    }

    override fun forward() {

    }

    override fun setPosition(percent: Float) {

    }

    override fun getDuration(): Float {
        return 420.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.FILE
    }
}
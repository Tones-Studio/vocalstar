package de.tech41.tones.vocalstar.apple

import android.content.Context
import android.net.Uri
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.player.IPlayer
import de.tech41.tones.vocalstar.player.PLAYER

class ApplePlayer(context : Context, viewModel: Model) : IPlayer {

    val context : Context = context
    var viewModel : Model = viewModel

    override fun setup(){

    }
    override fun play() {

    }

    override fun setVolume(vol:Float){

    }

    override fun stop() {

    }

    override fun pause() {

    }

    override fun setSpeaker(){

    }

    override fun updatePosition(){

    }

    override fun isPlaying():Boolean{
        return false
    }

    override fun back() {
        viewModel.mediaController?.seekToPreviousMediaItem()
    }

    override fun forward() {
        viewModel.mediaController?.seekToNextMediaItem()
    }

    override fun setUri(url: Uri){

    }

    override fun setPosition(percent: Float) {

    }

    override fun setHeadphone(){

    }

    override fun getDuration(): Float {
       return 420.0f
    }

    override fun getType(): PLAYER {
       return PLAYER.APPLE
    }

    override fun release(){

    }
}
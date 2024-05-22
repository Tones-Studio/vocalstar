package de.tech41.tones.vocalstar.apple

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.player.IPlayer
import de.tech41.tones.vocalstar.player.PLAYER


class ApplePlayer(context : Context, viewModel: Model) : IPlayer {

    val context : Context = context
    var viewModel : Model = viewModel

    override fun setup(){
        if(viewModel.playerType == PLAYER.APPLE) {
            viewModel.title = viewModel.mediaController?.currentMediaItem?.mediaMetadata?.title.toString()
            viewModel.artist = viewModel.mediaController?.currentMediaItem?.mediaMetadata?.artist.toString()

           viewModel.mediaController?.currentMediaItem?.mediaMetadata?.artworkData?.let{
               viewModel.artworkBitmap =  BitmapFactory.decodeByteArray(it, 0, it.size) 
           }
        }
    }
    override fun play() {
        viewModel.mediaController?.play()
    }

    override fun setVolume(vol:Float){
        viewModel.mediaController?.volume = vol
    }

    override fun stop() {
        viewModel.mediaController?.stop()
    }

    override fun pause() {
        viewModel.mediaController?.pause()
    }

    override fun setSpeaker(){

    }

    override fun updatePosition(){

    }

    override fun isPlaying(): Boolean {
        if(viewModel.mediaController === null){
            return false
        }
        return  viewModel.mediaController!!.isPlaying
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
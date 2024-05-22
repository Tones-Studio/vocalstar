package de.tech41.tones.vocalstar.controls

import android.content.Context
import android.net.Uri
import de.tech41.tones.vocalstar.IPlayer
import de.tech41.tones.vocalstar.PLAYER

class ExternalPlayer(context : Context) : IPlayer {

    val context : Context = context

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

    }

    override fun forward() {

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
       return PLAYER.EXTERNAL
    }

    override fun release(){

    }
}
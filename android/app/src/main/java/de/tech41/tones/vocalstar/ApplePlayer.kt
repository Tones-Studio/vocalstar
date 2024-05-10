package de.tech41.tones.vocalstar

import android.util.Log

class ApplePlayer : IPlayer{

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

    override fun updatePosition(){

    }

    override fun isPlaying():Boolean{
        return false
    }

    override fun back() {

    }

    override fun forward() {

    }

    override fun setPosition(percent: Float) {

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
package de.tech41.tones.vocalstar.player

import android.net.Uri

enum class PLAYER {
    APPLE, FILE, SPOTIFY
}
    interface IPlayer {

        fun setup()
        fun play()
        fun stop()

        fun back()
        fun forward()

        fun pause()

        fun setPosition(percent:Float)

        fun getDuration():Float

        fun getType(): PLAYER

        fun setVolume(vol:Float)

        fun release()

        fun updatePosition()

        fun isPlaying():Boolean

        fun setSpeaker()

         fun setHeadphone()

         fun setUri(url: Uri)
    }

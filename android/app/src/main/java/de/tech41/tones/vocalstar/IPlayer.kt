package de.tech41.tones.vocalstar

import androidx.media3.common.Player

enum class PLAYER {
    APPLE, FILE
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
    }

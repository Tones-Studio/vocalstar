package de.tech41.tones.vocalstar

import androidx.media3.common.Player

enum class PLAYER {
    APPLE, FILE
}
    interface IPlayer {
        fun toggle()
        fun back()
        fun forward()

        fun setPosition(percent:Float)

        fun getDuration():Float

        fun getType(): PLAYER
    }

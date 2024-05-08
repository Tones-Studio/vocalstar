package de.tech41.tones.vocalstar

class FilePlayer  : IPlayer{
    override fun toggle() {

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
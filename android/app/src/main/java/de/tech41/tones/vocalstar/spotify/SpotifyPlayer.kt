package de.tech41.tones.vocalstar.spotify

import android.net.Uri
import de.tech41.tones.vocalstar.IPlayer
import de.tech41.tones.vocalstar.PLAYER

class SpotifyPlayer : IPlayer {
    override fun setup() {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun back() {
        TODO("Not yet implemented")
    }

    override fun forward() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun setPosition(percent: Float) {
        TODO("Not yet implemented")
    }

    override fun getDuration(): Float {
        TODO("Not yet implemented")
        return 0.0f;
    }

    override fun getType(): PLAYER {
        TODO("Not yet implemented")
        return PLAYER.SPOTIFY
    }

    override fun setVolume(vol: Float) {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    override fun updatePosition() {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
        return false
    }

    override fun setSpeaker() {
        TODO("Not yet implemented")
    }

    override fun setHeadphone() {
        TODO("Not yet implemented")
    }

    override fun setUri(url: Uri) {
        TODO("Not yet implemented")
    }
}
package de.tech41.tones.vocalstar.spotify

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.PlayerState
import de.tech41.tones.vocalstar.IPlayer
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.PLAYER
import java.util.concurrent.TimeUnit


class SpotifyPlayer(context : Context, viewModel : Model ) : IPlayer {
    val tag = "de.tech41.tones.vocalstar.spotify.SpotifyPlayer"
    val context : Context = context
    val viewModel : Model = viewModel
    private var _isPlaying = false

    override fun setup() {
        viewModel.spotifyAppRemote?.getPlayerApi()?.subscribeToPlayerState()?.setEventCallback(Subscription.EventCallback<PlayerState?>() {
            viewModel.title = it.track.name
            viewModel.artist = it.track.artist.name
            viewModel.duration = (it.track.duration).toFloat() / 1000.0f
            viewModel.spotifyAppRemote?.imagesApi?.getImage(it.track.imageUri, Image.Dimension.MEDIUM)?.setResultCallback { bitmap ->
                viewModel.artworkBitmap = bitmap
            }
            _isPlaying = !it.isPaused
        })
    }

    override fun play() {
        viewModel.spotifyAppRemote?.playerApi?.resume()
    }

    override fun stop() {
        viewModel.spotifyAppRemote?.playerApi?.pause()
    }

    override fun back() {
        viewModel.spotifyAppRemote?.playerApi?.skipPrevious()
    }

    override fun forward() {
        viewModel.spotifyAppRemote?.playerApi?.skipNext()
    }

    override fun pause() {
        viewModel.spotifyAppRemote?.playerApi?.pause()
    }

    override fun setPosition(percent: Float) {

    }

    override fun getDuration(): Float {
        return 0.0f
    }

    override fun getType(): PLAYER {
        return PLAYER.SPOTIFY
    }

    override fun setVolume(vol: Float) {

    }

    override fun release() {

    }

    override fun updatePosition() {

    }
    override fun isPlaying(): Boolean {
        return _isPlaying
    }
    override fun setSpeaker() {

    }

    override fun setHeadphone() {

    }

    override fun setUri(url: Uri) {

    }
}
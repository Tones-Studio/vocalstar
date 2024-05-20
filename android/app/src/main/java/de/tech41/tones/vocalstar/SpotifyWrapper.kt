package de.tech41.tones.vocalstar

import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote

// https://developer.spotify.com/documentation/android/tutorials/getting-started#prepare-your-environment
class SpotifyWrapper {
    private val clientId = "your_client_id"
    private val redirectUri = "http://box.tones.studio"
    private val spotifyAppRemote  : SpotifyAppRemote? = null
    fun authorize() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
    }
}
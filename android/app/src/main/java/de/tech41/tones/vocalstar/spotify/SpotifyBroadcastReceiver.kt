package de.tech41.tones.vocalstar.spotify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat

class SpotifyBroadcastReceiver : BroadcastReceiver(){

    fun register(context: Context){
        val filter = IntentFilter("com.spotify.music.active")
        val receiverFlags = ContextCompat.RECEIVER_EXPORTED
        ContextCompat.registerReceiver(context, this, filter, receiverFlags)
    }

    private val TAG = "SpotifyBroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
            }
        }
    }
}
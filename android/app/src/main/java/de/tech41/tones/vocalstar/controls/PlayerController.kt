package de.tech41.tones.vocalstar.controls

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import de.tech41.tones.vocalstar.VService


class PlayerController(context:Context) {
    var tag = PlayerController::class.java.name

    var context:Context = context

    val CMDTOGGLEPAUSE: String = "togglepause"
    val CMDPAUSE: String = "pause"
    val CMDPREVIOUS: String = "previous"
    val CMDNEXT: String = "next"
    val SERVICECMD: String = "com.android.music.musicservicecommand"
    val CMDNAME: String = "command"
    val CMDSTOP: String = "stop"

    @OptIn(UnstableApi::class)
    fun setup() : List<MediaController>{
        val mm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val mediaControllerList: List<MediaController> = mm.getActiveSessions(
            ComponentName(
                context,
                VService::class.java
            )
        )
        for(device in mediaControllerList){
            Log.d(tag, device.toString())
        }
        return mediaControllerList
    }

    fun control(context:Context, cmd:String){
        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        if (mAudioManager!!.isMusicActive) {
            val i = Intent(SERVICECMD)
            i.putExtra(CMDNAME, cmd)
            context.sendBroadcast(i)
        }
    }
}
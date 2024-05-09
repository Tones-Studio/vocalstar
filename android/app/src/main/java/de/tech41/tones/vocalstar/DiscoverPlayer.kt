package de.tech41.tones.vocalstar

import android.app.Notification.Builder
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.app.NotificationCompat
import androidx.media3.session.MediaButtonReceiver
import androidx.media3.session.MediaSession
import java.lang.reflect.Method

//     // https://code.tutsplus.com/background-audio-in-android-with-mediasessioncompat--cms-27030t
class DiscoverPlayer(context:Context){

    var context : Context = context

    var TAG = "DiscoverPlayer"

    fun openMusicPlayerChooser() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val method: Method =
                    StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                method.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC)
        intent.setAction(Intent.ACTION_VIEW)
      //  intent.setDataAndType(Uri.fromFile(null), "audio/*")
        context.startActivity( intent )
    }
    fun getPlayers(){
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1")
        intent.setData(uri)
        val playerList: List<ResolveInfo> =   context.getPackageManager().queryIntentActivities(intent, 0x00010000)
       var count = playerList.size
        Log.i(TAG,("Listing Players ==============="))
        playerList?.forEach {
          //  Log.i(TAG,it.serviceInfo.name)
        }
        Log.i(TAG,("End Listing Players ==============="))
    }

    fun start(){
        getPlayers()
        MediaSessionManager.OnActiveSessionsChangedListener {list: List<MediaController>? ->
            list?.forEach {
                Log.d(TAG,("Name:$it"))
            }
        }
    }
}
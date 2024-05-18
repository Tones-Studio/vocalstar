package de.tech41.tones.vocalstar.controls
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.media.MediaBrowserServiceCompat

/**
 * Implementation of [FindMediaAppsTask] that uses available implementations of
 * MediaBrowser to populate the list of apps.
 */
class FindMediaBrowserAppsTask constructor(
    context: Context, callback: AppListUpdatedCallback
) : FindMediaAppsTask(callback, sortAlphabetical = true) {

    val tag = "de.tech41.tones.vocalstar.controls.FindMediaBrowserAppsTask"

    private val packageManager: PackageManager = context.packageManager
    private val resources: Resources = context.resources

fun getPlayers():  List<ResolveInfo>{
    var intent =  Intent(Intent.ACTION_VIEW);
    var uri = Uri.withAppendedPath (MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
    intent.setData(uri);
    return packageManager.queryIntentActivities(intent, 0);
}

    /**
     * Finds installed packages that have registered a
     * [android.service.media.MediaBrowserService] or
     * [android.support.v4.media.MediaBrowserServiceCompat] service by
     * looking for packages that have services that respond to the
     * "android.media.browse.MediaBrowserService" action.
     */
    override val mediaApps: List<MediaAppDetails>
    get() {
        val mediaApps = ArrayList<MediaAppDetails>()
        val mediaBrowserIntent = Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE)
        val services = packageManager.queryIntentServices(
            mediaBrowserIntent,
            PackageManager.GET_RESOLVED_FILTER
        )

        if (services != null && !services.isEmpty()) {
            for (info in services) {
                val md = MediaAppDetails(
                    info.serviceInfo,
                    packageManager,
                    resources
                )
                Log.d(tag,md.appName)
                if(md.appName == "TikTok" || md.appName == "Bluetooth Audio" ) continue
                mediaApps.add(md)
            }
        }

        //val none: List<MediaAppDetails> = emptyList()
        //return none
        return mediaApps
    }
}
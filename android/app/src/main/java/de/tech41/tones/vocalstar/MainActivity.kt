package de.tech41.tones.vocalstar

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.DocumentsContract
import android.support.v4.media.session.MediaSessionCompat
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AccountsQueryParameters
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.LoginActivity
import de.tech41.tones.vocalstar.controls.FindMediaBrowserAppsTask
import de.tech41.tones.vocalstar.ui.theme.VocalstarTheme


private val AUDIO_EFFECT_REQUEST = 0
private var AUDIO_RECORD_REQUEST_CODE = 300

class MainActivity :ComponentActivity(){
    private val TAG: String = MainActivity::class.java.name
    private lateinit var viewModel: Model
    lateinit var audioManager: AudioManager
    private var isBound = false
    var discoverPlayer = DiscoverPlayer(this)
    private val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()

    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
        }

        override fun onStop() {
            unregisterReceiver(myNoisyAudioStreamReceiver)
        }
    }

    private val connection = object : ServiceConnection {
        @OptIn(UnstableApi::class)
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as VService.VServiceBinder
            viewModel.vService = binder.getService()
            isBound = true

            // Start Audio and open Mic
            viewModel.vService?.startAudio(viewModel)
            viewModel.isMuted = false
            viewModel.isRunning = true

            // get Media Players like Apple and Spotify
            viewModel.mediaAppBrowser = FindMediaBrowserAppsTask(viewModel.vService!!.applicationContext, viewModel.vService!!)
            viewModel.mediaAppBrowser?.execute()

            viewModel.mediaPlayers =  viewModel.mediaAppBrowser!!.mediaApps
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    init {
        instance = this
    }

    companion object {
        public var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Vocalstar"
            val descriptionText = "Vocalstar Audio Service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Vocalstar", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificationAccessGranted(context:Context):Boolean {
        var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationListenerAccessGranted(ComponentName(context, VService::class.java))
    }

    private fun openPermissions() {
        try {
            val settingsIntent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(settingsIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Model
        viewModel = ViewModelProvider(this).get(Model::class.java)
        viewModel.context =  applicationContext()
        val displayMetrics: DisplayMetrics = applicationContext.resources.displayMetrics
        viewModel.width = displayMetrics.widthPixels / displayMetrics.density
        viewModel.height = displayMetrics.heightPixels / displayMetrics.density

        // File root
        FileHelper(this, viewModel).makeAppFolder(true)
        discoverPlayer.start()

        // notification
        createNotificationChannel()

        // MIC Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            Log.d("permission", "have permission to record audio")
        }else{
            Toast.makeText(this, "Please allow Mic access", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_RECORD_REQUEST_CODE)
            Log.d("permission", "Don't have permission to record audio")
        }

        // Start Audio Service
        val intent = Intent(applicationContext(), VService::class.java)
       applicationContext.startForegroundService(intent)

        // the UI Root
        enableEdgeToEdge()
        setContent {
            VocalstarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TabScreen(viewModel)
                }
            }
        }
        viewModel.player.setup()
        if(!notificationAccessGranted(this)){
            openPermissions()
        }
        viewModel.spotifyBroadcastReceiver.register(this)
    }

    @OptIn(UnstableApi::class)
    override fun onStart(){
        super.onStart()

        Log.d(TAG,"Binding Service")
        // Bind to LocalService.
        Intent(this, VService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d(TAG,"MainActivity onCreate complete")

        if(isSpotifyInstalled()) {
            //connectSpotify()
           openSpotifySlow()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        viewModel.spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, VService::class.java)
        applicationContext.stopService(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
    val PICK_AUDIO_FILE = 2

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    @OptIn(UnstableApi::class)
    override fun onActivityResult( requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        // Check if result comes from the correct activity
        if (requestCode == LoginActivity.REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {}
                AuthorizationResponse.Type.ERROR -> {}
                else -> {}
            }
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                Log.d(TAG,uri.toString())
                viewModel.setFileTitle(uri)
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG,"File browsing cancelled")
        }
    }

    fun openDirectory() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/"))
        }
        startActivityForResult(intent, PICK_AUDIO_FILE)
    }
    /*
    ===================================================================================================================
    Private
    ===================================================================================================================
     */

    fun openSpotifySlow(){
        val spotifyContent = "https://open.spotify.com/album/4mtJHQbuhjHGmG2yaKemqw"
        val branchLink = ("https://spotify.link/content_linking?~campaign=" + getPackageName()).toString() + "&\$deeplink_path=" + spotifyContent + "&\$fallback_url=" + spotifyContent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(branchLink))
        startActivity(intent)
    }
    fun isSpotifyInstalled():Boolean{
        val pm = packageManager
        var isSpotifyInstalled = false
        try {
            pm.getPackageInfo("com.spotify.music", 0)
            isSpotifyInstalled = true
        } catch (e: PackageManager.NameNotFoundException) {
            isSpotifyInstalled = false
        }
        return isSpotifyInstalled
    }

    private fun connectSpotify(){
        val connectionParams = ConnectionParams.Builder(viewModel.clientId)
            .setRedirectUri(viewModel.redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                viewModel.spotifyAppRemote = appRemote
                android.util.Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                onSpotifyConnect()
            }

            override fun onFailure(throwable: Throwable) {
                android.util.Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }
    fun authorize() {
        val REQUEST_CODE = 1337
        val REDIRECT_URI = "https://vocalstar.app/spotify"
        val builder =  AuthorizationRequest.Builder(AccountsQueryParameters.CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }


    private fun onSpotifyConnect() {
        viewModel.spotifyAppRemote?.let {
            // Play a playlist

           // https://open.spotify.com/album/4mtJHQbuhjHGmG2yaKemqw
           // val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            val playlistURI = "spotify:album:4mtJHQbuhjHGmG2yaKemqw"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                android.util.Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }
}

fun openFileBrowser(){
    MainActivity.instance?.openDirectory()
}

private class BecomingNoisyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            // Pause the playback
        }
    }
}
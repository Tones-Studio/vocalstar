package de.tech41.tones.vocalstar

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.mediarouter.media.MediaRouter
import de.tech41.tones.vocalstar.ui.theme.VocalstarTheme

private val AUDIO_EFFECT_REQUEST = 0
private var AUDIO_RECORD_REQUEST_CODE = 300

class MainActivity :ComponentActivity()  { //ComponentActivity()
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
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as VService.VServiceBinder
            viewModel.vService = binder.getService()
            isBound = true

            // open Mic
            viewModel.vService?.startAudio(viewModel)
            viewModel.isMuted = false
            viewModel.isRunning = true
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
        viewModel.setPlayer(PLAYER.FILE)
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
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
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


    fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }
    /*
    ===================================================================================================================
    Private
    ===================================================================================================================
     */
}

fun openFileBrowser(){
    MainActivity.instance?.openDirectory()
}

@Composable
fun TabScreen(viewModel : Model) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sing", "In-Out", "About")
    Box(
        Modifier
            .safeDrawingPadding()
            .background(MaterialTheme.colorScheme.background)
            .clip(shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally ) {
            Row(modifier = Modifier
                .background(Color.Black)
                .padding(5.dp)){
                // Apple
                IconButton(onClick = { viewModel.setPlayer(PLAYER.APPLE) }, modifier = Modifier.size(24.dp)) {
                    Image( painterResource(R.drawable.apple_icon), contentDescription = "apple music")
                }
                if(viewModel.playerType == PLAYER.APPLE) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Green,
                                shape = CircleShape
                            )
                    )
                }else{
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Black,
                                shape = CircleShape
                            )
                    )
                }
                Spacer(Modifier.weight(0.5f))
                Image(painterResource(R.drawable.logoheader), contentDescription = "vocalstar", modifier = Modifier.height(30.dp))
                Spacer(Modifier.weight(0.5f))

                // File Player
                if(viewModel.playerType == PLAYER.FILE) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Green,
                                shape = CircleShape
                            )
                    )
                }else{
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Black,
                                shape = CircleShape
                            )
                    )
                }
                IconButton(onClick = {
                    if (viewModel.playerType == PLAYER.FILE){
                        // user wants to open file browser
                        openFileBrowser()
                    }else {
                        viewModel.setPlayer(PLAYER.FILE)
                    }
                   }, modifier = Modifier.size(30.dp)) {
                    Icon( painterResource(R.drawable.audio_file), contentDescription = "file player")
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
            when (tabIndex) {
                0 -> HomeScreen(viewModel)
                1 -> DeviceScreen(viewModel)
                2 -> AboutScreen(viewModel)
            }
            Spacer(modifier = Modifier.weight(1f))
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = {
                            when (index) {
                                0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                                1 -> Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                                2 -> Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}


private class BecomingNoisyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            // Pause the playback
        }
    }
}
package de.tech41.tones.vocalstar

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import de.tech41.tones.vocalstar.ui.theme.VocalstarTheme
import android.content.pm.ActivityInfo
private val AUDIO_EFFECT_REQUEST = 0
private var AUDIO_RECORD_REQUEST_CODE = 300

class MainActivity :ComponentActivity()  { //ComponentActivity()
    private val TAG: String = MainActivity::class.java.name
    private lateinit var viewModel: Model
    lateinit var audioManager: AudioManager
    private var isBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as VService.VServiceBinder
            viewModel.vService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
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
        viewModel = ViewModelProvider(this).get(Model::class.java)
        createNotificationChannel()

        // Get MIC Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            Log.d("permission", "have permission to record audio")
        }else{
            Toast.makeText(this, "Please allow Mic access", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_RECORD_REQUEST_CODE)
            Log.d("permission", "Don't have permission to record audio")
        }

        enableEdgeToEdge()
        setContent {
            VocalstarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TabScreen(viewModel)
                }
            }
        }

        Log.d(TAG,"Starting service")
        val intent = Intent(this, VService::class.java)
        applicationContext.startForegroundService(intent)

        Log.d(TAG,"Service Running - MainActivity onCreate complete")
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

/*
===================================================================================================================
Private
===================================================================================================================
 */

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
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
            Box ( contentAlignment = Alignment.Center,  modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(color = Color.Black))
                {
                /*
                Image(
                    painter = painterResource(id = R.drawable.logoheader), // TODO set Vocalstar logo
                    modifier = Modifier.height(20.dp),
                    contentDescription = "Vocalstar",
                    contentScale = ContentScale.FillHeight
                )*/
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



@Composable
fun MessageFromNativeLibrary(name: String) {
    Text(text = name)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VocalstarTheme {
        Greeting("Android")
    }
}
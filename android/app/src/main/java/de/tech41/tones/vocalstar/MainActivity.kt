package de.tech41.tones.vocalstar

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import de.tech41.tones.vocalstar.ui.theme.VocalstarTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: Model
    lateinit var audioManager: AudioManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(Model::class.java)

        // Query the AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRateStr = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        viewModel.sampleRate = sampleRateStr.toInt()
        print(viewModel.sampleRate)
        val framesPerBurstStr = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        viewModel.framesPerBurst = framesPerBurstStr.toInt()
        print( viewModel.framesPerBurst)
        var currentAudioMode = audioManager.ringerMode;
        print(currentAudioMode)

        enableEdgeToEdge()
        setContent {
            VocalstarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TabScreen(viewModel)
                }
            }
        }
    }
    companion object {
        init {
            System.loadLibrary("vocalstar")
        }
    }
}

@Composable
fun TabScreen(viewModel : Model) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sing", "In-Out", "About")
    Box(
        Modifier
            .safeDrawingPadding()
            .background( MaterialTheme.colorScheme.background)
            .clip(shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
            Box ( contentAlignment = Alignment.Center,  modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(color =  Color.Black))
                {
                Image(
                    painter = painterResource(id = R.drawable.logo_intern),
                    modifier = Modifier.height(20.dp),
                    contentDescription = "Vocalstar",
                    contentScale = ContentScale.FillHeight
                )
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
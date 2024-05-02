package de.tech41.tones.vocalstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import de.tech41.tones.vocalstar.ui.theme.VocalstarTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(Model::class.java)

        enableEdgeToEdge()
        setContent {
            VocalstarTheme {
                /*
                https://medium.com/@alessandro.lombardi.089/android-adding-native-code-to-an-empty-compose-activity-project-f6f23bffd6e3
                Surface(color = MaterialTheme.colors.background) {
                    MessageFromNativeLibrary(stringFromJNI())
                }

                 */

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    print(innerPadding)
                    TabScreen(viewModel)

                    /*
                    Greeting(
                        name = "Android " + stringFromJNI(),
                        modifier = Modifier.padding(innerPadding)
                    )
                     */
                }
            }
        }
    }

   external fun stringFromJNI(): String

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

    Box(Modifier.safeDrawingPadding()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            when (tabIndex) {
                0 -> HomeScreen(viewModel)
                1 -> DeviceScreen()
                2 -> AboutScreen()
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
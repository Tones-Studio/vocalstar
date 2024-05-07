package de.tech41.tones.vocalstar

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

private val TAG: String = "HomeScreen"
@Composable
fun ButtonStart(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(" Mic On")
    }
}
@Composable
fun ButtonStop(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Mic Off")
    }
}

@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(viewModel : Model) {
    Log.d(TAG,"HomeScreen")
    //val context = LocalContext.current
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painterResource(R.drawable.mic),
            "Mic",
            tint = if (viewModel.isMuted) Color.Red else Color.Green
        )
        Row {
            ButtonStart(onClick = {
                viewModel.vService?.startAudio(viewModel)
                viewModel.isMuted = false
                viewModel.isRunning = true
            })
            ButtonStop(onClick = {
                viewModel.vService?.stopAudio()
                viewModel.isMuted = true
                viewModel.isRunning = false
            })
        }
        MySlider(viewModel)
    }
}

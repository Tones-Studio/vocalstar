package de.tech41.tones.vocalstar

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.Log
import android.media.AudioManager
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

// https://medium.com/@dugguRK/kotlin-music-foreground-service-play-on-android-4b57b10fe583
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

@Composable
fun HomeScreen(viewModel : Model) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painterResource(R.drawable.mic),
            "Mic",
            tint = if (viewModel.isMuted) Color.Red else Color.Green
        )
        Row {
            ButtonStart(onClick = {
                viewModel.vService?.startAudio( viewModel.sampleRate, viewModel.framesPerBurst)
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

package de.tech41.tones.vocalstar.controls
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.R
import kotlin.math.log10


fun linearToDecibel(linear: Float): Float {
    val db: Float
    if (linear != 0.0f) db = 20.0f * log10(linear)
    else db = -144.0f // effectively minus infinity
    return db
}

@Composable
fun VolumeSlider( viewModel : Model) {
        Row() {
            IconButton(onClick = { viewModel.toggleIsSpeaker() }) {
                Icon(
                    if (viewModel.isSpeaker) painterResource(R.drawable.speaker_2) else painterResource(
                        R.drawable.headphones
                    ),
                    "Speaker",
                    tint =  Color(0xFFFFA500)
                )
            }

            Slider(
                value = viewModel.volume,
                onValueChange = { viewModel.putVolume(it) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                valueRange = 0f..1f
            )
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicVolumeSlider( viewModel : Model) {
    Row() {
        IconButton(onClick = { viewModel.toggleMute() }) {
            Icon(
                if (viewModel.isMuted) {painterResource(R.drawable.mic_off)} else {painterResource(R.drawable.mic)},
                "Mic",
                tint = if (viewModel.isMuted) Color(0xFFFFA500) else Color(0xFFFFA500)
            )
        }
        Slider(
            value = viewModel.micVolume,
            onValueChange = { viewModel.putMicVolume(it) },
            colors = SliderDefaults.colors(
                thumbColor = if (viewModel.micVolume < 1.0f) {MaterialTheme.colorScheme.secondary}else{ Color.Red},
                activeTrackColor =  if (viewModel.micVolume < 1.5f) {MaterialTheme.colorScheme.secondary}else{ Color.Red},
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 0f..2f,
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionSlider( viewModel : Model) {
    Slider(
        value = viewModel.positionPercent,
        onValueChange = { viewModel.seekPositionPercent(it) },
        onValueChangeFinished = { viewModel.seekDone() },
        colors = SliderDefaults.colors(
            thumbColor = Color(255, 165, 0, 127), //MaterialTheme.colorScheme.secondary,
            activeTrackColor = Color(255, 165, 0, 117), //MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        valueRange = 0f..100f,
    )
}
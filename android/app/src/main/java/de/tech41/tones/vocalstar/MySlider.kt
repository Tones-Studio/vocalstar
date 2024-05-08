package de.tech41.tones.vocalstar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.math.log10
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton

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
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                valueRange = 0f..1f,
            )
        }
}
@Composable
fun PositionSlider( viewModel : Model) {
        Slider(
            value = viewModel.positionPercent,
            onValueChange = { viewModel.putPositionPercent(it) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 0f..100f
        )
}
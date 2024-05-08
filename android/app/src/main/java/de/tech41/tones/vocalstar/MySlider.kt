package de.tech41.tones.vocalstar

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

fun linearToDecibel(linear: Float): Float {
    val db: Float
    if (linear != 0.0f) db = 20.0f * log10(linear)
    else db = -144.0f // effectively minus infinity
    return db
}

@Composable
fun VolumeSlider( viewModel : Model) {
    Column {
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
@Composable
fun MicVolumeSlider( viewModel : Model) {
    Column {
        Slider(
            value = viewModel.micVolume,
            onValueChange = { viewModel.putMicVolume(it) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 0f..1f
        )
    }
}
@Composable
fun PositionSlider( viewModel : Model) {
    Column {
        Slider(
            value = viewModel.position,
            onValueChange = { viewModel.putPosition(it) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 0f..1f
        )
    }
}
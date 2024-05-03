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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.log10

fun linearToDecibel(linear: Float): Float {
    val db: Float
    if (linear != 0.0f) db = 20.0f * log10(linear)
    else db = -144.0f // effectively minus infinity
    return db
}

@Composable
fun MySlider( viewModel : Model) {

    Column {
        Icon(
            painterResource(R.drawable.mic),
            "Mic",
            tint = Color.White
        )

        Slider(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = 270f
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxHeight,
                        )
                    )
                    layout(placeable.height, placeable.width) {
                        placeable.place(-placeable.width, 0)
                    }
                }
                .width(420.dp)
                .height(100.dp),
            value = viewModel.volume,
            onValueChange = { viewModel.putVol(it) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 0f..1f
        )
        Text(text = "%.2f Db".format(linearToDecibel(viewModel.volume)), color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(text = "%.2f   ".format(viewModel.volume), color = Color.White, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(20.dp))
        //Divider(color = Color.Blue, thickness = 1.dp)
        Icon(
            painterResource(R.drawable.speaker_2),
            "Mic",
            tint = Color.White
        )
        Switch(
            checked = viewModel.isSpeaker,
            onCheckedChange = {
                viewModel.putIsSpeaker(it)
            }
        )
        var text = "Speaker off"
        if (viewModel.isSpeaker){
            text = "Speaker on"
        }
        Text(text = text, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Input: " + viewModel.inputDevice, color = Color.White)
        Text(text = "Output: " + viewModel.outputDevice, color = Color.White)
    }
}
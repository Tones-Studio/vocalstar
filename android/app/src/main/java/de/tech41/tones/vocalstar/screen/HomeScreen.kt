package de.tech41.tones.vocalstar.screen

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import coil.compose.rememberAsyncImagePainter
import de.tech41.tones.vocalstar.CoverType
import de.tech41.tones.vocalstar.controls.MicVolumeSlider
import de.tech41.tones.vocalstar.Model
import de.tech41.tones.vocalstar.controls.PositionSlider
import de.tech41.tones.vocalstar.R
import de.tech41.tones.vocalstar.controls.VolumeSlider
import de.tech41.tones.vocalstar.player.PLAYER
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds


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

fun convertTime(sec: Float): String {
    var secTotal: Int = sec.roundToInt()
    var hours = secTotal / 3600
    var minutes = (secTotal % 3600) / 60
    var seconds = secTotal % 60
    if (hours > 0) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    if (minutes < 10) {
        return String.format("%01d:%02d", minutes, seconds)
    }
    return String.format("%02d:%02d", minutes, seconds)
}

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: Model) {
    Log.d(TAG, "HomeScreen")
    LaunchedEffect(Unit) {
        while (true) {
            delay(500.milliseconds)
            viewModel.updatePosition()
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Artist
        viewModel.artist?.let { Text(it, fontSize = 26.sp) }

        // Cover
        val imageModifier =
            Modifier
                .size(300.dp)
                .border(BorderStroke(1.dp, Color.Black))
                .background(Color.Black)
        when (viewModel.playerType) {
            PLAYER.SPOTIFY -> {
                viewModel.artworkBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = viewModel.title,
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                }
                if (viewModel.artworkBitmap == null) {
                    Image(
                        painter = painterResource(id = R.drawable.vocalstar),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                }
            }

            PLAYER.FILE -> {
                var res: Painter = painterResource(id = R.drawable.slow_cover)
                if (viewModel.coverType == CoverType.DEFAULT) {
                    res = painterResource(id = R.drawable.vocalstar)
                }
                Image(
                    painter = res,
                    contentDescription = "SLOW",
                    contentScale = ContentScale.Fit,
                    modifier = imageModifier
                )
            }

            PLAYER.APPLE -> {
                viewModel.artworkBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = viewModel.title,
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                }
            }
        }

        // Title
        viewModel.title?.let { Text(it, fontSize = 26.sp) }
        Row {
            Text(convertTime(viewModel.position))
            Spacer(Modifier.weight(1f))
            Text(convertTime(viewModel.duration - viewModel.position))
        }
        PositionSlider(viewModel)

        // Transport
        val imageBtn = Modifier
            .size(60.dp)
            .border(BorderStroke(0.dp, Color.White))
        Row {
            IconButton(onClick = { viewModel.back() }, modifier = Modifier.size(60.dp)) {
                Icon(
                    painterResource(R.drawable.rewind),
                    contentDescription = "rewind",
                    modifier = Modifier.fillMaxSize(1F),
                    tint = { Color.White })
            }
            Spacer(modifier = Modifier.width(50.dp))
            IconButton(onClick = { viewModel.toggle() }, Modifier.size(60.dp)) {
                if (viewModel.isPlaying) {
                    Icon(
                        painterResource(R.drawable.pause),
                        contentDescription = "Pause",
                        tint = { Color.White })
                } else {
                    Icon(
                        painterResource(R.drawable.play),
                        contentDescription = "Playing",
                        tint = { Color.White })
                }
            }
            Spacer(modifier = Modifier.width(60.dp))
            IconButton(
                onClick = { viewModel.forward() },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painterResource(R.drawable.fast_forward),
                    contentDescription = "forward",
                    tint = { Color.White },
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        VolumeSlider(viewModel)
        MicVolumeSlider(viewModel)
    }
}

package de.tech41.tones.vocalstar

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


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

fun convertTime(sec:Double):String{
    var secTotal :Int = sec.roundToInt()
    var hours = secTotal / 3600
    var minutes = (secTotal % 3600) / 60
    var seconds = secTotal % 60
    if (hours > 0){
       return  String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    if(minutes < 10) {
        return String.format("%01d:%02d", minutes, seconds)
    }
    return String.format("%02d:%02d", minutes, seconds)
}
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel : Model) {
    Log.d(TAG,"HomeScreen")

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(viewModel.artist,  fontSize = 26.sp)
        val imageModifier = Modifier
            .size(300.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .background(Color.Black)
        var res = painterResource(id = R.drawable.slow_cover)
        Image(
            painter = res,
            contentDescription = "SLOW",
            contentScale = ContentScale.Fit,
            modifier = imageModifier
        )
        Text(viewModel.title,  fontSize = 26.sp)
        Row{
            Text(convertTime(viewModel.startTime))
            Spacer(Modifier.weight(1f))
            Text(convertTime(viewModel.timeLeft))
        }
        PositionSlider(viewModel)
        Row{
            IconButton(onClick = { viewModel.back() }, modifier = Modifier.size(60.dp)) {
                Icon( painterResource(R.drawable.rewind), contentDescription = "Localized description", tint = { Color.White })
            }
            Spacer(modifier = Modifier.width(50.dp))
            IconButton(onClick = { viewModel.toggle()},modifier = Modifier.size(60.dp) ) {
                if (viewModel.isPlaying){
                    Icon(
                        painterResource(R.drawable.pause),
                        contentDescription = "Localized description",
                        tint = { Color.White })
                }else {
                    Icon(
                        painterResource(R.drawable.play),
                        contentDescription = "Localized description",
                        tint = { Color.White })
                }
            }
            Spacer(modifier = Modifier.width( 50.dp))
            IconButton(onClick = { viewModel.forward() },
            modifier = Modifier.size(60.dp)
            ) {
                Icon(painterResource(R.drawable.fast_forward), contentDescription = "forward", tint = { Color.White }, modifier = Modifier.size(60.dp))
            }
        }

        Text("Volume")
        VolumeSlider(viewModel)
        Text("Mic")
        Row{
            MicVolumeSlider(viewModel)
            Icon(
                painterResource(R.drawable.mic),
                "Mic",
                tint = if (viewModel.isMuted) Color.Red else Color.Green
            )
        }
    }
}

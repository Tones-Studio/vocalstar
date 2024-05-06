package de.tech41.tones.vocalstar

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

@Composable
fun ButtonStart(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Start")
    }
}
@Composable
fun ButtonStop(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Stop")
    }
}

@Composable
fun ButtonTap(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Tap")
    }
}


private val OBOE_API_AAUDIO = 0
private val OBOE_API_OPENSL_ES = 1

internal var mAAudioRecommended = true
var apiSelection: Int = OBOE_API_AAUDIO

fun EnableAudioApiUI(enable: Boolean) {
    if (apiSelection == OBOE_API_AAUDIO && !mAAudioRecommended) {
        apiSelection = OBOE_API_OPENSL_ES
    }
}
@Composable
fun HomeScreen(viewModel : Model) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            ButtonStart(onClick = {
                LiveEffectEngine.create()
                mAAudioRecommended = LiveEffectEngine.isAAudioRecommended()
                EnableAudioApiUI(true)
                LiveEffectEngine.setAPI(apiSelection)
                LiveEffectEngine.setDefaultStreamValues(context)
                LiveEffectEngine.setEffectOn(true)
            })
            ButtonStop(onClick = {
                stopEngine()
            })
        }
        ButtonTap(onClick = {
            viewModel.isTapDown =! viewModel.isTapDown
           tap(viewModel.isTapDown)
        })
        MySlider(viewModel)
    }
}

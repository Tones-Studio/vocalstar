package de.tech41.tones.vocalstar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.util.Log
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

private var mAAudioRecommended = true
private var apiSelection: Int = OBOE_API_AAUDIO

fun EnableAudioApiUI(enable: Boolean) {
    if (apiSelection == OBOE_API_AAUDIO && !mAAudioRecommended) {
        apiSelection = OBOE_API_OPENSL_ES
    }
}
@Composable
fun HomeScreen(viewModel : Model) {

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            ButtonStart(onClick = {
                LiveEffectEngine.create()
                mAAudioRecommended = LiveEffectEngine.isAAudioRecommended()
                EnableAudioApiUI(true)
                LiveEffectEngine.setAPI(apiSelection)


                /* original Engine
                var res = startEngine(0,0, 2)
                if(res == 0){
                    viewModel.isRunning = true
                    Log.d("de.tech41.tones.vocalstar.HomeScreen", "Audio Engine Started")
                }
                */
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

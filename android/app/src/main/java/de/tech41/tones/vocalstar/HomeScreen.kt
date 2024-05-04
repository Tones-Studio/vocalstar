package de.tech41.tones.vocalstar

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
fun HomeScreen(viewModel : Model) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row() {
            ButtonStart(onClick = {
                print("Start")

                var res = startEngine(0,0, 2)
                print(res)
            })
            ButtonStop(onClick = {
                stopEngine()
            })
        }
        MySlider(viewModel)
    }
}

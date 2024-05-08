package de.tech41.tones.vocalstar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log

private val TAG: String = "DeviceScreen"

@Composable
fun MySpinner(
    title:String,
    list: List<Pair<String, String>>,
    preselected: Pair<String, String>,
    onSelectionChanged: (selection: Pair<String, String>) -> Unit
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value

    Box {
        Column {
            OutlinedTextField(
                value = (selected.second),
                onValueChange = { },
                label = { Text(text = title) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true
            )
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { entry ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            selected = entry
                            expanded = false
                            onSelectionChanged(entry)
                        },
                        text = {
                            Text(
                                text = (entry.second),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .align(Alignment.Start))
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

fun getInDevice(viewModel : Model,  deviceId : String): Pair<String,String>{
    for (device in viewModel.devicesIn) {
        if (device.first == deviceId){
            return device
        }
    }
    return Pair("","")
}

fun getOutDevice(viewModel : Model,  deviceId : String): Pair<String,String>{
    for (device in viewModel.devicesOut) {
        if (device.first == deviceId){
            return device
        }
    }
    return Pair("","")
}

@Composable
fun DeviceScreen(viewModel : Model) {
    Log.d(TAG,"DeviceScreen renders")
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("In & Out",  fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            ButtonStart(onClick = {
                viewModel.vService?.startAudio(viewModel)
                viewModel.isMuted = false
                viewModel.isRunning = true
            })
            ButtonStop(onClick = {
                viewModel.vService?.stopAudio()
                viewModel.isMuted = true
                viewModel.isRunning = false
            })
        }
        Text(text = "%.2f Db".format(linearToDecibel(viewModel.volume)))
        Text(text = "%.2f   ".format(viewModel.volume), color = Color.White, fontFamily = FontFamily.Monospace)

        Text("Oboe Version: " + getVersions(), Modifier.height(20.dp),  fontSize = 17.sp)
        Text("AAudio Support: " + isAAudioSupported(),  fontSize = 17.sp)
        Text("Sample Rate: " + viewModel.sampleRate, fontSize = 17.sp)
        Text("Frames per burst: " +viewModel.framesPerBurst, fontSize = 17.sp)

        if (viewModel.isRunning){
            Text("Latency: " + viewModel.vService?.getLatency().toString(), fontSize = 17.sp)
            Text("Block Size : " + getBlockSize(), fontSize = 17.sp)
            Text("Channels : " + getChannels(), fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.devicesIn.count() > 0) {
            MySpinner(
                "Input Device",
                viewModel.devicesIn,
                preselected = getInDevice(viewModel, viewModel.deviceInSelected),
                onSelectionChanged = { selected ->
                    print("selected $selected")
                    LiveEffectEngine.setRecordingDeviceId(selected.first.toInt())
                    LiveEffectEngine.setEffectOn(false);
                    LiveEffectEngine.setEffectOn(true);
                }
            )
        }
        if (viewModel.devicesOut.count() > 0) {
            MySpinner(
                "Output Device",
                viewModel.devicesOut,
                preselected = getOutDevice(viewModel, viewModel.deviceOutSelected),
                onSelectionChanged = { selected ->
                    print("selected $selected")
                    LiveEffectEngine.setPlaybackDeviceId(selected.first.toInt())
                    LiveEffectEngine.setEffectOn(false);
                    LiveEffectEngine.setEffectOn(true);
                }
            )
        }
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
    }
}
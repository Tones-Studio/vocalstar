package de.tech41.tones.vocalstar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                        },
                        text = {
                            Text(
                                text = (entry.second),
                                modifier = Modifier.wrapContentWidth().align(Alignment.Start))
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

@Composable
fun DeviceScreen(viewModel : Model) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.material3.Text("In & Out",  fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))
        androidx.compose.material3.Text("Oboe Version: " + getVersions(), Modifier.height(20.dp),  fontSize = 17.sp)
        androidx.compose.material3.Text("AAudio Support: " + isAAudioSupported(),  fontSize = 17.sp)

        if (viewModel.isRunning){
        androidx.compose.material3.Text("Sample Rate: " + getSampleRate(), fontSize = 17.sp)
        androidx.compose.material3.Text("Block Size : " + getBlockSize(), fontSize = 17.sp)
        androidx.compose.material3.Text("Latency : " + getLatency() + " ms", fontSize = 17.sp)
        androidx.compose.material3.Text("Channels : " + getChannels() + " ms", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        val entry0 = Pair("0", "MIC")
        val entry1 = Pair("1", "Headphone")
        val entry2 = Pair("2", "USB")

        MySpinner(
            "Input Device",
            listOf(entry0, entry1, entry2),
            preselected = entry0,
            onSelectionChanged = { selected ->
                print("selected $selected")
            }
        )

        val entry4 = Pair("0", "Speaker")
        val entry5 = Pair("0", "Headphones")
        val entry6 = Pair("0", "USB")
        MySpinner(
            "Output Device",
            listOf(entry4, entry5, entry6),
            preselected = entry4,
            onSelectionChanged = { selected ->
                print("selected $selected")
            }
        )
    }
}
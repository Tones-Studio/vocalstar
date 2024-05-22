package de.tech41.tones.vocalstar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@RequiresApi(Build.VERSION_CODES.S)

private fun openFileBrowser(){
    MainActivity.instance?.openDirectory()
}

@Composable
fun TabScreen(viewModel : Model) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var showPlayerMenu by remember { mutableStateOf(false) }
    val tabs = listOf("Sing", "In-Out", "About")
    Box(
        Modifier
            .safeDrawingPadding()
            .background(MaterialTheme.colorScheme.background)
            .clip(shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp))) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally ) {
            Row(modifier = Modifier
                .background(Color.Black)
                .padding(5.dp)) {
                IconButton(onClick = {
                    if (viewModel.playerType == PLAYER.EXTERNAL || viewModel.selectedPlayer == null) {
                        showPlayerMenu = true
                    } else {
                        viewModel.setPlayer(PLAYER.EXTERNAL)
                    }
                }, modifier = Modifier.size(16.dp)) {
                    if (viewModel.selectedPlayer != null){
                        val imageModifier = Modifier.size(16.dp)
                        Image(bitmap = viewModel.selectedPlayer!!.icon.asImageBitmap(), contentDescription = viewModel.selectedPlayer!!.appName ,contentScale = ContentScale.FillHeight, modifier = imageModifier)
                    }else {
                        Image(painterResource(R.drawable.menu), contentDescription = "Player")
                    }
                }
                val mab = viewModel.mediaAppBrowser?.mediaApps
                if(showPlayerMenu) {
                    Popup(
                        offset = IntOffset(0, 70),
                        onDismissRequest = { showPlayerMenu = false },
                    )
                    {
                        Box (modifier = Modifier.background(Color.DarkGray,shape = RoundedCornerShape(1.dp) )){
                            Column {
                                if (mab.isNullOrEmpty()) {
                                    Text(buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = Color.Red)) {
                                            append("No music player started")
                                        }
                                    })
                                } else {
                                    for (player in viewModel.mediaPlayers!!) {
                                        TextButton(
                                            onClick = {
                                                viewModel.setPlayer(player)
                                                showPlayerMenu = false
                                            }
                                        )
                                        {
                                            Row {
                                                val imageModifier = Modifier.size(16.dp)
                                                Image(bitmap = player.icon.asImageBitmap(), contentDescription = player.appName ,contentScale = ContentScale.FillHeight, modifier = imageModifier)
                                                Text(buildAnnotatedString {
                                                    withStyle(style = SpanStyle(color = Color.White)) {
                                                        append(" " + player.appName)
                                                    }
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(viewModel.playerType == PLAYER.EXTERNAL) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Green,
                                shape = CircleShape
                            )
                    )
                }else{
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Black,
                                shape = CircleShape
                            )
                    )
                }
                Spacer(Modifier.weight(0.5f))
                Image(painterResource(R.drawable.logoheader), contentDescription = "vocalstar", modifier = Modifier.height(30.dp))
                Spacer(Modifier.weight(0.5f))

                // File Player
                if(viewModel.playerType == PLAYER.FILE) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Green,
                                shape = CircleShape
                            )
                    )
                }else{
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .border(
                                width = 5.dp,
                                color = Color.Black,
                                shape = CircleShape
                            )
                    )
                }
                IconButton(onClick = {
                    if (viewModel.playerType == PLAYER.FILE){
                        // user wants to open file browser
                        openFileBrowser()
                    }else {
                        viewModel.setPlayer(PLAYER.FILE)
                    }
                }, modifier = Modifier.size(30.dp)) {
                    Icon( painterResource(R.drawable.audio_file), contentDescription = "file player")
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
            when (tabIndex) {
                0 -> HomeScreen(viewModel)
                1 -> DeviceScreen(viewModel)
                2 -> AboutScreen(viewModel)
            }
            Spacer(modifier = Modifier.weight(1f))
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        icon = {
                            when (index) {
                                0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                                1 -> Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                                2 -> Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

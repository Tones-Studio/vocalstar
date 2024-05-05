package de.tech41.tones.vocalstar
// https://developer.android.com/jetpack/androidx/releases/media3#kts
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
0
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerExo(
    videoUrl: String
) {
    val context = LocalContext.current
    val player = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(videoUrl))
    }
    val playerView = PlayerView(context)
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }

    playerView.player = player
    player.setRepeatMode(Player.REPEAT_MODE_ALL);
    playerView.hideController();

    playerView.setShowFastForwardButton(false)
    playerView.setShowNextButton(false)
    playerView.setShowPreviousButton(false)
    playerView.setShowRewindButton(false)
    playerView.setShowPlayButtonIfPlaybackIsSuppressed(false)
    playerView.setShowShuffleButton(false)
    playerView.setShowVrButton(false)
    playerView.setShowSubtitleButton(false)
    playerView.controllerAutoShow = false
    playerView.controllerShowTimeoutMs = -1
    playerView.setControllerAnimationEnabled(false)
    playerView.useController = false
    playerView.setControllerAutoShow(false);

    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9F)
            .padding(20.dp)
            .scale(4.0f)
            .clip(RoundedCornerShape(0.dp)),
        factory = {
            playerView
        })
}
@Composable
fun AnnotatedClickableText() {
    val termsUrl = "https://tones.studio"
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)) {
            appendLink("Tones.studio", termsUrl)
        }
    }
    val context = LocalContext.current
    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.onLinkClick(offset) { link ->
                val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                defaultBrowser.data = Uri.parse(link)
                context.startActivity(defaultBrowser)
            }
        }
    )
}

fun AnnotatedString.Builder.appendLink(linkText: String, linkUrl: String) {
    pushStringAnnotation(tag = linkUrl, annotation = linkUrl)
    append(linkText)
    pop()
}

fun AnnotatedString.onLinkClick(offset: Int, onClick: (String) -> Unit) {
    getStringAnnotations(start = offset, end = offset).firstOrNull()?.let {
        onClick(it.item)
    }
}
@Composable
fun AboutScreen(viewModel : Model) {
    val context = LocalContext.current
    val path = "android.resource://" + context.packageName + "/" + R.raw.future
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        VideoPlayerExo(path)
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = "About", fontSize = 30.sp)
            Spacer(Modifier.height(20.dp))
            var t = "This is a very long Text. This is a very long Text.This is a very long Text.This is a very long Text.This is a very long Text.This is a very long Text."
            Text(text = t,  maxLines = 25,  fontSize = 21.sp, color = Color.White)

            AnnotatedClickableText()
        }

    }
}


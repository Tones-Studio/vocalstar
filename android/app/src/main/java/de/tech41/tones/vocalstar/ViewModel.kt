package de.tech41.tones.vocalstar

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.tech41.tones.vocalstar.apple.ApplePlayer
import de.tech41.tones.vocalstar.controls.FindMediaBrowserAppsTask
import de.tech41.tones.vocalstar.controls.MediaAppDetails
import de.tech41.tones.vocalstar.player.FilePlayer
import de.tech41.tones.vocalstar.player.IPlayer
import de.tech41.tones.vocalstar.player.PLAYER
import de.tech41.tones.vocalstar.spotify.SpotifyBroadcastReceiver
import de.tech41.tones.vocalstar.spotify.SpotifyPlayer
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import kotlin.math.exp
import kotlin.math.ln
enum class CoverType{
    SLOW,
    DEFAULT,
    DYNAMIC
}
class Model: ViewModel(){
    var context : Context = MainActivity.applicationContext()
    val tag = "de.tech41.tones.vocalstar.ViewModel"
    var coverType by mutableStateOf(CoverType.SLOW)
    val devicesIn: MutableList<Pair<String, String>> = ArrayList()
    val devicesOut: MutableList<Pair<String, String>> = ArrayList()
    val framesBurst: MutableList<Pair<String, String>> = ArrayList()
    var deviceInSelected = ""
    var deviceOutSelected = ""
    var frameBurstSelected = "192"
    var width = 0.0f
    var height = 0.0f
    var vService: VService? = null
    var isPlaying by mutableStateOf(false)
    var isSpeaker by mutableStateOf(false)
    var isMuted by mutableStateOf(true)
    var volume by mutableFloatStateOf(0.7f)
    var micVolume by mutableFloatStateOf(0.0f)
    var position by mutableFloatStateOf(0f)
    var positionPercent by mutableFloatStateOf(0f)
    var duration by mutableFloatStateOf(0.0f)
    var sampleRate : Int = 0
    var framesPerBurst = 0
    var isRunning = false //engine is started
    var title by mutableStateOf("SLOW")
    var cover by mutableStateOf("DEFAULT")
    var artist by mutableStateOf("NiniF")
    var album by mutableStateOf("")
    var player : IPlayer = FilePlayer(context, this)
    var playerType by mutableStateOf(PLAYER.FILE)
    var lastPlayerType by mutableStateOf(PLAYER.FILE)
    var playerUri : Uri? = null
    private var isSeeking = false
    var isMonoInput by mutableStateOf(false)
    var mediaAppBrowser : FindMediaBrowserAppsTask? = null
    var mediaPlayers : List<MediaAppDetails> = emptyList()
    var selectedPlayer : MediaAppDetails? = null
    var mediaController : MediaController? = null
    var artworkUri : Uri? by mutableStateOf(Uri.parse("https://tech41.de"))
    var artworkBitmap : Bitmap? by mutableStateOf(null)
    var spotifyBroadcastReceiver = SpotifyBroadcastReceiver()

    // Spotify
    var spotifyAppRemote  : SpotifyAppRemote? = null
    val clientId = "7ac580d73cb543de9fbe8eb777891bae"
    val redirectUri = "detech41tonesvocalstar://callback"
    private var playerBitmap :Bitmap? = null

    @OptIn(UnstableApi::class)
    fun setPlayer(p: MediaAppDetails){
        if (p.appName ==  "Spotify"){
            selectedPlayer = p
            setPlayer(PLAYER.SPOTIFY)
            return
        }
        selectedPlayer = p
        val sessionToken = SessionToken(context, p.componentName)
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        if(controllerFuture != null) {
            controllerFuture.addListener({
                try {
                    mediaController = controllerFuture.get()
                    when (p.appName) {
                        "Apple Music" -> {
                            setPlayer(PLAYER.APPLE)
                        }
                        "Deezer" -> player = ApplePlayer(context, this)
                        "Youtube" -> player = ApplePlayer(context, this)
                        "Another" -> player = ApplePlayer(context, this)
                    }

                    setPlayerData()
                    selectedPlayer = p
                    val imageModifier = Modifier.size(20.dp)
                } catch (e: Exception) {
                    Log.e(tag, e.toString())
                }
                // MediaController is available here with controllerFuture.get()
            }, MoreExecutors.directExecutor())
        }
    }

    private fun setPlayerData(){
        if (mediaController != null) {
            duration = mediaController!!.contentDuration.toFloat() / 1000.0f
            position = mediaController!!.contentPosition.toFloat() / 1000.0f
            positionPercent = position * 100 / duration
            isPlaying = mediaController!!.isPlaying
        }
    }

    fun stopPlayer(){

    }

    fun toggleIsMono(){
        isMonoInput = !isMonoInput
        LiveEffectEngine.setupDSP(sampleRate.toDouble(), framesPerBurst, isMonoInput)
    }

    private val mAudioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    init {
        framesBurst.add(Pair("64", "64"))
        framesBurst.add(Pair("128", "128"))
        framesBurst.add(Pair("192", "192"))
        framesBurst.add(Pair("256", "256"))
        framesBurst.add(Pair("320", "320"))
        framesBurst.add(Pair("384", "384"))
        framesBurst.add(Pair("448", "448"))
        framesBurst.add(Pair("512", "512"))
    }

    @OptIn(UnstableApi::class)
    fun setFileTitle(url:Uri){
        isPlaying = false
        player.stop()

        var retriever = MediaMetadataRetriever()
         retriever.setDataSource(context, url)
        var hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)

        // Check if this is an Audio File!!
        if(hasAudio != null && hasAudio == "yes") {
            playerUri = url
            player.setUri(url)

            // Get Meta Data
            var file = FileHelper(context, this).fileFromContentUri(url)
            var f = AudioFileIO.read(file)
            var t= f.getTag()
            var artists = t.getAll(FieldKey.ARTIST)
            if(artists.count() > 0){
                if(artists[0].count() > 0) {
                    artist = artists[0]
                }else{
                    artist = "?"
                }
            }else{
                artist = "?"
            }

            var titles = t.getAll(FieldKey.TITLE)
            if(titles.count() > 0){
                if(titles[0].count() > 0) {
                    title = titles[0]
                }else{
                    title = FileHelper(context, this).getPathEnd(url)
                }
            }else{
                title = FileHelper(context, this).getPathEnd(url)
            }

            var covers = t.artworkList
            if(covers.count() > 0){
                cover = covers[0].imageUrl
                coverType = CoverType.DYNAMIC
            }else{
                coverType = CoverType.DEFAULT
            }
        }
        retriever.release()
    }

    fun updatePosition(){
        if(!isSeeking) {
            if(playerType == PLAYER.FILE &&  player.isPlaying()) {
                player.updatePosition()
            }

            if(playerType == PLAYER.APPLE && mediaController != null) {
                setPlayerData()
            }
        }
    }

    fun setPlayer(type: PLAYER){
        if (type == PLAYER.FILE){
            player = FilePlayer(context, this)
        }
        if (type == PLAYER.APPLE){
            player = ApplePlayer(context, this)
        }
        if (type == PLAYER.SPOTIFY) {
            player = SpotifyPlayer(context, this)
        }
        if (playerType != PLAYER.FILE){
            lastPlayerType = playerType
        }
        playerType = type
        player.setup()
    }
    fun toggleIsSpeaker(){
        isSpeaker = !isSpeaker
        if(isSpeaker){
            putMicVolume(0f)
            player.setSpeaker()
        }else{
            player.setHeadphone()
        }
    }

    fun toggleMute(){
        isMuted = !isMuted
        LiveEffectEngine.setEffectOn(!isMuted)
    }
    fun back(){
        player.back()
    }
    fun toggle(){
        if (player == null){
            setPlayer(playerType)
        }
        if(!isPlaying) {
            player.play()
        }else{
            player.pause()
        }
        isPlaying = !isPlaying
    }
    fun forward(){
        player.forward()
    }

    fun putVolume(vol:Float){
        var maxVolume = 1.0f
        volume = vol
        player.setVolume((volume * volume))
    }

    fun logslider(position:Float): Float {
        // position will be between 0 and 100
        var minp = 0f;
        var maxp = 1f;

        // The result should be between 100 an 10000000
        var minv = ln(0F);
        var maxv = ln(1F);
        // calculate adjustment factor
        var scale = (maxv - minv) / (maxp - minp);
        return exp(minv + scale*(position-minp)).toFloat();
    }

    fun putMicVolume(vol:Float){
        micVolume = vol
        LiveEffectEngine.setMicVolume(vol)
    }

    fun seekPositionPercent(percent:Float){
        isSeeking = true
        positionPercent = percent
        position = percent * duration / 100.0f
        player.setPosition(percent)
    }
    fun seekDone() {
        player.setPosition(positionPercent)
        isSeeking = false
    }

    fun putMute(b:Boolean){
        isMuted = b
    }

    fun putIsSpeaker(b:Boolean){
        isSpeaker = b
    }
}
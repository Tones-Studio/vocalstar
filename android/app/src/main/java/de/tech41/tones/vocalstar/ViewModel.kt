package de.tech41.tones.vocalstar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.spotify.android.appremote.api.SpotifyAppRemote
import de.tech41.tones.vocalstar.controls.ExternalPlayer
import de.tech41.tones.vocalstar.controls.FindMediaBrowserAppsTask
import de.tech41.tones.vocalstar.controls.MediaAppDetails
import de.tech41.tones.vocalstar.spotify.SpotifyBroadcastReceiver
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.net.URL
import kotlin.math.exp
import kotlin.math.ln
enum class CoverType{
    SLOW,
    DEFAULT,
    DYNAMIC
}
class Model: ViewModel(){
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
    var context : Context = MainActivity.applicationContext()
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
    var player :IPlayer = FilePlayer(context, this)
    var playerType by mutableStateOf(PLAYER.EXTERNAL)
    var playerUri : Uri? = null
    var isSeeking = false
    var isMonoInput by mutableStateOf(false)
    var mediaAppBrowser : FindMediaBrowserAppsTask? = null
    var mediaPlayers : List<MediaAppDetails> = emptyList()
    var selectedPlayer : MediaAppDetails? = null
    var selectedPlayerIcon by mutableIntStateOf( R.drawable.menu)
    var mediaController : MediaController? = null
    var artworkUri by mutableStateOf(Uri.parse("https://tech41.de"))
    var bitmap:Bitmap? = null
    var spotifyBroadcastReceiver = SpotifyBroadcastReceiver()

    // Spotify
    var spotifyAppRemote  : SpotifyAppRemote? = null
    val clientId = "7ac580d73cb543de9fbe8eb777891bae"
    val redirectUri = "https://vocalstar.app/spotify"

    @OptIn(UnstableApi::class)
    fun setPlayer(player: MediaAppDetails){
        val sessionToken = SessionToken(context, player.componentName)
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        if(controllerFuture != null) {
            controllerFuture.addListener({
                try {
                    mediaController = controllerFuture.get()
                    when (player.appName) {
                        "Apple Music" -> selectedPlayerIcon = R.drawable.apple_icon
                        "Spotify" -> selectedPlayerIcon = R.drawable.spotify
                        "Deezer" -> selectedPlayerIcon = R.drawable.apple_icon
                        "Youtube" -> selectedPlayerIcon = R.drawable.apple_icon
                        "Another" -> selectedPlayerIcon = R.drawable.apple_icon
                    }
                    setPlayerData()
                    selectedPlayer = player
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
            var mediaItem = mediaController!!.currentMediaItem

            if (mediaItem != null) {
                title = mediaItem.mediaMetadata.title.toString()
                artist = mediaItem.mediaMetadata.artist.toString()
                val uri = mediaItem.mediaMetadata.artworkUri
                try {
                    if (uri != null) {
                        if (artworkUri.path != uri.path) {
                            artworkUri = uri
                            val url = URL(uri.path)
                            bitmap =
                                BitmapFactory.decodeStream(url.openConnection().getInputStream())
                        }
                    }
                }catch(e:Exception){

                }
            }
        }
    }

    fun stopPlayer(){
        //MediaController.releaseFuture(mediaController)
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
            if(playerType == PLAYER.FILE && player != null && player.isPlaying()) {
                player.updatePosition()
            }

            if(playerType == PLAYER.EXTERNAL && mediaController != null) {
                setPlayerData()
            }
        }
    }

    fun setPlayer(type:PLAYER){
        if (type == PLAYER.FILE){
            //player = FilePlayer(context, this)
            if(playerType== PLAYER.FILE){
                return
            }
            player = FilePlayer(context, this)
            playerType = PLAYER.FILE
            selectedPlayerIcon = R.drawable.menu
        }
        if (type == PLAYER.EXTERNAL){
            if(playerType== PLAYER.EXTERNAL){
                return
            }
            player = ExternalPlayer()
            playerType = PLAYER.EXTERNAL
        }
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
        if(playerType == PLAYER.FILE) {
            player.back()
        }else {
            mediaController?.seekToPreviousMediaItem()
            setPlayerData()
        }
    }
    fun toggle(){
        isPlaying = !isPlaying
        if (player == null){
            setPlayer(playerType)
        }
        if(isPlaying) {
            if(playerType == PLAYER.FILE) {
                player.play()
            }else {
                mediaController?.play()
            }
        }else{
            if(playerType == PLAYER.FILE) {
                player.pause()
            }else {
                mediaController?.pause()
            }
        }
    }
    fun forward(){
        if(playerType == PLAYER.FILE) {
            player.forward()
        }else {
            mediaController?.seekToNextMediaItem()
            setPlayerData()
        }
    }

    fun putVolume(vol:Float){
        var maxVolume = 1.0f
        volume = vol
       // val log1 = (ln(maxVolume - vol) / ln(maxVolume)).toFloat()
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
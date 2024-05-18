package de.tech41.tones.vocalstar

import android.content.Context
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.session.MediaController
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import de.tech41.tones.vocalstar.controls.ExternalPlayer
import de.tech41.tones.vocalstar.controls.FindMediaBrowserAppsTask
import kotlin.math.exp
import kotlin.math.ln


enum class CoverType{
    SLOW,
    DEFAULT,
    DYNAMIC
}
class Model: ViewModel() {
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
    var inputDevice by mutableStateOf("Headphone")
    var outputDevice by mutableStateOf("USB")
    var duration by mutableFloatStateOf(0.0f)
    var sampleRate : Int = 0
    var framesPerBurst = 0
    var isRunning = false //engine is started

    var title by mutableStateOf("SLOW")
    var cover by mutableStateOf("DEFAULT")
    var artist by mutableStateOf("NiniF")
    var album by mutableStateOf("")
    var player :IPlayer = FilePlayer2(context, this)
    var playerType by mutableStateOf(PLAYER.EXTERNAL)
    var playerUri : Uri? = null
    var isSeeking = false
    var isMonoInput by mutableStateOf(false)
    var mediaAppBrowser : FindMediaBrowserAppsTask? = null
    var playController = PlayerController(context)
    var mediaPlayers : List<MediaAppDetails>? = null
    var currentPlayer = 0

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
        if(hasAudio != null && hasAudio == "yes") {
            playerUri = url
            player.setUri(url)

           //var albumT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            var titleT = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            if(titleT != null){
                title = titleT!!
            }
            if (titleT == null) {
                title = url.path?.substring((url.path?.lastIndexOf("/") ?: 0) + 1).toString()
            }
            var hasCover = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE)
            if (hasCover != null) {
                var imgBytes =
                    retriever.embeddedPicture // TODO find an audio file example that has an image
            } else {
                coverType = CoverType.DEFAULT
            }
        }
        retriever.release()
    }

    fun updatePosition(){
        if(!isSeeking) {
            if(player != null && player.isPlaying()) {
                player.updatePosition()
            }
        }
    }

    fun setPlayer(type:PLAYER){
        if (type == PLAYER.FILE){
            //player = FilePlayer(context, this)
            if(playerType== PLAYER.FILE){
                return
            }
            player = FilePlayer2(context, this)
            playerType = PLAYER.FILE
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
       player.back()
    }
    fun toggle(){
        isPlaying = !isPlaying
        if (player == null){
            setPlayer(playerType)
        }
        if(isPlaying) {
            player.play()
        }else{
            player.pause()
        }
    }
    fun forward(){
        player.forward()
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
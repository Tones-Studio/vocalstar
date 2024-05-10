package de.tech41.tones.vocalstar

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.ln

class Model: ViewModel() {
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
    var volume by mutableFloatStateOf(0f)
    var micVolume by mutableFloatStateOf(0f)
    var position by mutableFloatStateOf(0f)
    var positionPercent by mutableFloatStateOf(0f)
    var inputDevice by mutableStateOf("Headphone")
    var outputDevice by mutableStateOf("USB")
    var duration by mutableFloatStateOf(0.0f)
    var sampleRate : Int = 0
    var framesPerBurst = 0
    var isRunning = false
    var title = "SLOW"
    var cover = "DEFAULT"
    var artist = "NiniF"
    lateinit var player : IPlayer
    var playerType by mutableStateOf(PLAYER.FILE)
    var playerUri : Uri? = null
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

    fun updatePosition(){
        player.updatePosition()
    }

    fun setPlayer(type:PLAYER){
        if (type == PLAYER.FILE){
            //player = FilePlayer(context, this)
            player = FilePlayer(context, this)
            playerType = PLAYER.FILE
        }
        if (type == PLAYER.APPLE){
            player = ApplePlayer()
            playerType = PLAYER.APPLE
        }
        player.setup()
    }
    fun toggleIsSpeaker(){
        isSpeaker = !isSpeaker
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
        val log1 = (ln(maxVolume - vol) / ln(maxVolume)).toFloat()
        player.setVolume(log1)
    }

    fun putMicVolume(vol:Float){
        micVolume = vol
    }

    fun putPositionPercent(percent:Float){
        positionPercent = percent
        position = percent * duration / 100.0f
    }

    fun putMute(b:Boolean){
        isMuted = b
    }

    fun putIsSpeaker(b:Boolean){
        isSpeaker = b
    }
}
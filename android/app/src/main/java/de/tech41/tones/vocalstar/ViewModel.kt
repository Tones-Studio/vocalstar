package de.tech41.tones.vocalstar

import android.media.AudioDeviceInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Model: ViewModel() {
    val devicesIn: MutableList<Pair<String, String>> = ArrayList()
    val devicesOut: MutableList<Pair<String, String>> = ArrayList()

    var deviceInSelected = ""
    var deviceOutSelected = ""

    var vService: VService? = null
    init {
        devicesIn.add(Pair("0", "MIC"))
        devicesIn.add(Pair("1", "Headphone"))
        devicesIn.add(Pair("2", "Headphone"))

        devicesOut.add(Pair("0", "MIC"))
        devicesOut.add(Pair("1", "Headphone"))
        devicesOut.add(Pair("2", "whatever"))
    }

    var isPlaying = false

    fun back(){

    }
    fun toggle(){

    }
    fun forward(){

    }


    var isSpeaker by mutableStateOf(false)
    var isMuted by mutableStateOf(true)
    var volume by mutableFloatStateOf(0f)
    var micVolume by mutableFloatStateOf(0f)
    var position by mutableFloatStateOf(0f)
    var inputDevice by mutableStateOf("Headphone")
    var outputDevice by mutableStateOf("USB")
    var sampleRate : Int = 0
    var framesPerBurst = 0
    var isRunning = false

    var title = "SLOW"
    var cover = "DEFAULT"
    var artist = "NiniF"

    var startTime = 0.0
    var timeLeft = 4.0 * 60.0


    fun putVolume(vol:Float){
        volume = vol
    }

    fun putMicVolume(vol:Float){
        micVolume = vol
    }

    fun putPosition(vol:Float){
        position = vol
    }

    fun putMute(b:Boolean){
        isMuted = b
    }

    fun putIsSpeaker(b:Boolean){
        isSpeaker = b
    }
}
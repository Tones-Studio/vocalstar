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
    var isSpeaker by mutableStateOf(false)
    var isMuted by mutableStateOf(false)
    var volume by mutableFloatStateOf(0f)
    var inputDevice by mutableStateOf("Headphone")
    var outputDevice by mutableStateOf("USB")

    var sampleRate : Int = 48000
    var framesPerBurst = 0

    var isRunning = false

    var devices = arrayOfNulls<AudioDeviceInfo>(0) as Array<AudioDeviceInfo>


    fun putVol(vol:Float){
        volume = vol
        updateVolume(vol)
    }

    fun putMute(b:Boolean){
        isMuted = b
       updateMute(b)
    }

    fun putIsSpeaker(b:Boolean){
        isSpeaker = b
        updateSpeaker(b)
    }
}
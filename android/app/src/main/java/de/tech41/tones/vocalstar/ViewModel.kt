package de.tech41.tones.vocalstar

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

    fun updateVolume(vol:Float){
        volume = vol
        c_updateVolume(vol)
    }

    fun updateMute(b:Boolean){
        isMuted = b
        c_updateMute(b)
    }

    fun updateIsSpeaker(b:Boolean){
        isSpeaker = b
        c_updateSpeaker(b)
    }

    external fun c_updateVolume(vol:Float)
    external fun c_updateMute(b:Boolean)
    external fun c_updateSpeaker(b:Boolean)
}
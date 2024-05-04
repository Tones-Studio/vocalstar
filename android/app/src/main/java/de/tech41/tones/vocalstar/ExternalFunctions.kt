package de.tech41.tones.vocalstar

/*
external fun startEngine(audioApi: Int, deviceId: Int, channelCount: Int): Int

external fun stopEngine(): Int
*/

external fun startEngine(): Int

external fun stringFromJNI(): String
external fun getVersions(): String
external fun isAAudioSupported(): Boolean
external fun getSampleRate(): Int
external fun getBlockSize(): Int
external fun getLatency(): Int
external fun getChannels(): Int
external fun updateVolume(vol:Float)
external fun updateMute(b:Boolean)
external fun updateSpeaker(b:Boolean)
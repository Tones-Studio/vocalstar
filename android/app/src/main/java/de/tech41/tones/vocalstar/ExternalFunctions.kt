package de.tech41.tones.vocalstar


external fun startEngine(audioApi: Int, deviceId: Int, channelCount: Int): Int
external fun stopEngine()

external fun stringFromJNI(): String
external fun getVersions(): String
external fun isAAudioSupported(): Boolean
external fun getSampleRate(): Int
external fun getBlockSize(): Int
external fun getLatency(): Double
external fun getChannels(): Int
external fun updateVolume(vol:Float)
external fun updateMute(b:Boolean)
external fun updateSpeaker(b:Boolean)
external fun tap(b:Boolean)
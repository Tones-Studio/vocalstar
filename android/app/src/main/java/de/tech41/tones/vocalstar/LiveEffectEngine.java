package de.tech41.tones.vocalstar;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
public enum LiveEffectEngine {
    INSTANCE;
    // Native methods
    static native boolean create();
    static native boolean isAAudioRecommended();
    static native boolean setAPI(int apiType);
    public static native boolean setEffectOn(boolean isEffectOn);
    public static native void setRecordingDeviceId(int deviceId);
    public  static native void setPlaybackDeviceId(int deviceId);

    static native void setBlocksize(int blockSize);
    static native int getRecordingDeviceId();
    static native int getPlaybackDeviceId();
    static native void delete();
    static native void native_setDefaultStreamValues(int defaultSampleRate, int defaultFramesPerBurst);
    static native void setMicVolume(float volume);

    static native void setupDSP(double sampleRate, int blockSize, boolean isMono);

    static void setDefaultStreamValues(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);
            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
        }
    }

    static void setDefaults(int sampleRate, int framesPerBurst){
        native_setDefaultStreamValues(sampleRate, framesPerBurst);
    }
}

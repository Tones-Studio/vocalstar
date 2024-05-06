#include <jni.h>

#include <oboe/Oboe.h>
#include "include/HelloOboeEngine.h"
#include "include/Engine.h"

#include "LiveEffectEngine.h"

static const int kOboeApiAAudio = 0;
static const int kOboeApiOpenSLES = 1;

static LiveEffectEngine *lengine = nullptr;

extern "C" {
static Engine engine;

/*
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_startEngine(JNIEnv *env, jclass, int audioApi, int deviceId, int channelCount) {
    return static_cast<jint>(HelloOboeEngine().start((oboe::AudioApi)audioApi, deviceId, channelCount));
}

JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_stopEngine(JNIEnv *env, jclass clazz) {
    return static_cast<jint>(HelloOboeEngine().stop());
}
 */

JNIEXPORT jstring JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_stringFromJNI(
        JNIEnv *env,
        jclass /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateVolume(JNIEnv *env, jclass thiz,
                                                                jfloat vol) {
    puts("update Volume");
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateMute(JNIEnv *env, jclass thiz,
                                                              jboolean b) {
    puts("update Mute");
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateSpeaker(JNIEnv *env, jclass thiz,
                                                                 jboolean b) {
    puts("update is Speaker");
}

JNIEXPORT jstring JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getVersions(JNIEnv *env, jclass clazz) {
    oboe::Version version = oboe::Version();
    return env->NewStringUTF(version.Text);
}

JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_isAAudioSupported(JNIEnv *env, jclass clazz) {
    return oboe::AudioStreamBuilder::isAAudioRecommended();
}

JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getSampleRate(JNIEnv *env, jclass clazz) {
    return 48000;
}

JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getBlockSize(JNIEnv *env, jclass clazz) {
    return 64;
}

JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getChannels(JNIEnv *env, jclass clazz) {
    return 2;
}

JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_startEngine(JNIEnv *env, jclass clazz,
                                                               jint audio_api, jint device_id,
                                                               jint channel_count) {
    return engine.start();
}

JNIEXPORT jdouble JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getLatency(JNIEnv *env, jclass clazz) {
    return engine.getLatency();
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_tap(JNIEnv *env, jclass clazz, jboolean b) {
    engine.tap(b);
}

JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_create(JNIEnv *env, jclass clazz) {
    if (lengine == nullptr) {
        lengine = new LiveEffectEngine();
    }
    return (lengine != nullptr) ? JNI_TRUE : JNI_FALSE;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_isAAudioRecommended(JNIEnv *env, jclass clazz) {
    if (lengine == nullptr) {
        LOGE(
                "Engine is null, you must call createEngine "
                "before calling this method");
        return JNI_FALSE;
    }
    return lengine->isAAudioRecommended() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setAPI(JNIEnv *env, jclass clazz, jint apiType) {
    if (lengine == nullptr) {
        LOGE("Engine is null, you must call createEngine "
                "before calling this method");
        return JNI_FALSE;
    }
    oboe::AudioApi audioApi;
    switch (apiType) {
        case kOboeApiAAudio:
            audioApi = oboe::AudioApi::AAudio;
            break;
        case kOboeApiOpenSLES:
            audioApi = oboe::AudioApi::OpenSLES;
            break;
        default:
            LOGE("Unknown API selection to setAPI() %d", apiType);
            return JNI_FALSE;
    }
    return lengine->setAudioApi(audioApi) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setEffectOn(JNIEnv *env, jclass clazz, jboolean is_effect_on) {
    if (lengine == nullptr) {
        LOGE(
                "Engine is null, you must call createEngine before calling this "
                "method");
        return JNI_FALSE;
    }
    return lengine->setEffectOn(is_effect_on) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setRecordingDeviceId(JNIEnv *env, jclass clazz,
                                                                     jint device_id) {
    if (lengine == nullptr) {
        LOGE(
                "Engine is null, you must call createEngine before calling this "
                "method");
        return;
    }
    lengine->setRecordingDeviceId(device_id);
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setPlaybackDeviceId(JNIEnv *env, jclass clazz,
                                                                    jint deviceId) {
    if (lengine == nullptr) {
        LOGE(
                "Engine is null, you must call createEngine before calling this "
                "method");
        return;
    }
    lengine->setPlaybackDeviceId(deviceId);
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_delete(JNIEnv *env, jclass clazz) {
    if (lengine) {
        lengine->setEffectOn(false);
        delete lengine;
        lengine = nullptr;
    }
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_native_1setDefaultStreamValues(JNIEnv *env,
                                                                               jclass clazz,
                                                                               jint sampleRate,
                                                                               jint framesPerBurst) {
    oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}
}
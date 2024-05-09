#include <jni.h>
#include <oboe/Oboe.h>
#include "include/Engine.h"
#include "LiveEffectEngine.h"
#include "include/logging_macros.h"

static const int kOboeApiAAudio = 0;
static const int kOboeApiOpenSLES = 1;
static LiveEffectEngine *lengine = nullptr;

extern "C" {
static Engine engine;
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
JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_create(JNIEnv *env, jclass clazz) {
    if (lengine == nullptr) {
        lengine = new LiveEffectEngine();
    }
    return (lengine != nullptr) ? JNI_TRUE : JNI_FALSE;
}
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
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setEffectOn(JNIEnv *env, jclass clazz,
                                                            jboolean is_effect_on) {
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
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_getRecordingDeviceId(JNIEnv *env, jclass clazz) {
    return lengine->getInDevice();
}
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_getPlaybackDeviceId(JNIEnv *env, jclass clazz) {
    return lengine->getOutDevice();
}
JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_LiveEffectEngine_setBlocksize(JNIEnv *env, jclass clazz,
                                                             jint block_size) {
    lengine->setBlockSize(block_size);
}
}
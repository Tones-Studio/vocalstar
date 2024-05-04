#include <jni.h>

#include <oboe/Oboe.h>
#include "include/HelloOboeEngine.h"
#include "include/Engine.h"

extern "C" {
//static HelloOboeEngine sEngine = HelloOboeEngine();
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
}
extern "C"
JNIEXPORT jdouble JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getLatency(JNIEnv *env, jclass clazz) {
    return engine.getLatency();
}
extern "C"
JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_tap(JNIEnv *env, jclass clazz, jboolean b) {
    engine.tap(b);
}
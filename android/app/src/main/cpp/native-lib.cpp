#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include "HelloOboeEngine.h"

extern "C" {
//static HelloOboeEngine sEngine;

JNIEXPORT jstring JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateVolume(JNIEnv *env, jobject thiz, jfloat vol) {
    puts("update Volume");
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateMute(JNIEnv *env, jobject thiz, jboolean b) {
    puts("update Mute");
}

JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_updateSpeaker(JNIEnv *env, jobject thiz, jboolean b) {
    puts("update is Speaker");
}

JNIEXPORT jstring JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getVersions(JNIEnv *env, jclass clazz) {
    oboe::Version version = oboe::Version();
    return env->NewStringUTF(version.Text);
}
}// extern "C"
extern "C"
JNIEXPORT jboolean JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_isAAudioSupported(JNIEnv *env, jclass clazz) {
    return oboe::AudioStreamBuilder::isAAudioRecommended();
}
extern "C"
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getSampleRate(JNIEnv *env, jclass clazz) {
    return 48000;
}
extern "C"
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getBlockSize(JNIEnv *env, jclass clazz) {
    return 64;
}
extern "C"
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getLatency(JNIEnv *env, jclass clazz) {
   return 12;
}

extern "C"
JNIEXPORT jint JNICALL
Java_de_tech41_tones_vocalstar_ExternalFunctionsKt_getChannels(JNIEnv *env, jclass clazz) {
    return 2;
}
#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include "HelloOboeEngine.h"

extern "C" {

//static HelloOboeEngine sEngine;


    JNIEXPORT jstring JNICALL
    Java_de_tech41_tones_vocalstar_MainActivity_stringFromJNI(
            JNIEnv* env,
            jobject /* this */) {
        std::string hello = "Hello from C++";
        return env->NewStringUTF(hello.c_str());
    }

    JNIEXPORT void JNICALL
    Java_de_tech41_tones_vocalstar_Model_c_1updateVolume(JNIEnv *env, jobject thiz, jfloat vol) {
        puts("update Volume");
    }

    JNIEXPORT void JNICALL
    Java_de_tech41_tones_vocalstar_Model_c_1updateMute(JNIEnv *env, jobject thiz, jboolean b) {
        puts("update Mute");
    }

    JNIEXPORT void JNICALL
    Java_de_tech41_tones_vocalstar_Model_c_1updateSpeaker(JNIEnv *env, jobject thiz, jboolean b) {
        puts("update is Speaker");
    }


    JNIEXPORT jstring JNICALL
    Java_de_tech41_tones_vocalstar_AboutScreenKt_getVersions(JNIEnv *env, jclass clazz) {
        oboe::Version version = oboe::Version();
        return env->NewStringUTF(version.Text);
    }

}
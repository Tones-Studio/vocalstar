#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_de_tech41_tones_vocalstar_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_Model_c_1updateVolume(JNIEnv *env, jobject thiz, jfloat vol) {
  puts("update Volume");
}
extern "C"
JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_Model_c_1updateMute(JNIEnv *env, jobject thiz, jboolean b) {
    puts("update Mute");
}
extern "C"
JNIEXPORT void JNICALL
Java_de_tech41_tones_vocalstar_Model_c_1updateSpeaker(JNIEnv *env, jobject thiz, jboolean b) {
    puts("update is Speaker");
}
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_de_tech41_tones_vocalstar_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Vocalstar";
    return env->NewStringUTF(hello.c_str());
}
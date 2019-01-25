#include <jni.h>
#include "cpu-features.h"

extern "C" {
JNIEXPORT char JNICALL Java_apc_ndk_Cpu_family(JNIEnv *, jobject) {
    return android_getCpuFamily();
}

JNIEXPORT int JNICALL Java_apc_ndk_Cpu_features(JNIEnv *, jobject) {
    return android_getCpuFeatures();
}
}
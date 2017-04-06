#include <jni.h>
#include "include/HelloJNI.h"
 
JNIEXPORT jstring JNICALL Java_com_arlahiru_tsart_HelloJni_getMessage
          (JNIEnv *env, jobject thisObj) {
   return (*env)->NewStringUTF(env, "Hello from native code!");
}

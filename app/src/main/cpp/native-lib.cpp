#include <jni.h>
#include <string>

extern "C" {

JNIEXPORT
jstring
Java_com_droid47_petfriend_app_AppKeyMaker_getClientId(JNIEnv *env, jobject object) {
    std::string clientId = "REMVNULUG3fJrZakPY0xyOhKI6N22XB8zOec2gu0vUa7z5batw";
    return env->NewStringUTF(clientId.c_str());
}

JNIEXPORT
jstring
Java_com_droid47_petfriend_app_AppKeyMaker_getClientSecret(JNIEnv *env, jobject object) {
    std::string clientSecret = "DAjRblc3McHcIQNwG3WWDDZoLQZs87wnWBrR9Nof";
    return env->NewStringUTF(clientSecret.c_str());
}

}

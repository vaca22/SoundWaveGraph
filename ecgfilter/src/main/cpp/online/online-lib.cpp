//
// Created by wangjiang on 2019/4/8.
//

#include <jni.h>
#include <unistd.h>
#include <sys/stat.h>
#include <ctime>
#include <cstdlib>
#include <fcntl.h>

#include <string>
#include <deque>
#include <cstdlib>
#include <cstring>

#include "streamswtqua.h"
#include "commalgorithm.h"
#include "swt.h"
#include <android/log.h>
#include <cassert>

static StreamSwtQua streamSwtQua;



JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    streamSwtQua = StreamSwtQua();

    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_viatom_ecgfilter_EcgFilter_filter(JNIEnv *env, jobject thiz, jdouble f, jboolean reset) {

    if (reset) {
        streamSwtQua.ResetMe();
        return (*env).NewDoubleArray(0);
    }


    deque<double> outputPoints;
    streamSwtQua.GetEcgData(f, outputPoints);
    double *arrays = 0;
    if (outputPoints.empty()) {
        arrays = (double *) malloc(sizeof(double) * 7);
        memset(arrays, '\0', sizeof(arrays));
    } else {
        arrays = (double *) malloc(sizeof(double) * outputPoints.size());
        for (int i = 0; i < outputPoints.size(); i++) {
            arrays[i] = outputPoints[i];
        }
    }

    long length = outputPoints.size();

    auto size = (jsize) outputPoints.size();
    jdoubleArray result = (*env).NewDoubleArray(size);
    (*env).SetDoubleArrayRegion(result, 0, size, arrays);

    return result;
}
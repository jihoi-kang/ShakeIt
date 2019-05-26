#include <jni.h>
#include "com_example_kjh_shakeit_main_more_ImageFilterActivity.h"
#include "../../../../opencv/native/jni/include/opencv2/core/hal/interface.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" {

    JNIEXPORT void JNICALL
    Java_com_example_kjh_shakeit_main_more_ImageFilterActivity_ConvertRGBtoGray(
            JNIEnv *env,
            jobject instance,
            jlong matAddrInput,
            jlong matAddrResult) {

        Mat &matInput = *(Mat *) matAddrInput;
        Mat &matResult = *(Mat *) matAddrResult;

        // 흑백
        cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

    }

    JNIEXPORT void JNICALL
    Java_com_example_kjh_shakeit_main_more_ImageFilterActivity_ConvertToBlur(
            JNIEnv *env,
            jobject instance,
            jlong matAddrInput,
            jlong matAddrResult) {

        Mat &matInput = *(Mat *) matAddrInput;
        Mat &matResult = *(Mat *) matAddrResult;

        // 블러
        for ( int i = 1; i < 31; i = i + 2 )
            GaussianBlur( matInput, matResult, Size( i, i ), 0, 0 );

    }

    JNIEXPORT void JNICALL
    Java_com_example_kjh_shakeit_main_more_ImageFilterActivity_ConvertToContrast(
            JNIEnv *env,
            jobject instance,
            jlong matAddrInput,
            jlong matAddrResult) {

        Mat &matInput = *(Mat *) matAddrInput;
        Mat &matResult = *(Mat *) matAddrResult;

        // 대비 UP
        matInput.convertTo(matResult, -1, 2, 0);
    }

    JNIEXPORT void JNICALL Java_com_example_kjh_shakeit_main_more_ImageFilterActivity_ConvertToBrightness(
            JNIEnv *env,
            jobject instance,
            jlong matAddrInput,
            jlong matAddrResult) {

        Mat &matInput = *(Mat *) matAddrInput;
        Mat &matResult = *(Mat *) matAddrResult;

        // 밝기 UP
        matInput.convertTo(matResult, -1, 1, 20);
    }
}
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=off
#OPENCV_LIB_TYPE:=SHARED
include D:\home\Projekte\OpenCV-2.4.10-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES  := CLEENR_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := CLEEN_R

include $(BUILD_SHARED_LIBRARY)

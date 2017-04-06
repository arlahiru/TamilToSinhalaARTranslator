LOCAL_PATH := $(call my-dir)

# static library info
LOCAL_MODULE := libccv
LOCAL_SRC_FILES := ../prebuild/libccv.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libpng
LOCAL_SRC_FILES := ../prebuild/libpng.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjpeg
LOCAL_SRC_FILES := ../prebuild/libjpeg.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)

# wrapper info
include $(CLEAR_VARS)
LOCAL_C_INCLUDES += ../prebuild/include
LOCAL_MODULE    := ccv-wrapper
LOCAL_SRC_FILES := ccv-wrapper.c
LOCAL_STATIC_LIBRARIES := libccv
#LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
# common
#LOCAL_SHARED_LIBRARIES:= libpngt
LOCAL_STATIC_LIBRARIES := android_native_app_glue libccv libpng libjpeg
#Zlib
LOCAL_LDLIBS    += -lz 
include $(BUILD_SHARED_LIBRARY)

# Just build the Android.mk files in the subdirs
#include $(call all-subdir-makefiles)

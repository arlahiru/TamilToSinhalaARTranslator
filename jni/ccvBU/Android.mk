LOCAL_PATH := $(call my-dir)
#TESSERACT_PATH := $(LOCAL_PATH)/com_googlecode_tesseract_android/src
#LEPTONICA_PATH := $(LOCAL_PATH)/com_googlecode_leptonica_android/src

# static library info
LOCAL_MODULE := libccv
LOCAL_SRC_FILES := ../prebuild/libccv.a
#LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)

# wrapper info
include $(CLEAR_VARS)
#LOCAL_C_INCLUDES += ../prebuild/include
LOCAL_MODULE    := ccv-wrapper
LOCAL_SRC_FILES := ccv-wrapper.c
LOCAL_STATIC_LIBRARIES := libccv
include $(BUILD_SHARED_LIBRARY)

# Just build the Android.mk files in the subdirs
include $(call all-subdir-makefiles)

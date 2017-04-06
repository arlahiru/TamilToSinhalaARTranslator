/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
//#include <ccv.h>
#include <sys/time.h>
#include <ctype.h>

/*const ccv_swt_param_t ccv_swt_nobreakdown_params = {
	.interval = 1,
	.same_word_thresh = { 0.1, 0.8 },
	.min_neighbors = 1,
	.scale_invariant = 0,
	.size = 3,
	.low_thresh = 124,
	.high_thresh = 204,
	.max_height = 300,
	.min_height = 8,
	.min_area = 38,
	.letter_occlude_thresh = 3,
	.aspect_ratio = 8,
	.std_ratio = 0.83,
	.thickness_ratio = 1.5,
	.height_ratio = 1.7,
	.intensity_thresh = 31,
	.distance_ratio = 2.9,
	.intersect_ratio = 1.3,
	.letter_thresh = 3,
	.elongate_ratio = 1.9,
	.breakdown = 0,
	.breakdown_ratio = 1.0,
};
 */
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
JNIEXPORT jobjectArray JNICALL Java_com_arlahiru_tsart_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
  /*ccv_enable_default_cache();
  char* image_path = "/home/lahiru/ccv/bin/notice.jpg";
  ccv_dense_matrix_t* image = 0;
  ccv_read(image_path, &image, CCV_IO_GRAY | CCV_IO_ANY_FILE);
  assert(image != 0);
  ccv_array_t* words = ccv_swt_detect_words(image, ccv_swt_nobreakdown_params);
  jobjectArray ret= (*env)->NewObjectArray(env,words->rnum,(*env)->FindClass(env,"java/lang/String"),NULL);	
  if (words) {
    
    int i;
    for (i = 0; i < words->rnum; i++)
      {
	ccv_rect_t* rect = (ccv_rect_t*)ccv_array_get(words, i);
	char* rect_value;
	sprintf(rect_value,"%d,%d,%d,%d", rect->x, rect->y, rect->width, rect->height);
	(*env)->SetObjectArrayElement(env,ret,i,(*env)->NewStringUTF(env,rect_value));

      }
  }
  ccv_matrix_free(image);*/
  return NULL;
}

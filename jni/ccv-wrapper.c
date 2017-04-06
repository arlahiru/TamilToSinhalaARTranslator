#include <string.h>
#include <jni.h>
#include <ccv.h>
#include <include/ccv-wrapper.h>
#include <sys/time.h>
#include <ctype.h>

const ccv_swt_param_t ccv_swt_nobreakdown_params = {
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
 
JNIEXPORT jobjectArray JNICALL Java_com_arlahiru_tsart_MainActivity_ccvSwtDetectwords( JNIEnv* env,
                                                  jobject thiz, jstring imgpath)
{
  ccv_enable_default_cache();
  const char* image_path = (*env)->GetStringUTFChars(env, imgpath, 0);
  ccv_dense_matrix_t* image = 0;
  ccv_read(image_path, &image, CCV_IO_GRAY | CCV_IO_ANY_FILE); 
  //assert(image == 0);
  ccv_array_t* words = ccv_swt_detect_words(image, ccv_swt_nobreakdown_params);
  jobjectArray ret= (*env)->NewObjectArray(env,words->rnum,(*env)->FindClass(env,"java/lang/String"),NULL);	
  if (words) {
    
    int i;
    for (i = 0; i < words->rnum; i++)
      {
	ccv_rect_t* rect = (ccv_rect_t*)ccv_array_get(words, i);
	//max value=>9999,9999,9999,9999
	char* rect_value[20];
	//printf("%d %d %d %d\n", rect->x, rect->y, rect->width, rect->height);
	sprintf(rect_value,"%d,%d,%d,%d", rect->x, rect->y, rect->width, rect->height);
	(*env)->SetObjectArrayElement(env,ret,i,(*env)->NewStringUTF(env,rect_value));

      }
  }

  ccv_matrix_free(image);
  return ret;
}
